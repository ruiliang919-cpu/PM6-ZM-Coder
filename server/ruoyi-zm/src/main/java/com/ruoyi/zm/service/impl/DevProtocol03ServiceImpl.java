package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevProtocol03;
import com.ruoyi.zm.domain.bo.DevProtocol03Bo;
import com.ruoyi.zm.domain.vo.DevProtocol03Vo;
import com.ruoyi.zm.mapper.DevProtocol03Mapper;
import com.ruoyi.zm.service.IDevProtocol03Service;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 遥测协议Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Service
public class DevProtocol03ServiceImpl extends BaseProtocolServiceImpl<DevProtocol03Mapper, DevProtocol03, DevProtocol03Bo, DevProtocol03Vo> implements IDevProtocol03Service {

    @Override
    protected LambdaQueryWrapper<DevProtocol03> buildQueryWrapper(DevProtocol03Bo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevProtocol03> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getAddr()), DevProtocol03::getAddr, bo.getAddr());
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevProtocol03::getName, bo.getName());
        lqw.eq(bo.getMagnification() != null, DevProtocol03::getMagnification, bo.getMagnification());
        lqw.eq(StringUtils.isNotBlank(bo.getUnit()), DevProtocol03::getUnit, bo.getUnit());
        lqw.eq(StringUtils.isNotBlank(bo.getReadWrite()), DevProtocol03::getReadWrite, bo.getReadWrite());
        lqw.eq(StringUtils.isNotBlank(bo.getMemo()), DevProtocol03::getMemo, bo.getMemo());
        return lqw;
    }

    @Override
    protected DevProtocol03 convertToEntity(DevProtocol03Bo bo) {
        return BeanUtil.toBean(bo, DevProtocol03.class);
    }
}
