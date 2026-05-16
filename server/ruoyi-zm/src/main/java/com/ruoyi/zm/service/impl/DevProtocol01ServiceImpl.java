package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevProtocol01;
import com.ruoyi.zm.domain.bo.DevProtocol01Bo;
import com.ruoyi.zm.domain.vo.DevProtocol01Vo;
import com.ruoyi.zm.mapper.DevProtocol01Mapper;
import com.ruoyi.zm.service.IDevProtocol01Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 遥信协议Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@RequiredArgsConstructor
@Service
public class DevProtocol01ServiceImpl implements IDevProtocol01Service {

    private final DevProtocol01Mapper baseMapper;

    /**
     * 查询遥信协议
     */
    @Override
    public DevProtocol01Vo queryById(Long id){
        //return baseMapper.selectVoById(id);
        return null;
    }

    /**
     * 查询遥信协议列表
     */
    @Override
    public TableDataInfo<DevProtocol01Vo> queryPageList(DevProtocol01Bo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevProtocol01> lqw = buildQueryWrapper(bo);
        Page<DevProtocol01Vo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询遥信协议列表
     */
    @Override
    public List<DevProtocol01Vo> queryList(DevProtocol01Bo bo) {
        LambdaQueryWrapper<DevProtocol01> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevProtocol01> buildQueryWrapper(DevProtocol01Bo bo) {
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

    /**
     * 新增遥信协议
     */
    @Override
    public Boolean insertByBo(DevProtocol01Bo bo) {
        DevProtocol01 add = BeanUtil.toBean(bo, DevProtocol01.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改遥信协议
     */
    @Override
    public Boolean updateByBo(DevProtocol01Bo bo) {
        DevProtocol01 update = BeanUtil.toBean(bo, DevProtocol01.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevProtocol01 entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除遥信协议
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
