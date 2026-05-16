package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevProtocol06;
import com.ruoyi.zm.domain.bo.DevProtocol06Bo;
import com.ruoyi.zm.domain.vo.DevProtocol06Vo;
import com.ruoyi.zm.mapper.DevProtocol06Mapper;
import com.ruoyi.zm.service.IDevProtocol06Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 遥控协议Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@RequiredArgsConstructor
@Service
public class DevProtocol06ServiceImpl implements IDevProtocol06Service {

    private final DevProtocol06Mapper baseMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询遥控协议
     */
    @Override
    public DevProtocol06Vo queryById(Long id) {
        // return baseMapper.selectVoById(id);
        return null;
    }

    /**
     * 查询遥控协议列表
     */
    @Override
    public TableDataInfo<DevProtocol06Vo> queryPageList(DevProtocol06Bo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevProtocol06> lqw = buildQueryWrapper(bo);
        Page<DevProtocol06Vo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询遥控协议列表
     */
    @Override
    public List<DevProtocol06Vo> queryList(DevProtocol06Bo bo) {
        LambdaQueryWrapper<DevProtocol06> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevProtocol06> buildQueryWrapper(DevProtocol06Bo bo) {
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

    /**
     * 新增遥控协议
     */
    @Override
    public Boolean insertByBo(DevProtocol06Bo bo) {
        DevProtocol06 add = BeanUtil.toBean(bo, DevProtocol06.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改遥控协议
     */
    @Override
    public Boolean updateByBo(DevProtocol06Bo bo) {
        DevProtocol06 update = BeanUtil.toBean(bo, DevProtocol06.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevProtocol06 entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除遥控协议
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
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
