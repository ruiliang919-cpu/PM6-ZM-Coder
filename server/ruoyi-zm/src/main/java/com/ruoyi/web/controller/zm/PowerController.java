package com.ruoyi.web.controller.zm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.utils.dnb.DNBUtil;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.domain.bo.DevBasePowerBo;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.mapper.*;
import com.ruoyi.zm.service.IDevBasePowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

// 机柜记录 - 耗电统计 控制层
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/zm/powerRecord")
public class PowerController {
    private final DevBasePowerMapper powerMapper;
    private final IDevBasePowerService powerService;
    private final DevBaseDeviceMapper deviceMapper;
    private final DevEnergyMeterDayMapper energyMeterDayMapper;
    private final DevEnergyMeterWeekMapper energyMeterWeekMapper;
    private final DevEnergyMeterMonthMapper energyMeterMonthMapper;
    private final DevEnergyMeterQuarterMapper energyMeterQuarterMapper;
    private final DevEnergyMeterYearMapper energyMeterYearMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    // 不再使用static可变对象，避免并发数据污染；每次请求新建对象

    // 根据设备ID获取机柜的总功率
    @RequestMapping("/getOneTotalPower")
    public R<DevBaseLoss> getOneTotalPower(Integer deviceId) {
        DevBaseLoss loss = (DevBaseLoss) redisTemplate.opsForValue().get("zm:power:loss:" + deviceId);
        if (loss!=null) return R.ok(loss);
        DevBaseLoss fallback = new DevBaseLoss();
        fallback.setLoss(BigDecimal.ZERO);
        return R.ok(fallback);
    }

    // 根据设备ID获取机柜的总耗电量
    @RequestMapping("/getOneTotalElectricByDeviceId")
    public R<PowerTimeRespVo> getOneTotalElectricByDeviceId(Integer deviceId) {
        PowerTimeRespVo vo = (PowerTimeRespVo) redisTemplate.opsForValue().get("zm:power:power:" + deviceId);
        if (vo!=null) return R.ok(vo);
        PowerTimeRespVo fallback = new PowerTimeRespVo();
        fallback.setPower(BigDecimal.ZERO);
        return R.ok(fallback);
    }

    // 类型 1:总电量 2:日耗电量 3:周耗电量 4:月耗电量 5:季耗电量 6:年耗电量
    // 根据设备ID获取年耗电量列表
    @PostMapping("/getYear")
    public TableDataInfo<?> getYear(PageWithIdReqVo reqVo) {
        return getPowerList(reqVo, 6);
    }

    // 根据设备ID获取季耗电量列表
    @PostMapping("/getQuarter")
    public TableDataInfo<?> getQuarter(PageWithIdReqVo reqVo) {
        return getPowerList(reqVo, 5);
    }

    // 根据设备ID获取月耗电量列表
    @PostMapping("/getMonth")
    public TableDataInfo<?> getMonth(PageWithIdReqVo reqVo) {
        return getPowerList(reqVo, 4);
    }

    // 根据设备ID获取周耗电量列表
    @PostMapping("/getWeek")
    public TableDataInfo<?> getWeek(PageWithIdReqVo reqVo) {
        return getPowerList(reqVo, 3);
    }

    // 根据设备ID获取日耗电量列表
    @PostMapping("/getDay")
    public TableDataInfo<?> getDay(PageWithIdReqVo reqVo) {
        return getPowerList(reqVo, 2);
    }

