package com.ruoyi.zm.mapper;

import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.zm.domain.DevZoneCombination;
import com.ruoyi.zm.domain.vo.DevZoneCombinationVo;
import org.apache.ibatis.annotations.Delete;

/**
 * 分区组合Mapper接口
 *
 * @author ruoyi
 * @date 2024-10-08
 */
public interface DevZoneCombinationMapper extends BaseMapperPlus<DevZoneCombinationMapper, DevZoneCombination, DevZoneCombinationVo> {

    @Delete("DELETE FROM dev_zone_combination")
    void delete();
}
