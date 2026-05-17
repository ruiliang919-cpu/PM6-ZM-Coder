package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevProtocol01;
import com.ruoyi.zm.domain.bo.DevProtocol01Bo;
import com.ruoyi.zm.domain.vo.DevProtocol01Vo;
import com.ruoyi.zm.mapper.DevProtocol01Mapper;
import com.ruoyi.zm.service.IDevProtocol01Service;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 遥信协议Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Service
public class DevProtocol01ServiceImpl extends BaseProtocolServiceImpl<DevProtocol01Mapper, DevProtocol01, DevProtocol01Bo, DevProtocol01Vo> implements IDevProtocol01Service {

    @Override
    protected LambdaQueryWrapper<DevProtocol01> buildQueryWrapper(DevProtocol01Bo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevProtocol01> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getAddr()), DevProtocol01::getAddr, bo.getAddr());
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevProtocol01::getName, bo.getName());
        lqw.eq(bo.getMagnification() != null, DevProtocol01::getMagnification, bo.getMagnification());
        lqw.eq(StringUtils.isNotBlank(bo.getUnit()), DevProtocol01::getUnit, bo.getUnit());
        lqw.eq(StringUtils.isNotBlank(bo.getReadWrite()), DevProtocol01::getReadWrite, bo.getReadWrite());
        lqw.eq(StringUtils.isNotBlank(bo.getMemo()), DevProtocol01::getMemo, bo.getMemo());
        return lqw;
    }

    @Override
    protected DevProtocol01 convertToEntity(DevProtocol01Bo bo) {
        return BeanUtil.toBean(bo, DevProtocol01.class);
    }
}
