package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevProtocol06;
import com.ruoyi.zm.domain.bo.DevProtocol06Bo;
import com.ruoyi.zm.domain.vo.DevProtocol06Vo;
import com.ruoyi.zm.mapper.DevProtocol06Mapper;
import com.ruoyi.zm.service.IDevProtocol06Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 遥控协议Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Service
public class DevProtocol06ServiceImpl extends BaseProtocolServiceImpl<DevProtocol06Mapper, DevProtocol06, DevProtocol06Bo, DevProtocol06Vo> implements IDevProtocol06Service {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected LambdaQueryWrapper<DevProtocol06> buildQueryWrapper(DevProtocol06Bo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevProtocol06> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getAddr()), DevProtocol06::getAddr, bo.getAddr());
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevProtocol06::getName, bo.getName());
        lqw.eq(bo.getMagnification() != null, DevProtocol06::getMagnification, bo.getMagnification());
        lqw.eq(StringUtils.isNotBlank(bo.getUnit()), DevProtocol06::getUnit, bo.getUnit());
        lqw.eq(StringUtils.isNotBlank(bo.getReadWrite()), DevProtocol06::getReadWrite, bo.getReadWrite());
        lqw.eq(StringUtils.isNotBlank(bo.getMemo()), DevProtocol06::getMemo, bo.getMemo());
        return lqw;
    }

    @Override
    protected DevProtocol06 convertToEntity(DevProtocol06Bo bo) {
        return BeanUtil.toBean(bo, DevProtocol06.class);
    }

    @Override
    public List<DevProtocol06> get06List() {
        List<DevProtocol06> devProtocol06s = (List<DevProtocol06>) redisTemplate.opsForValue().get("zm:devProtocol06sList");
        if (ObjectUtils.isEmpty(devProtocol06s)) {
            devProtocol06s = baseMapper.selectList();
            redisTemplate.opsForValue().set("zm:devProtocol06sList", devProtocol06s, 2, TimeUnit.DAYS);
        }
        return devProtocol06s;
    }
}
