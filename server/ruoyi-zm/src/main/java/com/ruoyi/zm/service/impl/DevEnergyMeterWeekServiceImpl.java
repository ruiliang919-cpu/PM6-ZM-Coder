package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevEnergyMeterWeek;
import com.ruoyi.zm.domain.bo.DevEnergyMeterWeekBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterWeekVo;
import com.ruoyi.zm.mapper.DevEnergyMeterWeekMapper;
import com.ruoyi.zm.service.IDevEnergyMeterWeekService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 十六电周耗电量Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@RequiredArgsConstructor
@Service
public class DevEnergyMeterWeekServiceImpl implements IDevEnergyMeterWeekService {

    private final DevEnergyMeterWeekMapper baseMapper;

    /**
     * 查询十六电周耗电量
     */
    @Override
    public DevEnergyMeterWeekVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询十六电周耗电量列表
     */
    @Override
    public TableDataInfo<DevEnergyMeterWeekVo> queryPageList(DevEnergyMeterWeekBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevEnergyMeterWeek> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(DevEnergyMeterWeek::getTimestamp);
        lqw.orderByAsc(DevEnergyMeterWeek::getEnergyMeterNo);
        Page<DevEnergyMeterWeekVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询十六电周耗电量列表
     */
    @Override
    public List<DevEnergyMeterWeekVo> queryList(DevEnergyMeterWeekBo bo) {
        LambdaQueryWrapper<DevEnergyMeterWeek> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevEnergyMeterWeek> buildQueryWrapper(DevEnergyMeterWeekBo bo) {
        LambdaQueryWrapper<DevEnergyMeterWeek> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceNo() != null, DevEnergyMeterWeek::getDeviceNo, bo.getDeviceNo());
        lqw.eq(bo.getEnergyMeterNo() != null, DevEnergyMeterWeek::getEnergyMeterNo, bo.getEnergyMeterNo());
        lqw.eq(bo.getPower() != null, DevEnergyMeterWeek::getPower, bo.getPower());
        lqw.eq(bo.getTimestamp() != null, DevEnergyMeterWeek::getTimestamp, bo.getTimestamp());
        lqw.orderByDesc(DevEnergyMeterWeek::getTimestamp);
        return lqw;
    }

    /**
     * 新增十六电周耗电量
     */
    @Override
    public Boolean insertByBo(DevEnergyMeterWeekBo bo) {
        DevEnergyMeterWeek add = BeanUtil.toBean(bo, DevEnergyMeterWeek.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改十六电周耗电量
     */
    @Override
    public Boolean updateByBo(DevEnergyMeterWeekBo bo) {
        DevEnergyMeterWeek update = BeanUtil.toBean(bo, DevEnergyMeterWeek.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevEnergyMeterWeek entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除十六电周耗电量
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
