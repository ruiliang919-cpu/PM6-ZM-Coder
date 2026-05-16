package com.ruoyi.web.controller.zm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.DeviceCache;
import com.ruoyi.cache.Key;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.DevBasePower;
import com.ruoyi.zm.domain.vo.DeviceHomeVoResp;
import com.ruoyi.zm.domain.vo.PowerRespVo;
import com.ruoyi.zm.domain.vo.PowerTimeRespVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBasePowerMapper;
import com.ruoyi.zm.mapper.DevBaseRegionMapper;
import com.ruoyi.zm.service.IDevBaseDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 首页控制层
 */
@Slf4j
@RestController
@RequestMapping("/zm/home")
@RequiredArgsConstructor
public class HomeController {
    private final RedisTemplate<String, Object> redisTemplate;

    private final IDevBaseDeviceService deviceService;
    private final DevBaseDeviceMapper deviceMapper;
    private final DevBasePowerMapper powerMapper;

    private final DevBaseRegionMapper regionMapper;
    private final RedisTemplate<String, short[]> shortArrayRedisTemplate;
    private final RedisTemplate<String, boolean[]> booleanArrayRedisTemplate;
    private final Key key;
    private final DListUtil dListUtil;

    @Value("${version:V0.0.1}")
    public String version;

    // 系统版本号 GET /zm/home/version
    @GetMapping("/version")
    public R<String> version() {
        return R.ok("获取主机版本号成功", version);
    }

    /**
     * 获取每一个机柜的总耗电量
     */
    @GetMapping("/powersByAll")
    public R<List<PowerRespVo>> powersByAll() {
        R<List<PowerRespVo>> r = (R<List<PowerRespVo>>) redisTemplate.opsForValue().get("zm:home:power:all");
        if (r == null) r = getEnergyData(1);
        return r;
    }

    /**
     * 获取每一个机柜的日耗电量
     */
    @GetMapping("/powersByDay")
    public R<List<PowerRespVo>> powersByDay() {
        R<List<PowerRespVo>> r = (R<List<PowerRespVo>>) redisTemplate.opsForValue().get("zm:home:power:day");
        if (r == null) r = getEnergyData(2);
        return r;
    }

    @PostConstruct
    @Scheduled(fixedDelay = 30000)
    public void initPower() {
        R<List<PowerRespVo>> all = getEnergyData(1);
        R<List<PowerRespVo>> day = getEnergyData(2);
        redisTemplate.opsForValue().set("zm:home:power:all", all);
        redisTemplate.opsForValue().set("zm:home:power:day", day);
    }

    public R<List<PowerRespVo>> getEnergyData(int type) {
        List<PowerRespVo> allPower = new ArrayList<>();
        List<Long> nos = dListUtil.Nos();
        Map<Long, String> nameMap = dListUtil.NameMap();
        if (!nos.isEmpty()) {
            if (type == 1) {
                nos.forEach(item -> {
                    try {
                        if (key.getTelecommand(Math.toIntExact(item))[822]) {
                            PowerTimeRespVo power = (PowerTimeRespVo) redisTemplate.opsForValue().get("zm:power:power:" + item);
                            PowerRespVo vo = new PowerRespVo();
                            vo.setDeviceId(Math.toIntExact(item));
                            vo.setName(nameMap.getOrDefault(item, "未登记机柜"));
                            if (power != null) vo.setPower(power.getPower());
                            else vo.setPower(BigDecimal.ZERO);
                            allPower.add(vo);
                        }
                    } catch (Exception ignored) {
                    }
                });
            } else if (type == 2) {
                nos.forEach(item -> {
                    try {
                        if (key.getTelecommand(Math.toIntExact(item))[822]) {
                            DevBasePower power = powerMapper.selectOne(new LambdaQueryWrapper<DevBasePower>()
                                .eq(DevBasePower::getDeviceId, item)
                                .eq(DevBasePower::getType, type)
                                .orderByDesc(DevBasePower::getTimestamp)
                                .select(DevBasePower::getDeviceId, DevBasePower::getValue).last("LIMIT 1"));
                            PowerRespVo vo = new PowerRespVo();
                            vo.setDeviceId(Math.toIntExact(item));
                            vo.setName(nameMap.getOrDefault(item, "未登记机柜"));
                            if (power != null) vo.setPower(power.getValue());
                            else vo.setPower(BigDecimal.ZERO);
                            allPower.add(vo);
                        }
                    } catch (Exception ignored) {
                    }
                });
            }
        }
        return R.ok(allPower);
    }

    /**
     * 获取机柜综合信息
     */
    @PostMapping("/cabinetList")
    public TableDataInfo<DeviceHomeVoResp> getCabinetList(@RequestBody PageQuery pageQuery) {
        List<DeviceHomeVoResp> cabinetList = (List<DeviceHomeVoResp>) redisTemplate.opsForValue().get("zm:home:cabinetList");
        if (cabinetList != null) return key.getPageTable(cabinetList, pageQuery);
        return deviceService.getCabinetList(pageQuery);
    }

    @PostConstruct
    @Scheduled(fixedDelay = 5000)
    public void cabinetListCache() {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(100);
        DeviceCache cache = new DeviceCache(redisTemplate, shortArrayRedisTemplate, booleanArrayRedisTemplate, deviceMapper, dListUtil, regionMapper, key);
        List<DeviceHomeVoResp> cabinetList = cache.getCabinetList();
        if (cabinetList != null) redisTemplate.opsForValue().set("zm:home:cabinetList", cabinetList);
        else redisTemplate.opsForValue().set("cabinetList", Collections.EMPTY_LIST);
    }
}
