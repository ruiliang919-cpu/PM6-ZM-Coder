package com.ruoyi.zm.mapper;

import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.zm.domain.DevLightZoneCombination;
import com.ruoyi.zm.domain.vo.DevLightZoneCombinationVo;
import org.apache.ibatis.annotations.Delete;

/**
 * 上位机控制照明的照明分区组合Mapper接口
 *
 * @author ruoyi
 * @date 2024-11-29
 */
public interface DevLightZoneCombinationMapper extends BaseMapperPlus<DevLightZoneCombinationMapper, DevLightZoneCombination, DevLightZoneCombinationVo> {
    @Delete("DELETE FROM dev_light_zone_combination")
    void delete();
}
