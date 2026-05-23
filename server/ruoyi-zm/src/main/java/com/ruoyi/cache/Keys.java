package com.ruoyi.cache;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;

import java.util.List;

public class Keys {
    final String QUEUE_KEY = "zm:queue:zm:cache:";
    final String TCP_KEY = "zm:create-tcp:";
    final String TELEMETER_KEY = QUEUE_KEY + "3:";
    final String TELECOMMAND_KEY = QUEUE_KEY + "1:";
    final String REMOTE_KEY = QUEUE_KEY + "63:";

    private RedisTemplate<String, Object> redisTemplate;
    private RedisTemplate<String, short[]> shortArrayRedisTemplate;
    private RedisTemplate<String, boolean[]> booleanArrayRedisTemplate;
    private DevBaseDeviceMapper deviceMapper;

    public Keys(RedisTemplate<String, Object> redisTemplate,
                RedisTemplate<String, short[]> shortArrayRedisTemplate,
                RedisTemplate<String, boolean[]> booleanArrayRedisTemplate,
                DevBaseDeviceMapper deviceMapper) {
        this.redisTemplate = redisTemplate;
        this.shortArrayRedisTemplate = shortArrayRedisTemplate;
        this.booleanArrayRedisTemplate = booleanArrayRedisTemplate;
        this.deviceMapper = deviceMapper;
    }

    public DevBaseDeviceTCPVo getCreateTCP(Integer deviceId) {
        DevBaseDeviceTCPVo tcpVo = (DevBaseDeviceTCPVo) this.redisTemplate.opsForValue().get(TCP_KEY + deviceId);
        if (ObjectUtils.isEmpty(tcpVo)) {
            DevBaseDevice device = this.deviceMapper.selectById(deviceId);
            if (device == null) {
                log.warn("Keys.getCreateTCP device not found, deviceId={}", deviceId);
                return new DevBaseDeviceTCPVo();
            }
            tcpVo = new DevBaseDeviceTCPVo();
            tcpVo.setId(device.getDeviceNo());
            tcpVo.setIp(device.getIp());
            tcpVo.setPort(device.getPort());
            tcpVo.setOnlineStatus(0); // 离线
            tcpVo.setLastTime(device.getLastTime());
        }
        return tcpVo;
    }

    public Object getTelemeter(Integer deviceId, String addr) {
        // System.out.println(TELEMETER_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + addr);
        return this.redisTemplate.opsForValue()
            .get(TELEMETER_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + addr);
    }

    public boolean[] getTelecommand(Integer deviceId) {
        // System.out.println(TELECOMMAND_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":0x0000");
        return this.booleanArrayRedisTemplate.opsForValue().
            get(TELECOMMAND_KEY + getCreateTCP(deviceId).getIp() + ":" + deviceId + ":0x0000");
    }

    public <T> TableDataInfo<T> getPageTable(List<T> source, PageQuery page) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        int end = Math.min(start + page.getPageSize(), source.size());
        TableDataInfo<T> table = TableDataInfo.build();
        table.setTotal(source.size());
        table.setRows(source.subList(start, end));
        return table;
    }
}
