package com.ruoyi.mqtt03.addr03.handler.addr0X00CB;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.mapper.*;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ruoyi.utils.device.time.TimeUtil.*;

@Service("Addr00CBHandler")
@RequiredArgsConstructor
public class Addr0x00CBHandler implements AddrHandler {
    private final DevEnergyMeterDayMapper energyMeterDayMapper;
    private final DevEnergyMeterWeekMapper energyMeterWeekMapper;
    private final DevEnergyMeterMonthMapper energyMeterMonthMapper;
    private final DevEnergyMeterQuarterMapper energyMeterQuarterMapper;
    private final DevEnergyMeterYearMapper energyMeterYearMapper;

    @Timer("00CB")
    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0X00CB body = gson.fromJson(payload, Addr0X00CB.class);
        long timestamp = body.getTime();

        List<DevEnergyMeterDay> days = new ArrayList<>();
        List<DevEnergyMeterWeek> weeks = new ArrayList<>();
        List<DevEnergyMeterMonth> months = new ArrayList<>();
        List<DevEnergyMeterQuarter> quarters = new ArrayList<>();
        List<DevEnergyMeterYear> years = new ArrayList<>();

        int[] no = {1};

        for (Addr0X00CB.Data d : body.getData()) {
            long uuid = IdGenerator.UUIDId();
            DevEnergyMeterDay day = new DevEnergyMeterDay();
            day.setId(uuid);
            day.setDeviceNo(deviceNo);
            day.setTimestamp(timestamp);
            day.setPower(new BigDecimal(d.getValue1()));
            day.setEnergyMeterNo(no[0]);
            DevEnergyMeterWeek week = new DevEnergyMeterWeek();
            week.setId(uuid);
            week.setDeviceNo(deviceNo);
            week.setTimestamp(timestamp);
            week.setPower(new BigDecimal(d.getValue2()));
            week.setEnergyMeterNo(no[0]);
            DevEnergyMeterMonth month = new DevEnergyMeterMonth();
            month.setId(uuid);
            month.setDeviceNo(deviceNo);
            month.setTimestamp(timestamp);
            month.setPower(new BigDecimal(d.getValue3()));
            month.setEnergyMeterNo(no[0]);
            DevEnergyMeterQuarter quarter = new DevEnergyMeterQuarter();
            quarter.setId(uuid);
            quarter.setDeviceNo(deviceNo);
            quarter.setTimestamp(timestamp);
            quarter.setPower(new BigDecimal(d.getValue4()));
            quarter.setEnergyMeterNo(no[0]);
            DevEnergyMeterYear year = new DevEnergyMeterYear();
            year.setId(uuid);
            year.setDeviceNo(deviceNo);
            year.setTimestamp(timestamp);
            year.setPower(new BigDecimal(d.getValue5()));
            year.setEnergyMeterNo(no[0]);

            no[0]++;

            days.add(day);
            weeks.add(week);
            months.add(month);
            quarters.add(quarter);
            years.add(year);
        }

