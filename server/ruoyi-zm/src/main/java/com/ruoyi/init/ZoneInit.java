package com.ruoyi.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zm.domain.DevBaseDistrict;
import com.ruoyi.zm.domain.DevLightZoneCombination;
import com.ruoyi.zm.domain.DevZoneCombination;
import com.ruoyi.zm.mapper.DevBaseDistrictAssistMapper;
import com.ruoyi.zm.mapper.DevBaseDistrictMapper;
import com.ruoyi.zm.mapper.DevLightZoneCombinationMapper;
import com.ruoyi.zm.mapper.DevZoneCombinationMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ZoneInit {
    private static final Logger log = LoggerFactory.getLogger(ZoneInit.class);

    private final DevBaseDistrictMapper devBaseDistrictMapper;
    private final DevLightZoneCombinationMapper devLightZoneCombinationMapper;
    private final DevZoneCombinationMapper combinationMapper;
    private final DevBaseDistrictAssistMapper devBaseDistrictAssistMapper;
    @Value("${init.zoneInit:false}")
    public boolean initFlag;
    @Value("${init.lightZoneInit20251024:false}")
    public boolean lightAddFlag;

    @PostConstruct
    public void init() {
        if (initFlag) {
            List<DevZoneCombination> INIT_LIST = Arrays.asList(
                new DevZoneCombination(1L, 1L, "分区组合0" + 1, ""),
                new DevZoneCombination(2L, 2L, "分区组合0" + 2, ""),
                new DevZoneCombination(3L, 3L, "分区组合0" + 3, ""),
                new DevZoneCombination(4L, 4L, "分区组合0" + 4, ""),
                new DevZoneCombination(5L, 5L, "分区组合0" + 5, ""),
                new DevZoneCombination(6L, 6L, "分区组合0" + 6, ""),
                new DevZoneCombination(7L, 7L, "分区组合0" + 7, ""),
                new DevZoneCombination(8L, 8L, "分区组合0" + 8, "")
            );

            List<DevLightZoneCombination> LIGHT_INIT_LIST = Arrays.asList(
                new DevLightZoneCombination(1L, 1L, "广告照明分区组合", ""),
                new DevLightZoneCombination(2L, 2L, "导向照明分区组合", ""),
                new DevLightZoneCombination(3L, 3L, "出入口照明分区组合", ""),
                new DevLightZoneCombination(4L, 4L, "装饰灯照明", ""),
                new DevLightZoneCombination(5L, 5L, "站台照明", ""),
                new DevLightZoneCombination(6L, 6L, "站厅照明", ""),
                new DevLightZoneCombination(7L, 7L, "站台灯带照明", "")
            );

            combinationMapper.delete();
            devBaseDistrictMapper.delete();
            devBaseDistrictAssistMapper.delete();
            devLightZoneCombinationMapper.delete();
            combinationMapper.insertBatch(INIT_LIST);
            devLightZoneCombinationMapper.insertBatch(LIGHT_INIT_LIST);

            List<DevBaseDistrict> districtList = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                DevBaseDistrict devBaseDistrict = new DevBaseDistrict();
                devBaseDistrict.setOrderNo(i + 1);
                devBaseDistrict.setId((long) i);
                devBaseDistrict.setName("分区" + i);
                devBaseDistrict.setLux(0);
                devBaseDistrict.setSwitchStatus(0);
                districtList.add(devBaseDistrict);
            }
            devBaseDistrictMapper.insertBatch(districtList);
        }
        if (lightAddFlag) {
//            List<DevZoneCombination> INIT_LIST = Arrays.asList(
//                new DevZoneCombination(4L, 4L, "装饰灯照明", ""),
//                new DevZoneCombination(5L, 5L, "站台照明", ""),
//                new DevZoneCombination(6L, 6L, "站厅照明", ""),
//                new DevZoneCombination(7L, 7L, "站台灯带照明", "")
//            );

            List<DevLightZoneCombination> LIGHT_INIT_LIST = Arrays.asList(
                new DevLightZoneCombination(4L, 4L, "装饰灯照明", ""),
                new DevLightZoneCombination(5L, 5L, "站台照明", ""),
                new DevLightZoneCombination(6L, 6L, "站厅照明", ""),
                new DevLightZoneCombination(7L, 7L, "站台灯带照明", "")
            );

            try {
//                List<DevZoneCombination> s = combinationMapper.selectList(new LambdaQueryWrapper<DevZoneCombination>()
//                    .select(DevZoneCombination::getZoneId)
//                    .in(DevZoneCombination::getZoneId, Arrays.asList(4L, 5L, 6L, 7L))
//                );
//                if (s == null || s.isEmpty()) {
//                    combinationMapper.insertBatch(INIT_LIST);
//                } else {
//                    Set<Long> collect = s.stream().map(DevZoneCombination::getZoneId).collect(Collectors.toSet());
//                    List<DevZoneCombination> insertList = INIT_LIST.stream().filter(z -> !collect.contains(z.getZoneId())).collect(Collectors.toList());
//                    if (!insertList.isEmpty()) {
//                        combinationMapper.insertBatch(insertList);
//                    }
//                }

                List<DevLightZoneCombination> l = devLightZoneCombinationMapper.selectList(new LambdaQueryWrapper<DevLightZoneCombination>()
                    .select(DevLightZoneCombination::getZoneId)
                    .in(DevLightZoneCombination::getZoneId, Arrays.asList(4L, 5L, 6L, 7L))
                );
                if (l == null || l.isEmpty()) {
                    devLightZoneCombinationMapper.insertBatch(LIGHT_INIT_LIST);
                } else {
                    Set<Long> collect = l.stream().map(DevLightZoneCombination::getZoneId).collect(Collectors.toSet());
                    List<DevLightZoneCombination> insertList = LIGHT_INIT_LIST.stream().filter(z -> !collect.contains(z.getZoneId())).collect(Collectors.toList());
                    if (!insertList.isEmpty()) {
                        devLightZoneCombinationMapper.insertBatch(insertList);
                    }
                }
            } catch (Exception e) {
                log.warn("初始化照明分区组合数据异常", e);
            }
        }
    }
}