    public TableDataInfo<DevBasePowerVo> getPowerList(PageWithIdReqVo reqVo, Integer type) {
        TableDataInfo<DevBasePowerVo> source = powerService.queryPageList(new DevBasePowerBo() {{
            setDeviceId(reqVo.getDeviceId());
            setType(type);
        }}, new PageQuery() {{
            setPageNum(reqVo.getPageNum());
            setPageSize(reqVo.getPageSize());
        }});
        source.getRows().forEach(d -> {
            d.setPower(d.getValue());
            d.setTimestamp(d.getTimestamp() * 1000L);
        });
        return source;
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 耗电统计导出
     */
    @PostMapping("/exportExcel")
    public void exportExcel(Integer deviceId, HttpServletResponse response) {
        DevBaseDevice info = getDeviceInfo(deviceId);
        CompletableFuture<List<ElectricVo>> task2 = getTotalElectric(info, 6);
        CompletableFuture<List<ElectricVo>> task3 = getTotalElectric(info, 5);
        CompletableFuture<List<ElectricVo>> task4 = getTotalElectric(info, 4);
        CompletableFuture<List<ElectricVo>> task5 = getTotalElectric(info, 3);
        CompletableFuture<List<ElectricVo>> task6 = getTotalElectric(info, 2);
        CompletableFuture<List<DnbVO>> task7 = getDNB1(info);
        CompletableFuture<List<DnbVO>> task8 = getDNB2(info);
        CompletableFuture<List<DnbVO>> task9 = getDNB3(info);
        CompletableFuture<List<DnbVO>> task10 = getDNB4(info);
        CompletableFuture<List<DnbVO>> task11 = getDNB5(info);
        List<Map<String, Object>> data = new ArrayList<>(Collections.emptyList());
        CompletableFuture.allOf(
            task2, task3, task4, task5, task6, task7, task8, task9, task10, task11
        ).thenApply(results -> {
            data.add(new HashMap() {{
                put("device2", task2.join());
            }});
            data.add(new HashMap() {{
                put("device3", task3.join());
            }});
            data.add(new HashMap() {{
                put("device4", task4.join());
            }});
            data.add(new HashMap() {{
                put("device5", task5.join());
            }});
            data.add(new HashMap() {{
                put("device6", task6.join());
            }});
            data.add(new HashMap() {{
                put("device7", task7.join());
            }});
            data.add(new HashMap() {{
                put("device8", task8.join());
            }});
            data.add(new HashMap() {{
                put("device9", task9.join());
            }});
            data.add(new HashMap() {{
                put("device10", task10.join());
            }});
            data.add(new HashMap() {{
                put("device11", task11.join());
            }});
            return data;
        }).join();

        ExcelUtil.exportTemplateMultiSheet(data, "耗电统计", "/power_statistics_template.xlsx", response);
    }

    DevBaseDevice getDeviceInfo(Integer deviceId) {
        LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseDevice::getDeviceNo, deviceId);
        lqw.last("LIMIT 1");
        return deviceMapper.selectOne(lqw);
    }

    CompletableFuture<List<ElectricVo>> getTotalElectric(DevBaseDevice device, Integer type) {
        return CompletableFuture.supplyAsync(() -> {
            LambdaQueryWrapper<DevBasePower> lqw = new LambdaQueryWrapper<>();
            lqw.select(DevBasePower::getTimestamp, DevBasePower::getValue);
            lqw.eq(DevBasePower::getDeviceId, device.getDeviceNo());
            lqw.isNotNull(DevBasePower::getType);
            lqw.eq(DevBasePower::getType, type);
            lqw.orderByAsc(DevBasePower::getTimestamp);
            List<DevBasePower> power = powerMapper.selectList(lqw);
            List<ElectricVo> result = new ArrayList<>();
            if (!power.isEmpty()) {
                power.forEach(item -> {
                    ElectricVo vo = new ElectricVo();
                    vo.setIp(device.getIp());
                    vo.setName(device.getDeviceName());
                    LocalDateTime localDateTime = Instant.ofEpochMilli(item.getTimestamp() * 1000L)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                    vo.setTime(localDateTime.format(formatter));
                    vo.setPower(item.getValue());
                    result.add(vo);
                });
            }
            return result;
        });
    }

    CompletableFuture<List<DnbVO>> getDNB1(DevBaseDevice device) {
        return DNBUtil.getDNBGeneric(
            device,
            deviceNo -> energyMeterDayMapper.selectList(
                new LambdaQueryWrapper<DevEnergyMeterDay>()
                    .eq(DevEnergyMeterDay::getDeviceNo, deviceNo)
                    .orderByAsc(DevEnergyMeterDay::getTimestamp)
            ),
            DevEnergyMeterDay::getTimestamp,
            DevEnergyMeterDay::getEnergyMeterNo,
            DevEnergyMeterDay::getPower,
            formatter
        );
    }

