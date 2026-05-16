package com.ruoyi.zm.mapper;

import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.zm.domain.DevWriteInstruct;
import com.ruoyi.zm.domain.vo.DevWriteInstructVo;
import org.apache.ibatis.annotations.Delete;

public interface DevWriteInstructMapper extends BaseMapperPlus<DevInstructMapper, DevWriteInstruct, DevWriteInstructVo> {

    @Delete("DELETE FROM dev_write_instruct")
    void clearTable();
}
