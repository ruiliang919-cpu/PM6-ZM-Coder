package com.ruoyi.mqtt03.addr03.handler.addr0x0004;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.TimeUtil;
import com.ruoyi.utils.device.time.Timer;
import com.ruoyi.zm.domain.DevBasePower;
import com.ruoyi.zm.mapper.DevBasePowerMapper;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("Addr0004Handler")
@RequiredArgsConstructor
public class Addr0x0004Handler implements AddrHandler {
    private final DevBasePowerMapper powerMapper;

    @Timer("0004")
    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0x0004 body = gson.fromJson(payload, Addr0x0004.class);
        long timestamp = body.getTime();
        List<Addr0x0004.Data> data = body.getData();
        for (Addr0x0004.Data d : data) {
            Integer type = d.getType();
            BigDecimal v = new BigDecimal(d.getValue());
            type(deviceNo, timestamp, v, type);
        }
    }

    // type != 2 => 10
    // type == 2 => 366
    private void type(int deviceNo, long timestamp, BigDecimal value, int type) {
        DevBasePower d = new DevBasePower();
        d.setId(IdGenerator.UUIDId());
        d.setDeviceId(deviceNo);
        d.setTimestamp(timestamp);
        d.setType(type);
        d.setValue(value);

        long[] s = timeRange(type);
//        log.info("{}-{}", type, Arrays.toString(s));
        List<DevBasePower> l = powerMapper.selectList(new LambdaQueryWrapper<DevBasePower>()
            .select(DevBasePower::getId)
            .eq(DevBasePower::getDeviceId, deviceNo)
            .eq(DevBasePower::getType, type)
            .between(DevBasePower::getTimestamp, s[0], s[1])
        );
//        log.info("{}", l);
        if (l == null || l.isEmpty()) powerMapper.insert(d);
        else {
//            log.info("{}", l.stream().map(DevBasePower::getId).collect(Collectors.toList()));
            powerMapper.deleteBatchIds(l.stream().map(DevBasePower::getId).collect(Collectors.toList()));
            powerMapper.insert(d);
        }
        LambdaQueryWrapper<DevBasePower> lqw = new LambdaQueryWrapper<DevBasePower>()
            .eq(DevBasePower::getType, type)
            .eq(DevBasePower::getDeviceId, deviceNo)
            .orderByAsc(DevBasePower::getTimestamp);
        Long c = powerMapper.selectCount(lqw);
//        log.info("{}", c);
        if (c != null) {
            if (2 == type && c > 366) powerMapper.delete(lqw.last("LIMIT 1"));
            else if (c > 10) powerMapper.delete(lqw.last("LIMIT 1"));
        }
    }

    // 3:周耗电量 4:月耗电量 5:季耗电量 6:年耗电量
    private long[] timeRange(int type) {
        long[] timeRange = new long[2];
        switch (type) {
            case 2:
                LocalDate date = LocalDate.now();
                timeRange[0] = date.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
                timeRange[1] = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond();
                break;
            case 3:
                timeRange = TimeUtil.getCurrentWeekRange();
                break;
            case 4:
                timeRange = TimeUtil.getCurrentMonthRange();
                break;
            case 5:
                timeRange = TimeUtil.getCurrentQuarterRange();
                break;
            case 6:
                timeRange = TimeUtil.getCurrentYearRange();
                break;
        }
        return timeRange;
    }
}
