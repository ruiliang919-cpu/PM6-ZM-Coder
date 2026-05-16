package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevEnergyMeterMonth;
import com.ruoyi.zm.domain.bo.DevEnergyMeterMonthBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterMonthVo;
import com.ruoyi.zm.mapper.DevEnergyMeterMonthMapper;
import com.ruoyi.zm.service.IDevEnergyMeterMonthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 十六电月耗电量Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@RequiredArgsConstructor
@Service
public class DevEnergyMeterMonthServiceImpl implements IDevEnergyMeterMonthService {

    private final DevEnergyMeterMonthMapper baseMapper;

    /**
     * 查询十六电月耗电量
     */
    @Override
    public DevEnergyMeterMonthVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询十六电月耗电量列表
     */
    @Override
    public TableDataInfo<DevEnergyMeterMonthVo> queryPageList(DevEnergyMeterMonthBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevEnergyMeterMonth> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(DevEnergyMeterMonth::getTimestamp);
        lqw.orderByAsc(DevEnergyMeterMonth::getEnergyMeterNo);
        Page<DevEnergyMeterMonthVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询十六电月耗电量列表
     */
    @Override
    public List<DevEnergyMeterMonthVo> queryList(DevEnergyMeterMonthBo bo) {
        LambdaQueryWrapper<DevEnergyMeterMonth> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevEnergyMeterMonth> buildQueryWrapper(DevEnergyMeterMonthBo bo) {
        LambdaQueryWrapper<DevEnergyMeterMonth> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceNo() != null, DevEnergyMeterMonth::getDeviceNo, bo.getDeviceNo());
        lqw.eq(bo.getEnergyMeterNo() != null, DevEnergyMeterMonth::getEnergyMeterNo, bo.getEnergyMeterNo());
        lqw.eq(bo.getPower() != null, DevEnergyMeterMonth::getPower, bo.getPower());
        lqw.eq(bo.getTimestamp() != null, DevEnergyMeterMonth::getTimestamp, bo.getTimestamp());
        lqw.orderByDesc(DevEnergyMeterMonth::getTimestamp);
        return lqw;
    }

    /**
     * 新增十六电月耗电量
     */
    @Override
    public Boolean insertByBo(DevEnergyMeterMonthBo bo) {
        DevEnergyMeterMonth add = BeanUtil.toBean(bo, DevEnergyMeterMonth.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改十六电月耗电量
     */
    @Override
    public Boolean updateByBo(DevEnergyMeterMonthBo bo) {
        DevEnergyMeterMonth update = BeanUtil.toBean(bo, DevEnergyMeterMonth.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevEnergyMeterMonth entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除十六电月耗电量
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
