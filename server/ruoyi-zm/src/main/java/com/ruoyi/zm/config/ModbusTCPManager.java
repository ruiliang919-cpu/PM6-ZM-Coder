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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
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

    private final ConcurrentHashMap<Integer, ModbusMaster> masterPool = new ConcurrentHashMap<>();

    public ModbusMaster getSlave(int slaveId) {
        ModbusMaster master = masterPool.get(slaveId);
        if (master == null || !isMasterValid(slaveId, master)) {
            master = createNewMaster(slaveId);
            if (master != null) masterPool.put(slaveId, master);
        }
        return master;
    }

    private ModbusMaster createNewMaster(int slaveId) {
        DevBaseDeviceTCPVo modbusInfo = getOneByCache(slaveId);
        if (modbusInfo == null) return null;

        try {
            IpParameters params = new IpParameters();
            params.setHost(modbusInfo.getIp());
            params.setPort(modbusInfo.getPort());
            params.setEncapsulated(false);
            ModbusMaster master = modbusFactory.createTcpMaster(params, true);
            master.setTimeout(1000);
            master.setRetries(0);
            master.init();
            ReadCoilsRequest request = new ReadCoilsRequest(1, 0, 1);
            master.send(request);
            return master;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isMasterValid(int slaveId, ModbusMaster master) {
        try {
            if (master == null) {
                destroyMaster(slaveId, null);
                return false;
            }
            ReadCoilsRequest request = new ReadCoilsRequest(1, 0, 1);
            master.send(request);
            return true;
        } catch (Exception e) {
            destroyMaster(slaveId, master);
            return false;
        }
    }

    private void destroyMaster(int slaveId, ModbusMaster master) {
        try {
            if (master != null) master.destroy();
            masterPool.remove(slaveId);
        } catch (Exception e) {
            // log.error("销毁ModbusMaster失败", e);
        }
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
                redisTemplate.opsForValue().set("zm:create-tcp:" + slaveId, modbusInfo, 1, TimeUnit.DAYS);
                // log.info("连接Modbus成功，设备IP：{} 连接设备主机号：{}", modbusInfo.getIp(), modbusInfo.getId());
                redisTemplate.delete("zm:order:" + modbusInfo.getId() + ":1:0x0000");
                redisTemplate.delete("zm:fault:" + modbusInfo.getId() + ":0xAAAA");
                redisTemplate.convertAndSend("Recover", slaveId + "~");
            }
        } catch (Exception e) {
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
                    redisTemplate.opsForValue().set("zm:create-tcp:" + slaveId, modbusInfo, 1, TimeUnit.DAYS);
                    // 清除断线设备的告警信息
                    Set<String> keys = redisTemplate.keys("zm:fault:" + modbusInfo.getId() + ":*");
                    redisTemplate.delete(keys);
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
                // log.error("出错又出错~~", e1);
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
                redisTemplate.opsForValue().set("zm:create-tcp:" + slaveId, modbusInfo, 1, TimeUnit.DAYS);
            }
        }
        return modbusInfo;
    }
}
