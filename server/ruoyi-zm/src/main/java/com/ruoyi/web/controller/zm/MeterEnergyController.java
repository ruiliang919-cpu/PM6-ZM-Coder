package com.ruoyi.web.controller.zm;

import com.ruoyi.cache.DeviceCachePlus;
import com.ruoyi.cache.Key;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.*;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.utils.device.time.TimeUtil.second;

//@CacheConfig(cacheNames = "zm:energyMeter")
@RestController
@RequestMapping("/zm/energy")
@RequiredArgsConstructor
public class MeterEnergyController {
    private final IDevEnergyMeterDayService energyMeterDayService;
    private final IDevEnergyMeterWeekService energyMeterWeekService;
    private final IDevEnergyMeterMonthService energyMeterMonthService;
    private final IDevEnergyMeterQuarterService energyMeterQuarterService;
    private final IDevEnergyMeterYearService energyMeterYearService;
    private final DeviceCachePlus deviceCache;
    private final Key key;
    private final RedisTemplate<String, Object> redisTemplate;

    // 获取四十电表日耗电量
    @PostMapping("/day")
//    @Cacheable(key = "'day:' + #reqVo.deviceId + ':' + #reqVo.pageNum + ':' + #reqVo.pageSize", unless = "#result == null")
    public TableDataInfo<DevEnergyMeterDayVo> meterDayEnergy(@RequestBody MeterByNoReqVo reqVo) {
        TableDataInfo<DevEnergyMeterDayVo> t = energyMeterDayService.queryPageList(new DevEnergyMeterDayBo() {{
            setDeviceNo(reqVo.getDeviceId());
        }}, new PageQuery() {{
            setPageNum(reqVo.getPageNum());
            setPageSize(reqVo.getPageSize());
        }});
        t.getRows().forEach(item -> {
            item.setId(item.getEnergyMeterNo());
            item.setTimestamp(item.getTimestamp() * 1000L);
            item.setPower(item.getPower());
        });
        return t;
    }

    // 获取四十电表周耗电量
    @PostMapping("/week")
//    @Cacheable(key = "'week:' + #reqVo.deviceId + ':' + #reqVo.pageNum + ':' + #reqVo.pageSize", unless = "#result == null")
    public TableDataInfo<DevEnergyMeterWeekVo> meterWeekEnergy(@RequestBody MeterByNoReqVo reqVo) {
        TableDataInfo<DevEnergyMeterWeekVo> t = energyMeterWeekService.queryPageList(new DevEnergyMeterWeekBo() {{
            setDeviceNo(reqVo.getDeviceId());
        }}, new PageQuery() {{
            setPageNum(reqVo.getPageNum());
            setPageSize(reqVo.getPageSize());
        }});
        t.getRows().forEach(item -> {
            item.setId(item.getEnergyMeterNo());
            item.setTimestamp(item.getTimestamp() * 1000L);
            item.setPower(item.getPower());
        });
        return t;
    }


    // 获取四十电表月耗电量
    @PostMapping("/month")
//    @Cacheable(key = "'month:' + #reqVo.deviceId + ':' + #reqVo.pageNum + ':' + #reqVo.pageSize", unless = "#result == null")
    public TableDataInfo<DevEnergyMeterMonthVo> meterMonthEnergy(@RequestBody MeterByNoReqVo reqVo) {
        TableDataInfo<DevEnergyMeterMonthVo> t = energyMeterMonthService.queryPageList(new DevEnergyMeterMonthBo() {{
            setDeviceNo(reqVo.getDeviceId());
        }}, new PageQuery() {{
            setPageNum(reqVo.getPageNum());
            setPageSize(reqVo.getPageSize());
        }});
        t.getRows().forEach(item -> {
            item.setId(item.getEnergyMeterNo());
            item.setTimestamp(item.getTimestamp() * 1000L);
            item.setPower(item.getPower());
        });
        return t;
    }

    // 获取四十电表季耗电量
    @PostMapping("/quarter")
//    @Cacheable(key = "'quarter:' + #reqVo.deviceId + ':' + #reqVo.pageNum + ':' + #reqVo.pageSize", unless = "#result == null")
    public TableDataInfo<DevEnergyMeterQuarterVo> meterQuarterEnergy(@RequestBody MeterByNoReqVo reqVo) {
        TableDataInfo<DevEnergyMeterQuarterVo> t = energyMeterQuarterService.queryPageList(new DevEnergyMeterQuarterBo() {{
            setDeviceNo(reqVo.getDeviceId());
        }}, new PageQuery() {{
            setPageNum(reqVo.getPageNum());
            setPageSize(reqVo.getPageSize());
        }});
        t.getRows().forEach(item -> {
            item.setId(item.getEnergyMeterNo());
            item.setTimestamp(item.getTimestamp() * 1000L);
            item.setPower(item.getPower());
        });
        return t;
    }

    // 获取四十电表年耗电量
    @PostMapping("/meterYearEnergy")
//    @Cacheable(key = "'meterYearEnergy:' + #reqVo.deviceId + ':' + #reqVo.pageNum + ':' + #reqVo.pageSize", unless = "#result == null")
    public TableDataInfo<DevEnergyMeterYearVo> meterYearEnergy(@RequestBody MeterByNoReqVo reqVo) {
        TableDataInfo<DevEnergyMeterYearVo> t = energyMeterYearService.queryPageList(new DevEnergyMeterYearBo() {{
            setDeviceNo(reqVo.getDeviceId());
        }}, new PageQuery() {{
            setPageNum(reqVo.getPageNum());
            setPageSize(reqVo.getPageSize());
        }});
        t.getRows().forEach(item -> {
            item.setId(item.getEnergyMeterNo());
            item.setTimestamp(item.getTimestamp() * 1000L);
            item.setPower(item.getPower());
        });
        return t;
    }

    // 获取设备类型，若是配电柜则显示数据，若是电源柜则不显示数据
    @GetMapping("/getDeviceType")
    public R<TreeMap<String, Object>> getDeviceType(@Param("deviceId") Integer deviceId) {
        TreeMap<String, Object> result = new TreeMap<>();
        try {
            int type = key.getTelecommand(deviceId, 822);
            // 机柜类型 （1:电源柜 0:配电柜）
            if (type == 0) {
                result.put("type", "配电柜");
                result.put("typeId", 0);
            } else if (type == 1) {
                result.put("type", "电源柜");
                result.put("typeId", 1);
            }
            return R.ok(result);
        } catch (Exception e) {
            result.put("typeId", 0);
            result.put("type", "配电柜");
        }
        return R.ok(result);
    }

    // 获取应该显示的电表（回路）数
    public Integer getLoopNums(Integer deviceId) {
        int result = 0;
        try {
            HashMap<String, Integer> dcDimNum = deviceCache.getDcDimNum(deviceId);
            result = dcDimNum.get("dimmerNum") + dcDimNum.get("dcModuleNum");
        } catch (Exception ignored) {

        }
        return result;
    }

    @PostConstruct
    public void clear() {
        long second = second();
        if (second <= 0) second += 10;
        redisTemplate.opsForValue().set("zm:clear:power", "", second, TimeUnit.SECONDS);
    }
}
