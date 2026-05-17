package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevProtocol05;
import com.ruoyi.zm.domain.bo.DevProtocol05Bo;
import com.ruoyi.zm.domain.vo.DevProtocol05Vo;
import com.ruoyi.zm.mapper.DevProtocol05Mapper;
import com.ruoyi.zm.service.IDevProtocol05Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 遥调协议Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Service
public class DevProtocol05ServiceImpl extends BaseProtocolServiceImpl<DevProtocol05Mapper, DevProtocol05, DevProtocol05Bo, DevProtocol05Vo> implements IDevProtocol05Service {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public List<DevProtocol05> get05List() {
        List<DevProtocol05> devProtocol05s = (List<DevProtocol05>) redisTemplate.opsForValue().get("zm:devProtocol05sList");
        if (ObjectUtils.isEmpty(devProtocol05s)) {
            devProtocol05s = baseMapper.selectList();
            redisTemplate.opsForValue().set("zm:devProtocol05sList", devProtocol05s, 2, TimeUnit.DAYS);
        }
        // System.out.println("devProtocol05s:"+devProtocol05s);
        return devProtocol05s;
    }

    @Override
    protected LambdaQueryWrapper<DevProtocol05> buildQueryWrapper(DevProtocol05Bo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevProtocol05> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getAddr()), DevProtocol05::getAddr, bo.getAddr());
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevProtocol05::getName, bo.getName());
        lqw.eq(bo.getMagnification() != null, DevProtocol05::getMagnification, bo.getMagnification());
        lqw.eq(StringUtils.isNotBlank(bo.getUnit()), DevProtocol05::getUnit, bo.getUnit());
        lqw.eq(StringUtils.isNotBlank(bo.getReadWrite()), DevProtocol05::getReadWrite, bo.getReadWrite());
        lqw.eq(StringUtils.isNotBlank(bo.getMemo()), DevProtocol05::getMemo, bo.getMemo());
        return lqw;
    }

    @Override
    protected DevProtocol05 convertToEntity(DevProtocol05Bo bo) {
        return BeanUtil.toBean(bo, DevProtocol05.class);
    }

    // 根据遥测协议的地址找到对应的名称
    public DevProtocol05 getByProtocolAddr(String addr) {
        DevProtocol05 devProtocol05 = (DevProtocol05) redisTemplate.opsForValue().get("zm:devProtocol05:addr:" + addr);
        if (ObjectUtils.isEmpty(devProtocol05)) {
            Optional<DevProtocol05> optionalProtocol = get05List().stream()
                .filter(value -> value.getAddr().equals(addr))
                .findFirst();
            if (optionalProtocol.isPresent()) {
                redisTemplate.opsForValue().set("zm:devProtocol05:addr:" + addr, optionalProtocol.get(), 2, TimeUnit.DAYS);
                return optionalProtocol.get();
            } else {
                return new DevProtocol05();
            }
        }
        return devProtocol05;
    }
}
