package com.ruoyi.web.controller.zm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.Key;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBasePower;
import com.ruoyi.zm.domain.vo.CabinetCalculationData;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBasePowerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 历史记录-电量计算-机柜综合计算
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/calculation/cabinet")
public class CabinetCalculationController {
    private final DevBasePowerMapper baseMapper;
    private final DevBaseDeviceMapper deviceMapper;
    private final Key key;

    // 类型 1:总电量 2:日耗电量 3:周耗电量 4:月耗电量 5:季耗电量 6:年耗电量

    @PostMapping("/total")
    public TableDataInfo<CabinetCalculationData> total(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getData(1), pageQuery);
    }

    @PostMapping("/day")
    public TableDataInfo<CabinetCalculationData> day(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getData(2), pageQuery);
    }

    @PostMapping("/week")
    public TableDataInfo<CabinetCalculationData> week(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getData(3), pageQuery);
    }

    @PostMapping("/month")
    public TableDataInfo<CabinetCalculationData> month(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getData(4), pageQuery);
    }

    @PostMapping("/quarter")
    public TableDataInfo<CabinetCalculationData> quarter(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getData(5), pageQuery);
    }

    @PostMapping("/year")
    public TableDataInfo<CabinetCalculationData> year(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getData(6), pageQuery);
    }

    private List<CabinetCalculationData> getData(Integer type) {
        List<CabinetCalculationData> result = new ArrayList<>();
        LambdaQueryWrapper<DevBasePower> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBasePower::getType, type);
        lqw.orderByDesc(DevBasePower::getId);
        List<DevBasePower> devBasePowers = baseMapper.selectList(lqw);
        if (!devBasePowers.isEmpty()) {
            devBasePowers.forEach(item -> {
                CabinetCalculationData data = new CabinetCalculationData();
                data.setName(getName(Long.valueOf(item.getDeviceId())));
                data.setTime(Instant.ofEpochMilli(item.getTimestamp() * 1000L).atZone(ZoneId.systemDefault())
                    .toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                data.setPower(String.valueOf(item.getValue()));
                if (!data.getName().isEmpty() && !data.getName().equals("未知机柜")) {
                    result.add(data);
                }
            });
        }
        return result;
    }

    private String getName(Long deviceId) {
        try {
            List<DevBaseDevice> devices = deviceMapper.selectList();
            return devices.stream()
                .filter(item -> Objects.equals(item.getDeviceNo(), deviceId))
                .findFirst().get().getDeviceName();
        } catch (Exception e) {
            return "未知机柜";
        }
    }
}