    CompletableFuture<List<DnbVO>> getDNB2(DevBaseDevice device) {
        return CompletableFuture.supplyAsync(() -> {
            List<DevEnergyMeterWeek> l = energyMeterWeekMapper.selectList(new LambdaQueryWrapper<DevEnergyMeterWeek>()
                .eq(DevEnergyMeterWeek::getDeviceNo, device.getDeviceNo())
                .orderByAsc(DevEnergyMeterWeek::getTimestamp));
            if (l.isEmpty()) return new ArrayList<>();
            return l.stream().map(i -> {
                DnbVO vo = new DnbVO();
                vo.setName(device.getDeviceName());
                vo.setNo(i.getEnergyMeterNo());
                vo.setTime(Instant.ofEpochMilli(i.getTimestamp() * 1000L)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime().format(formatter));
                vo.setPower(i.getPower());
                return vo;
            }).collect(Collectors.toList());
        });
    }

    CompletableFuture<List<DnbVO>> getDNB3(DevBaseDevice device) {
        return CompletableFuture.supplyAsync(() -> {
            List<DevEnergyMeterMonth> l = energyMeterMonthMapper.selectList(new LambdaQueryWrapper<DevEnergyMeterMonth>()
                .eq(DevEnergyMeterMonth::getDeviceNo, device.getDeviceNo())
                .orderByAsc(DevEnergyMeterMonth::getTimestamp));
            if (l.isEmpty()) return Collections.emptyList();
            return l.stream().map(i -> {
                DnbVO vo = new DnbVO();
                vo.setName(device.getDeviceName());
                vo.setNo(i.getEnergyMeterNo());
                vo.setTime(Instant.ofEpochMilli(i.getTimestamp() * 1000L)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime().format(formatter));
                vo.setPower(i.getPower());
                return vo;
            }).collect(Collectors.toList());
        });
    }

    CompletableFuture<List<DnbVO>> getDNB4(DevBaseDevice device) {
        return CompletableFuture.supplyAsync(() -> {
            List<DevEnergyMeterQuarter> l = energyMeterQuarterMapper.selectList(new LambdaQueryWrapper<DevEnergyMeterQuarter>()
                .eq(DevEnergyMeterQuarter::getDeviceNo, device.getDeviceNo())
                .orderByAsc(DevEnergyMeterQuarter::getTimestamp));
            if (l.isEmpty()) return Collections.emptyList();
            return l.stream().map(i -> {
                DnbVO vo = new DnbVO();
                vo.setName(device.getDeviceName());
                vo.setNo(i.getEnergyMeterNo());
                vo.setTime(Instant.ofEpochMilli(i.getTimestamp() * 1000L)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime().format(formatter));
                vo.setPower(i.getPower());
                return vo;
            }).collect(Collectors.toList());
        });
    }

    CompletableFuture<List<DnbVO>> getDNB5(DevBaseDevice device) {
        return CompletableFuture.supplyAsync(() -> {
            List<DevEnergyMeterYear> l = energyMeterYearMapper.selectList(new LambdaQueryWrapper<DevEnergyMeterYear>()
                .eq(DevEnergyMeterYear::getDeviceNo, device.getDeviceNo())
                .orderByAsc(DevEnergyMeterYear::getTimestamp));
            if (l.isEmpty()) return Collections.emptyList();
            return l.stream().map(i -> {
                DnbVO vo = new DnbVO();
                vo.setName(device.getDeviceName());
                vo.setNo(i.getEnergyMeterNo());
                vo.setTime(Instant.ofEpochMilli(i.getTimestamp() * 1000L)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime().format(formatter));
                vo.setPower(i.getPower());
                return vo;
            }).collect(Collectors.toList());
        });
    }
}
