package com.ruoyi.zm.mapper;

import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.domain.vo.DevInstructVo;
import org.apache.ibatis.annotations.Delete;

public interface DevInstructMapper extends BaseMapperPlus<DevInstructMapper, DevInstruct, DevInstructVo> {

    @Delete("DELETE FROM dev_instruct")
    void clearTable();
}