        sDay(days, deviceNo);
        sWeek(weeks, deviceNo);
        sMonth(months, deviceNo);
        sQuarter(quarters, deviceNo);
        sYear(years, deviceNo);
    }

    private void sDay(List<DevEnergyMeterDay> d, Integer deviceNo) {
        LocalDate date = LocalDate.now();
        long s = date.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        long e = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond();
        List<DevEnergyMeterDay> l = energyMeterDayMapper.selectList(new LambdaQueryWrapper<DevEnergyMeterDay>()
            .select(DevEnergyMeterDay::getId)
            .eq(DevEnergyMeterDay::getDeviceNo, deviceNo)
            .between(DevEnergyMeterDay::getTimestamp, s, e)
        );
        if (l == null || l.isEmpty()) energyMeterDayMapper.insertBatch(d);
        else {
            energyMeterDayMapper.deleteBatchIds(l.stream().map(DevEnergyMeterDay::getId).collect(Collectors.toList()));
            energyMeterDayMapper.insertBatch(d);
        }
        long saveCount = energyMeterDayMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterDay>()
            .eq(DevEnergyMeterDay::getDeviceNo, deviceNo)
            .eq(DevEnergyMeterDay::getEnergyMeterNo, 1));
        if (saveCount > 366) energyMeterDayMapper.delete(new LambdaQueryWrapper<DevEnergyMeterDay>()
            .eq(DevEnergyMeterDay::getDeviceNo, deviceNo)
            .orderByAsc(DevEnergyMeterDay::getTimestamp)
            .last("LIMIT 40"));
    }

    private void sWeek(List<DevEnergyMeterWeek> d, Integer deviceNo) {
        long[] s = getCurrentWeekRange();
        List<DevEnergyMeterWeek> l = energyMeterWeekMapper.selectList(new LambdaQueryWrapper<DevEnergyMeterWeek>()
            .select(DevEnergyMeterWeek::getId)
            .eq(DevEnergyMeterWeek::getDeviceNo, deviceNo)
            .between(DevEnergyMeterWeek::getTimestamp, s[0], s[1])
        );

        if (l == null || l.isEmpty()) energyMeterWeekMapper.insertBatch(d);
        else {
            energyMeterWeekMapper.deleteBatchIds(l.stream().map(DevEnergyMeterWeek::getId).collect(Collectors.toList()));
            energyMeterWeekMapper.insertBatch(d);
        }
        long saveCount = energyMeterWeekMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterWeek>()
            .eq(DevEnergyMeterWeek::getDeviceNo, deviceNo)
            .eq(DevEnergyMeterWeek::getEnergyMeterNo, 1));
        if (saveCount > 10) energyMeterWeekMapper.delete(new LambdaQueryWrapper<DevEnergyMeterWeek>()
            .eq(DevEnergyMeterWeek::getDeviceNo, deviceNo)
            .orderByAsc(DevEnergyMeterWeek::getTimestamp)
            .last("LIMIT 40"));
    }

    private void sMonth(List<DevEnergyMeterMonth> d, Integer deviceNo) {
        long[] s = getCurrentMonthRange();
        List<DevEnergyMeterMonth> l = energyMeterMonthMapper.selectList(new LambdaQueryWrapper<DevEnergyMeterMonth>()
            .select(DevEnergyMeterMonth::getId)
            .eq(DevEnergyMeterMonth::getDeviceNo, deviceNo)
            .between(DevEnergyMeterMonth::getTimestamp, s[0], s[1])
        );

        if (l == null || l.isEmpty()) energyMeterMonthMapper.insertBatch(d);
        else {
            energyMeterMonthMapper.deleteBatchIds(l.stream().map(DevEnergyMeterMonth::getId).collect(Collectors.toList()));
            energyMeterMonthMapper.insertBatch(d);
        }
        long saveCount = energyMeterMonthMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterMonth>()
            .eq(DevEnergyMeterMonth::getDeviceNo, deviceNo).eq(DevEnergyMeterMonth::getEnergyMeterNo, 1));
        if (saveCount > 10) energyMeterMonthMapper.delete(new LambdaQueryWrapper<DevEnergyMeterMonth>()
            .eq(DevEnergyMeterMonth::getDeviceNo, deviceNo)
            .orderByAsc(DevEnergyMeterMonth::getTimestamp)
            .last("LIMIT 40"));
    }

    private void sQuarter(List<DevEnergyMeterQuarter> d, Integer deviceNo) {
        long[] s = getCurrentQuarterRange();
        List<DevEnergyMeterQuarter> l = energyMeterQuarterMapper.selectList(new LambdaQueryWrapper<DevEnergyMeterQuarter>()
            .select(DevEnergyMeterQuarter::getId)
            .eq(DevEnergyMeterQuarter::getDeviceNo, deviceNo)
            .between(DevEnergyMeterQuarter::getTimestamp, s[0], s[1])
        );

        if (l == null || l.isEmpty()) energyMeterQuarterMapper.insertBatch(d);
        else {
            energyMeterQuarterMapper.deleteBatchIds(l.stream().map(DevEnergyMeterQuarter::getId).collect(Collectors.toList()));
            energyMeterQuarterMapper.insertBatch(d);
        }
        long saveCount = energyMeterQuarterMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterQuarter>()
            .eq(DevEnergyMeterQuarter::getDeviceNo, deviceNo).eq(DevEnergyMeterQuarter::getEnergyMeterNo, 1));
        if (saveCount > 10) energyMeterQuarterMapper.delete(new LambdaQueryWrapper<DevEnergyMeterQuarter>()
            .eq(DevEnergyMeterQuarter::getDeviceNo, deviceNo)
            .orderByAsc(DevEnergyMeterQuarter::getTimestamp).last("LIMIT 40"));
    }

    private void sYear(List<DevEnergyMeterYear> d, Integer deviceNo) {
        long[] s = getCurrentQuarterRange();
        List<DevEnergyMeterYear> l = energyMeterYearMapper.selectList(new LambdaQueryWrapper<DevEnergyMeterYear>()
            .select(DevEnergyMeterYear::getId)
            .eq(DevEnergyMeterYear::getDeviceNo, deviceNo)
            .between(DevEnergyMeterYear::getTimestamp, s[0], s[1])
        );

        if (l == null || l.isEmpty()) energyMeterYearMapper.insertBatch(d);
        else {
            energyMeterYearMapper.deleteBatchIds(l.stream().map(DevEnergyMeterYear::getId).collect(Collectors.toList()));
            energyMeterYearMapper.insertBatch(d);
        }
        long saveCount = energyMeterYearMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterYear>()
            .eq(DevEnergyMeterYear::getDeviceNo, deviceNo).eq(DevEnergyMeterYear::getEnergyMeterNo, 1));
        if (saveCount > 10) energyMeterYearMapper.delete(new LambdaQueryWrapper<DevEnergyMeterYear>()
            .eq(DevEnergyMeterYear::getDeviceNo, deviceNo)
            .orderByAsc(DevEnergyMeterYear::getTimestamp).last("LIMIT 40"));
    }
}
