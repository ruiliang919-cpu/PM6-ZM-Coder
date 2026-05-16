package com.ruoyi.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.flag.DeviceFlag;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBaseRegion;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.DeviceHomeVoResp;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBaseRegionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Lazy
public class DeviceCache {

    private RedisTemplate<String, Object> redisTemplate;
    private DevBaseDeviceMapper deviceMapper;
    private DListUtil dListUtil;
    private DevBaseRegionMapper regionMapper;
    private Keys keys;
    private Key key;

    public DeviceCache(RedisTemplate<String, Object> redisTemplate,
                       RedisTemplate<String, short[]> shortArrayRedisTemplate,
                       RedisTemplate<String, boolean[]> booleanArrayRedisTemplate,
                       DevBaseDeviceMapper deviceMapper,
                       DListUtil dListUtil,
                       DevBaseRegionMapper regionMapper,
                       Key key
    ) {
        this.redisTemplate = redisTemplate;
        this.deviceMapper = deviceMapper;
        this.dListUtil = dListUtil;
        this.regionMapper = regionMapper;
        this.keys = new Keys(this.redisTemplate,
            shortArrayRedisTemplate, booleanArrayRedisTemplate, this.deviceMapper);
        this.key = key;
    }

    // 获取区域名称
    public String getAreaName(Integer areaId) {
        if (areaId == null || areaId == 0) return "未知区域";
        LambdaQueryWrapper<DevBaseRegion> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseRegion::getId, areaId);
        DevBaseRegion region = this.regionMapper.selectOne(lqw);
        if (region == null) return "未知区域";
        return region.getName();
    }

    // 获取机柜信息列表 首页展示
    public List<DeviceHomeVoResp> getCabinetList() {
        DeviceFlag.SetFlag(DeviceFlag.LIST, true);
        List<DeviceHomeVoResp> result = new ArrayList<>();
        List<DevBaseDevice> source = dListUtil.List();
        Map<Long, String> nameMap = dListUtil.NameMap();
        if (!source.isEmpty()) {
            AtomicInteger id = new AtomicInteger(1);
            source.forEach(item -> {
                int no = Math.toIntExact(item.getDeviceNo());
                DevBaseDeviceTCPVo t = this.keys.getCreateTCP(no);
                DeviceHomeVoResp resp = new DeviceHomeVoResp();
                resp.setOrderNo(id.getAndIncrement());
                resp.setDeviceId(no);
                resp.setDeviceName(nameMap.getOrDefault(item.getDeviceNo(), "未登记机柜"));
                resp.setArea(getAreaName(Math.toIntExact(item.getRegionId())));
                try {
                    resp.setOnlineStatus(t.getOnlineStatus());
                    try {
                        resp.setRunStatus(this.keys.getTelecommand(no)[163] ? 1 : 0);
                        resp.setDeviceStatus(this.keys.getTelecommand(no)[162] ? 1 : 0);
                        resp.setRunMode(this.keys.getTelecommand(no)[164] ? 1 : 0);
                    } catch (Exception ignored) {
                        resp.setDeviceStatus(1);
                    }
                    try {
                        resp.setDcBusVoltage(((java.math.BigDecimal) this.keys.getTelemeter(no, "0x001A")).setScale(1, RoundingMode.HALF_UP));
                    } catch (Exception ignored) {
                        resp.setDcBusVoltage(new BigDecimal(0));
                    }
                    try {
                        resp.setDimmerNum(((java.math.BigDecimal) this.keys.getTelemeter(no, "0x0022")).intValue());
                    } catch (Exception ignored) {
                        resp.setDimmerNum(0);
                    }
                    try {
                        boolean typeB = this.key.getTelecommand(no)[822];
                        if (typeB)
                            // 电源柜 整流模块数量
                            resp.setDcModuleNum(((java.math.BigDecimal) this.keys.getTelemeter(no, "0x0026")).intValue());
                        else
                            // 配电柜 DC/DC模块数量
                            resp.setDcModuleNum(((java.math.BigDecimal) this.keys.getTelemeter(no, "0x0027")).intValue());
                    } catch (Exception ignored) {
                        resp.setDcModuleNum(0);
                    }
                    int acLoopNum = 0;
                    Object remote = key.getRemote(no, "0XAF1Enum");
                    if (remote != null) acLoopNum = Integer.parseInt(remote.toString());
                    resp.setAcSwitchNum(acLoopNum);
                    try {
                        resp.setVersion(t.getVersion());
                    } catch (Exception ignored) {
                        resp.setVersion("V0.0.0");
                    }
                } catch (Exception ignored) {
                }
                result.add(resp);
            });
            return result;
        }
        return null;
    }

    // 单独获取调光与开关模块
    public HashMap<String, Integer> getDcDimNum(Integer deviceId) {
        try {
            return new HashMap<String, Integer>() {{
                try {
                    put("dimmerNum", ((java.math.BigDecimal) keys.getTelemeter(deviceId, "0x0022")).intValue());
                } catch (Exception e) {
                    put("dimmerNum", 0);
                }
                try {
                    boolean typeB = key.getTelecommand(deviceId)[822];
                    if (typeB) {
                        // 电源柜 整流模块数量
                        put("dcModuleNum", ((java.math.BigDecimal) keys.getTelemeter(deviceId, "0x0026")).intValue());
                    } else {
                        // 配电柜 DC/DC模块数量
                        put("dcModuleNum", ((java.math.BigDecimal) keys.getTelemeter(deviceId, "0x0027")).intValue());
                    }
                } catch (Exception e) {
                    put("dcModuleNum", 0);
                }
            }};
        } catch (Exception e) {
            log.error("DeviceCache → getDcDimNum", e);
        }
        return new HashMap<>();
    }
}
