package com.ruoyi.zm.mapper;

import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.zm.domain.DevBaseDistrict;
import com.ruoyi.zm.domain.vo.DevBaseDistrictVo;
import org.apache.ibatis.annotations.Delete;

/**
 * 控制分区Mapper接口
 *
 * @author mophi
 * @date 2024-07-11
 */
public interface DevBaseDistrictMapper extends BaseMapperPlus<DevBaseDistrictMapper, DevBaseDistrict, DevBaseDistrictVo> {

    @Delete("DELETE FROM dev_base_district")
    void delete();
}
