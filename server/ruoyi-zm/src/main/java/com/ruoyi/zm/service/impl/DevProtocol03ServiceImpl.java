package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevProtocol03;
import com.ruoyi.zm.domain.bo.DevProtocol03Bo;
import com.ruoyi.zm.domain.vo.DevProtocol03Vo;
import com.ruoyi.zm.mapper.DevProtocol03Mapper;
import com.ruoyi.zm.service.IDevProtocol03Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 遥测协议Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@RequiredArgsConstructor
@Service
public class DevProtocol03ServiceImpl implements IDevProtocol03Service {

    private final DevProtocol03Mapper baseMapper;

    /**
     * 查询遥测协议
     */
    @Override
    public DevProtocol03Vo queryById(Long id){
        //return baseMapper.selectVoById(id);
        return null;
    }

    /**
     * 查询遥测协议列表
     */
    @Override
    public TableDataInfo<DevProtocol03Vo> queryPageList(DevProtocol03Bo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevProtocol03> lqw = buildQueryWrapper(bo);
        Page<DevProtocol03Vo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询遥测协议列表
     */
    @Override
    public List<DevProtocol03Vo> queryList(DevProtocol03Bo bo) {
        LambdaQueryWrapper<DevProtocol03> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevProtocol03> buildQueryWrapper(DevProtocol03Bo bo) {
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

    /**
     * 新增遥测协议
     */
    @Override
    public Boolean insertByBo(DevProtocol03Bo bo) {
        DevProtocol03 add = BeanUtil.toBean(bo, DevProtocol03.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改遥测协议
     */
    @Override
    public Boolean updateByBo(DevProtocol03Bo bo) {
        DevProtocol03 update = BeanUtil.toBean(bo, DevProtocol03.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevProtocol03 entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除遥测协议
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
