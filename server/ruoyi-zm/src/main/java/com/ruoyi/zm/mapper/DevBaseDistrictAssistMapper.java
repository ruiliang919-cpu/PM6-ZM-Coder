package com.ruoyi.zm.mapper;

import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.zm.domain.DevBaseDistrictAssist;
import com.ruoyi.zm.domain.vo.DevBaseDistrictAssistVo;
import org.apache.ibatis.annotations.Delete;

/**
 * 照明控制的分区控制的辅助Mapper接口
 *
 * @author ruoyi
 * @date 2024-08-31
 */
public interface DevBaseDistrictAssistMapper extends BaseMapperPlus<DevBaseDistrictAssistMapper, DevBaseDistrictAssist, DevBaseDistrictAssistVo> {

    @Delete("DELETE FROM dev_base_district_assist")
    void delete();
}
