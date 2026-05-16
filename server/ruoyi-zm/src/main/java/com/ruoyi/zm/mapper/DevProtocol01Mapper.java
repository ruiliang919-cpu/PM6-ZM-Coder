package com.ruoyi.zm.mapper;

import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.zm.domain.DevProtocol01;
import com.ruoyi.zm.domain.vo.DevProtocol01Vo;
import org.apache.ibatis.annotations.Delete;

/**
 * 遥信协议Mapper接口
 *
 * @author ruoyi
 * @date 2024-07-19
 */
public interface DevProtocol01Mapper extends BaseMapperPlus<DevProtocol01Mapper, DevProtocol01, DevProtocol01Vo> {

    @Delete("DELETE FROM dev_protocol_01")
    void deleteAll();
}
