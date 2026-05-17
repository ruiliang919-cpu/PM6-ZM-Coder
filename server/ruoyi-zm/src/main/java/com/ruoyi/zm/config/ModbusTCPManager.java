package com.ruoyi.zm.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.BeanCopyUtils;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevFaultRecord;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.DevFaultRecordVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevFaultRecordMapper;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ReadCoilsRequest;
import com.serotonin.modbus4j.msg.ReadCoilsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModbusTCPManager {
    @Value("${init.modbusInit:false}")
    public boolean flag;
    private final ModbusFactory modbusFactory = new ModbusFactory();
    private final DevBaseDeviceMapper devBaseDeviceMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, boolean[]> boolArrayRedisTemplate;
    private final DevFaultRecordMapper faultRecordMapper;
    private final ModbusProperties modbusProperties;

    private final ConcurrentHashMap<Integer, ModbusMaster> masterPool = new ConcurrentHashMap<>();
    /** 每个 slaveId 独立的锁对象，避免在 ConcurrentHashMap.compute() 内执行耗时 I/O */
    private final ConcurrentHashMap<Integer, Object> slaveIdLocks = new ConcurrentHashMap<>();

    public ModbusMaster getSlave(int slaveId) {
        // 使用 per-key 锁代替 compute()，避免在 ConcurrentHashMap 内部持锁期间执行阻塞网络 I/O
        Object lock = slaveIdLocks.computeIfAbsent(slaveId, k -> new Object());
        synchronized (lock) {
            ModbusMaster existing = masterPool.get(slaveId);
            if (existing != null && isMasterValid(slaveId, existing)) {
                return existing;
            }
            if (existing != null) {
                destroyConnection(existing);
            }
            ModbusMaster newMaster = createNewMaster(slaveId);
            if (newMaster != null) {
                masterPool.put(slaveId, newMaster);
            } else {
                masterPool.remove(slaveId);
            }
            return newMaster;
        }
    }

    private ModbusMaster createNewMaster(int slaveId) {
        DevBaseDeviceTCPVo modbusInfo = getOneByCache(slaveId);
        if (modbusInfo == null) {
            log.error("创建Modbus连接失败, 未找到设备缓存信息, slaveId={}", slaveId);
            return null;
        }

        try {
            IpParameters params = new IpParameters();
            params.setHost(modbusInfo.getIp());
            params.setPort(modbusInfo.getPort());
            params.setEncapsulated(false);
            ModbusMaster master = modbusFactory.createTcpMaster(params, true);
            master.setTimeout(modbusProperties.getTimeout());
            master.setRetries(modbusProperties.getRetries());
            master.init();
            ReadCoilsRequest request = new ReadCoilsRequest(1, 0, 1);
            master.send(request);
            return master;
        } catch (Exception e) {
            log.error("创建Modbus连接失败, slaveId={}, ip={}", slaveId, modbusInfo.getIp(), e);
            return null;
        }
    }

    private boolean isMasterValid(int slaveId, ModbusMaster master) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                ReadCoilsRequest request = new ReadCoilsRequest(1, 0, 1);
                ReadCoilsResponse response = (ReadCoilsResponse) master.send(request);
                if (response != null) {
                    return true;
                }
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    log.error("Modbus连接验证失败(重试{}次), slaveId={}", maxRetries, slaveId, e);
                } else {
                    log.warn("Modbus连接验证第{}次失败, slaveId={}, 将重试", i + 1, slaveId);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 仅销毁连接，不操作 masterPool（供 compute lambda 内部使用）
     */
    private void destroyConnection(ModbusMaster master) {
        try {
            if (master != null) master.destroy();
        } catch (Exception e) {
            log.error("释放Modbus连接失败", e);
        }
    }

    /**
     * 销毁连接并从 masterPool 移除（供 compute 外部使用）
     */
    private void safeDestroy(int slaveId, ModbusMaster master) {
        destroyConnection(master);
        masterPool.remove(slaveId);
    }

//    @Scheduled(fixedDelay = 5000) // 调整为5秒一次
    public void checkConnect() {
        List<DevBaseDevice> devices = devBaseDeviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>()
            .select(DevBaseDevice::getDeviceNo));
        if (ObjectUtils.isEmpty(devices)) return;
        devices.forEach(d -> createModbusMaster(Math.toIntExact(d.getDeviceNo())));
    }

    private void createModbusMaster(int slaveId) {
        DevBaseDeviceTCPVo modbusInfo = getOneByCache(slaveId);
        try {
            ModbusMaster slave = getSlave(slaveId);
            if (slave == null) throw new RuntimeException();
            // 写入设备状态
            if (modbusInfo.getOnlineStatus() == 0) {
                modbusInfo.setOnlineStatus(1); // 在线
                modbusInfo.setLastTime(new Date());
                // 重新写入到缓存中
                redisTemplate.opsForValue().set("zm:create-tcp:" + slaveId, modbusInfo, modbusProperties.getConnectionTtl(), TimeUnit.SECONDS);
                // log.info("连接Modbus成功，设备IP：{} 连接设备主机号：{}", modbusInfo.getIp(), modbusInfo.getId());
                redisTemplate.delete("zm:order:" + modbusInfo.getId() + ":1:0x0000");
                redisTemplate.delete("zm:fault:" + modbusInfo.getId() + ":0xAAAA");
                redisTemplate.convertAndSend("Recover", slaveId + "~");
            }
        } catch (Exception e) {
            log.error("Modbus连接异常, slaveId={}", slaveId, e);
            try {
                boolean[] initData = new boolean[845];
                boolean[] sourceData = boolArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:1:" + modbusInfo.getIp() + ":" + modbusInfo.getId() + ":0x0000");
                if (sourceData != null) {
                    sourceData[sourceData.length - 1] = true;
                    initData = sourceData;
                }
                boolArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:1:" + modbusInfo.getIp() + ":" + modbusInfo.getId() + ":0x0000", initData);
                redisTemplate.delete("zm:order:" + modbusInfo.getId() + ":1:0x0000");
                if (modbusInfo.getOnlineStatus() == 1) {
                    modbusInfo.setOnlineStatus(0); // 断开
                    redisTemplate.opsForValue().set("zm:create-tcp:" + slaveId, modbusInfo, modbusProperties.getConnectionTtl(), TimeUnit.SECONDS);
                    // 清除断线设备的告警信息（使用SCAN替代KEYS，避免阻塞Redis）
                    Set<String> keysToDelete = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                        Set<String> keys = new HashSet<>();
                        ScanOptions options = ScanOptions.scanOptions()
                            .match("zm:fault:" + modbusInfo.getId() +":*")
                            .count(100)
                            .build();
                        try (Cursor<byte[]> cursor = connection.scan(options)) {
                            while (cursor.hasNext()) {
                                keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                            }
                        }
                        return keys;
                    });
                    if (keysToDelete != null && !keysToDelete.isEmpty()) {
                        redisTemplate.delete(new ArrayList<>(keysToDelete));
                    }
                    boolArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:1:" + modbusInfo.getIp() + ":" + modbusInfo.getId() + ":0x0000", initData);
                    DevFaultRecordVo vo = new DevFaultRecordVo();
                    vo.setDeviceId(Math.toIntExact(modbusInfo.getId()));
                    vo.setName(devBaseDeviceMapper.selectOne(new LambdaQueryWrapper<DevBaseDevice>()
                        .select(DevBaseDevice::getDeviceName)
                        .eq(DevBaseDevice::getDeviceNo, modbusInfo.getId())
                    ).getDeviceName());
                    vo.setMessage("设备离线");
                    vo.setStime(System.currentTimeMillis());
                    redisTemplate.opsForValue().set("zm:fault:" + modbusInfo.getId() + ":0xAAAA", vo);
                    DevFaultRecord recordFault = new DevFaultRecord();
                    recordFault.setDeviceId(Math.toIntExact(modbusInfo.getId()));
                    recordFault.setMessage(vo.getMessage());
                    recordFault.setStime(vo.getStime() / 1000);
                    recordFault.setShowType(1);
                    recordFault.setType(1);
                    faultRecordMapper.insert(recordFault);
                }
            } catch (Exception e1) {
                log.error("Modbus断线处理失败, slaveId={}", slaveId, e1);
            }
        }
    }

    public DevBaseDeviceTCPVo getOneByCache(int slaveId) {
        DevBaseDeviceTCPVo modbusInfo = (DevBaseDeviceTCPVo) redisTemplate.opsForValue().get("zm:create-tcp:" + slaveId);
        if (modbusInfo == null) {
            LambdaQueryWrapper<DevBaseDevice> queryWrapper = new LambdaQueryWrapper<>();
            DevBaseDevice source = devBaseDeviceMapper.selectOne(queryWrapper.eq(DevBaseDevice::getDeviceNo, slaveId));
            modbusInfo = new DevBaseDeviceTCPVo();
            if (!ObjectUtils.isEmpty(source)) {
                BeanCopyUtils.copy(source, modbusInfo);
                redisTemplate.opsForValue().set("zm:create-tcp:" + slaveId, modbusInfo, modbusProperties.getConnectionTtl(), TimeUnit.SECONDS);
            }
        }
        return modbusInfo;
    }
}
