package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevEnergyMeterYear;
import com.ruoyi.zm.domain.bo.DevEnergyMeterYearBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterYearVo;
import com.ruoyi.zm.mapper.DevEnergyMeterYearMapper;
import com.ruoyi.zm.service.IDevEnergyMeterYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 十六电年耗电量Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@RequiredArgsConstructor
@Service
public class DevEnergyMeterYearServiceImpl implements IDevEnergyMeterYearService {

    private final DevEnergyMeterYearMapper baseMapper;

    /**
     * 查询十六电年耗电量
     */
    @Override
    public DevEnergyMeterYearVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询十六电年耗电量列表
     */
    @Override
    public TableDataInfo<DevEnergyMeterYearVo> queryPageList(DevEnergyMeterYearBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevEnergyMeterYear> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(DevEnergyMeterYear::getTimestamp);
        lqw.orderByAsc(DevEnergyMeterYear::getEnergyMeterNo);
        Page<DevEnergyMeterYearVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询十六电年耗电量列表
     */
    @Override
    public List<DevEnergyMeterYearVo> queryList(DevEnergyMeterYearBo bo) {
        LambdaQueryWrapper<DevEnergyMeterYear> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevEnergyMeterYear> buildQueryWrapper(DevEnergyMeterYearBo bo) {
        LambdaQueryWrapper<DevEnergyMeterYear> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceNo() != null, DevEnergyMeterYear::getDeviceNo, bo.getDeviceNo());
        lqw.eq(bo.getEnergyMeterNo() != null, DevEnergyMeterYear::getEnergyMeterNo, bo.getEnergyMeterNo());
        lqw.eq(bo.getPower() != null, DevEnergyMeterYear::getPower, bo.getPower());
        lqw.eq(bo.getTimestamp() != null, DevEnergyMeterYear::getTimestamp, bo.getTimestamp());
        lqw.orderByDesc(DevEnergyMeterYear::getTimestamp);
        return lqw;
    }

    /**
     * 新增十六电年耗电量
     */
    @Override
    public Boolean insertByBo(DevEnergyMeterYearBo bo) {
        DevEnergyMeterYear add = BeanUtil.toBean(bo, DevEnergyMeterYear.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改十六电年耗电量
     */
    @Override
    public Boolean updateByBo(DevEnergyMeterYearBo bo) {
        DevEnergyMeterYear update = BeanUtil.toBean(bo, DevEnergyMeterYear.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevEnergyMeterYear entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除十六电年耗电量
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
