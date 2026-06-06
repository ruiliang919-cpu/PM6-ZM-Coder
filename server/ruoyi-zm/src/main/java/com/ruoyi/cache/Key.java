package com.ruoyi.cache;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
import com.ruoyi.schedule.util.InstructAddrUtil;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevProtocol01;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevProtocol01Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Key {
    public static final String QUEUE_KEY = "zm:queue:zm:cache:";
    public static final String TCP_KEY = "zm:create-tcp:";
    public static final String TELEMETER_KEY = QUEUE_KEY + "3:";
    public static final String TELECOMMAND_KEY = QUEUE_KEY + "1:";
    public static final String REMOTE_KEY = QUEUE_KEY + "63:";
    public static final String CODE01_PROTOCOLS = "zm:global:protocols:01";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, short[]> shortArrayRedisTemplate;
    private final RedisTemplate<String, boolean[]> booleanArrayRedisTemplate;
    private final DevBaseDeviceMapper deviceMapper;
    private final DevProtocol01Mapper protocol01Mapper;
    private final InstructAddrUtil instructAddrUtil;

    public List<BigDecimal> getTelemeterBigDecimalList(Integer deviceId, String addr) {
        return (List<BigDecimal>) this.redisTemplate.opsForValue()
            .get(TELEMETER_KEY + getCreateTCP(Math.toIntExact(deviceId)).getIp() + ":" + deviceId + ":" + addr);
    }

    public BigDecimal getTelemeterByBigDecimalWithDigits(Integer deviceId, String addr, int digits) {
        return ((BigDecimal) Objects.requireNonNull(this.redisTemplate.opsForValue()
            .get(TELEMETER_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + addr))).setScale(digits, RoundingMode.HALF_UP);
    }

    public Integer getTelemeterByInt(Integer deviceId, String addr) {
        return ((BigDecimal) Objects.requireNonNull(this.redisTemplate.opsForValue()
            .get(TELEMETER_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + addr))).intValue();
    }

    public Object getTelemeter(Integer deviceId, String addr) {
//        if (addr.equals("0XAE8F")) System.err.println(TELEMETER_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + addr);
        return this.redisTemplate.opsForValue()
            .get(TELEMETER_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + addr);
    }

    public Object getRemote(Integer deviceId, String addr) {
//         System.out.println(REMOTE_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + addr);
        Object o = this.redisTemplate.opsForValue()
            .get(REMOTE_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + addr);
        if (!"0XB715".equals(addr)) return o;
        if (o == null) {
            Addr0XB715.Data d = new Addr0XB715.Data();
            d.setHandModule("");
            d.setIlluminanceSensorModule(false);
            d.setInfraredSensorModule(false);
            d.setTimeModule("noEnabled");
            redisTemplate.opsForValue().set(REMOTE_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + "0XB715", d);
            return d;
        } else return o;
    }

    public short[] getRemoteByArr(Integer deviceId, String addr) {
        short[] shorts = this.shortArrayRedisTemplate.opsForValue()
            .get(REMOTE_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + addr);
        return shorts != null && shorts.length > 0 ? shorts : new short[]{-1};
    }

    public DevBaseDeviceTCPVo getCreateTCP(Integer deviceNo) {
        try {
            DevBaseDeviceTCPVo tcpVo = (DevBaseDeviceTCPVo) this.redisTemplate.opsForValue().get(TCP_KEY + deviceNo);
            if (ObjectUtils.isEmpty(tcpVo)) {
                DevBaseDevice device = this.deviceMapper.selectOne(new LambdaQueryWrapper<DevBaseDevice>()
                    .eq(DevBaseDevice::getDeviceNo, deviceNo));
                tcpVo = new DevBaseDeviceTCPVo();
                tcpVo.setId(device.getDeviceNo());
                tcpVo.setIp(device.getIp());
                tcpVo.setPort(device.getPort());
                tcpVo.setOnlineStatus(device.getOnlineStatus());
                tcpVo.setLastTime(device.getLastTime());
            }
            return tcpVo;
        } catch (Exception e) {
            log.error("getCreateTCP failed, deviceNo={}", deviceNo, e);
            DevBaseDeviceTCPVo fallback = new DevBaseDeviceTCPVo();
            fallback.setId(deviceNo.longValue());
            return fallback;
        }
    }

    public <T> TableDataInfo<T> getPageTable(List<T> source, PageQuery page) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        int end = Math.min(start + page.getPageSize(), source.size());
        TableDataInfo<T> table = TableDataInfo.build();
        table.setTotal(source.size());
        table.setRows(source.subList(start, end));
        return table;
    }

    public <T> TableDataInfo<T> getPageTable(List<T> source, int num, int size) {
        int start = (num - 1) * size;
        int end = Math.min(start + size, source.size());
        TableDataInfo<T> table = TableDataInfo.build();
        table.setTotal(source.size());
        table.setRows(source.subList(start, end));
        return table;
    }

    public <T> List<T> getPageList(List<T> source, PageQuery page) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        int end = Math.min(start + page.getPageSize(), source.size());
        return source.subList(start, end);
    }

    // 获取 01 协议列表
    public List<DevProtocol01> getProtocol01List() {
        List<DevProtocol01> result = (List<DevProtocol01>) redisTemplate.opsForValue().get(CODE01_PROTOCOLS);
        if (result == null) {
            result = protocol01Mapper.selectList();
            redisTemplate.opsForValue().set(CODE01_PROTOCOLS, result);
        }
        return result;
    }

    // 根据协议地址获取协议名称 01
    public String get01Name(String addr) {
        return instructAddrUtil.NameByAddr(addr);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public boolean[] getTelecommand(Integer deviceId) {
        return this.booleanArrayRedisTemplate.opsForValue().
            get(TELECOMMAND_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":0x0000");
    }

    public int getTelecommand(Integer deviceId, int index) {
        boolean[] booleans = this.booleanArrayRedisTemplate.opsForValue().
            get(TELECOMMAND_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":0x0000");
        if (booleans == null || booleans.length == 0) return 0;
        return booleans[index] ? 1 : 0;
    }

    public boolean[] getTelecommand(Integer deviceId, int start, int end) {
        boolean[] booleans = this.booleanArrayRedisTemplate.opsForValue().
            get(TELECOMMAND_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":0x0000");
        boolean[] subArr = null;
        if (booleans != null) {
            subArr = Arrays.copyOfRange(booleans, start, end + 1);
        }
        return subArr;
    }

    public int[] getTelecommandByIntArr(Integer deviceId, int start, int end) {
        boolean[] booleans = this.booleanArrayRedisTemplate.opsForValue().
            get(TELECOMMAND_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":0x0000");
        boolean[] subArr = null;
        if (booleans != null) {
            subArr = Arrays.copyOfRange(booleans, start, end + 1);
        }
        int[] coilStatuses = new int[0];
        if (subArr != null) {
            coilStatuses = new int[subArr.length];
            for (int i = 0; i < subArr.length; i++) {
                if (subArr[i]) {
                    coilStatuses[i] = 1;
                } else {
                    coilStatuses[i] = 0;
                }
            }
        }
        return coilStatuses;
    }

    public Object multiGetTelemeter(int deviceId, String[] address) {
        String base = TELEMETER_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":";
        return this.redisTemplate.opsForValue().multiGet(Arrays.stream(address).map(a -> base + a).collect(Collectors.toList()));
    }
}
