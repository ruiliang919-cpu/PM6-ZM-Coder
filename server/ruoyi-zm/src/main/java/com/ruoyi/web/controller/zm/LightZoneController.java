package com.ruoyi.web.controller.zm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevLightZoneCombination;
import com.ruoyi.zm.domain.vo.DevZoneCombinationRespVo;
import com.ruoyi.zm.domain.vo.ZoneCombinationAddVo;
import com.ruoyi.zm.mapper.DevLightZoneCombinationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/light/zone")
@RequiredArgsConstructor
public class LightZoneController {
    private final DevLightZoneCombinationMapper lightZoneCombinationMapper;

    // 照明分区组合列表
    @GetMapping("/leftList")
    public TableDataInfo<?> leftList() {
        List<DevLightZoneCombination> source = lightZoneCombinationMapper.selectList();
        List<DevZoneCombinationRespVo> result = new ArrayList<>();
        if (!source.isEmpty()) {
            for (int i = 0; i < source.size(); i++) {
                DevZoneCombinationRespVo vo = new DevZoneCombinationRespVo();
                vo.setOrderNo((long) i + 1);
                vo.setName(source.get(i).getName());
                vo.setZoneId(source.get(i).getZoneId());
                vo.setZoneList(source.get(i).getZoneList().split(","));
                result.add(vo);
            }
            return TableDataInfo.build(result);
        }
        return TableDataInfo.build();
    }

    // 分区组合设置
    @PostMapping("/set")
    public R<?> set(@RequestBody ZoneCombinationAddVo addVo) {
        LambdaQueryWrapper<DevLightZoneCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevLightZoneCombination::getZoneId, addVo.getZoneId());
        DevLightZoneCombination source = lightZoneCombinationMapper.selectOne(lqw);

        if (addVo.getName() != null && !"".equals(addVo.getName())) {
            source.setName(addVo.getName());
        }

        if (addVo.getZones().length > 0) {
            StringBuilder zoneList = new StringBuilder();
            for (int i = 0; i < addVo.getZones().length; i++) {
                if (i == addVo.getZones().length - 1) {
                    zoneList.append(addVo.getZones()[i]);
                } else {
                    zoneList.append(addVo.getZones()[i]).append(",");
                }
            }
            source.setZoneList(String.valueOf(zoneList));
        }
        LambdaUpdateWrapper<DevLightZoneCombination> luw = new LambdaUpdateWrapper<>();
        luw.eq(DevLightZoneCombination::getZoneId, addVo.getZoneId());
        return lightZoneCombinationMapper.update(source, luw) > 0 ? R.ok("更新成功") : R.ok("更新失败");
    }
}
