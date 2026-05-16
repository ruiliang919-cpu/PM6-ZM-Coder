package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevEnergyMeterDay;
import com.ruoyi.zm.domain.bo.DevEnergyMeterDayBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterDayVo;
import com.ruoyi.zm.mapper.DevEnergyMeterDayMapper;
import com.ruoyi.zm.service.IDevEnergyMeterDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 十六电日耗电量Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@RequiredArgsConstructor
@Service
public class DevEnergyMeterDayServiceImpl implements IDevEnergyMeterDayService {

    private final DevEnergyMeterDayMapper baseMapper;

    /**
     * 查询十六电日耗电量
     */
    @Override
    public DevEnergyMeterDayVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询十六电日耗电量列表
     */
    @Override
    public TableDataInfo<DevEnergyMeterDayVo> queryPageList(DevEnergyMeterDayBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevEnergyMeterDay> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(DevEnergyMeterDay::getTimestamp);
        lqw.orderByAsc(DevEnergyMeterDay::getEnergyMeterNo);
        Page<DevEnergyMeterDayVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询十六电日耗电量列表
     */
    @Override
    public List<DevEnergyMeterDayVo> queryList(DevEnergyMeterDayBo bo) {
        LambdaQueryWrapper<DevEnergyMeterDay> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevEnergyMeterDay> buildQueryWrapper(DevEnergyMeterDayBo bo) {
        LambdaQueryWrapper<DevEnergyMeterDay> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceNo() != null, DevEnergyMeterDay::getDeviceNo, bo.getDeviceNo());
        lqw.eq(bo.getEnergyMeterNo() != null, DevEnergyMeterDay::getEnergyMeterNo, bo.getEnergyMeterNo());
        lqw.eq(bo.getPower() != null, DevEnergyMeterDay::getPower, bo.getPower());
        lqw.eq(bo.getTimestamp() != null, DevEnergyMeterDay::getTimestamp, bo.getTimestamp());
        lqw.orderByDesc(DevEnergyMeterDay::getTimestamp);
        return lqw;
    }

    /**
     * 新增十六电日耗电量
     */
    @Override
    public Boolean insertByBo(DevEnergyMeterDayBo bo) {
        DevEnergyMeterDay add = BeanUtil.toBean(bo, DevEnergyMeterDay.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改十六电日耗电量
     */
    @Override
    public Boolean updateByBo(DevEnergyMeterDayBo bo) {
        DevEnergyMeterDay update = BeanUtil.toBean(bo, DevEnergyMeterDay.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevEnergyMeterDay entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除十六电日耗电量
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
