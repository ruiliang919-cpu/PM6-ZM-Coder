package com.ruoyi.zm.mapper;

import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.zm.domain.DevConfigDistrict;
import com.ruoyi.zm.domain.vo.DevConfigDistrictVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分区配置Mapper接口
 *
 * @author ruoyi
 * @date 2024-08-26
 */
public interface DevConfigDistrictMapper extends BaseMapperPlus<DevConfigDistrictMapper, DevConfigDistrict, DevConfigDistrictVo> {

    @Select("SELECT DISTINCT district_id, name FROM dev_config_district")
    List<DevConfigDistrict> selectAllDistinctDistrictId();
}
