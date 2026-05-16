package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigIlluminanceSensor;
import com.ruoyi.zm.domain.bo.DevConfigIlluminanceSensorBo;
import com.ruoyi.zm.domain.vo.DevConfigIlluminanceSensorVo;
import com.ruoyi.zm.mapper.DevConfigIlluminanceSensorMapper;
import com.ruoyi.zm.service.IDevConfigIlluminanceSensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 系统设置-控制方式-照度模式 传感器Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@RequiredArgsConstructor
@Service
public class DevConfigIlluminanceSensorServiceImpl implements IDevConfigIlluminanceSensorService {

    private final DevConfigIlluminanceSensorMapper baseMapper;

    /**
     * 查询系统设置-控制方式-照度模式 传感器
     */
    @Override
    public DevConfigIlluminanceSensorVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询系统设置-控制方式-照度模式 传感器列表
     */
    @Override
    public TableDataInfo<DevConfigIlluminanceSensorVo> queryPageList(DevConfigIlluminanceSensorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigIlluminanceSensor> lqw = buildQueryWrapper(bo);
        Page<DevConfigIlluminanceSensorVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询系统设置-控制方式-照度模式 传感器列表
     */
    @Override
    public List<DevConfigIlluminanceSensorVo> queryList(DevConfigIlluminanceSensorBo bo) {
        LambdaQueryWrapper<DevConfigIlluminanceSensor> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigIlluminanceSensor> buildQueryWrapper(DevConfigIlluminanceSensorBo bo) {
        LambdaQueryWrapper<DevConfigIlluminanceSensor> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigIlluminanceSensor::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getSensorId() != null, DevConfigIlluminanceSensor::getSensorId, bo.getSensorId());
        lqw.eq(bo.getIlluminanceLux() != null, DevConfigIlluminanceSensor::getIlluminanceLux, bo.getIlluminanceLux());
        lqw.eq(bo.getConstantIlluminanceLux() != null, DevConfigIlluminanceSensor::getConstantIlluminanceLux, bo.getConstantIlluminanceLux());
        lqw.eq(bo.getHysteresisLux() != null, DevConfigIlluminanceSensor::getHysteresisLux, bo.getHysteresisLux());
        lqw.eq(bo.getAdjustmentTimeSeconds() != null, DevConfigIlluminanceSensor::getAdjustmentTimeSeconds, bo.getAdjustmentTimeSeconds());
        lqw.eq(bo.getExternalControlEnabled() != null, DevConfigIlluminanceSensor::getExternalControlEnabled, bo.getExternalControlEnabled());
        // lqw.eq(bo.getGroupId() != null, DevConfigIlluminanceSensor::getGroupId, bo.getGroupId());
        lqw.eq(bo.getEnabled() != null, DevConfigIlluminanceSensor::getEnabled, bo.getEnabled());
        lqw.eq(bo.getOutControlStatus() != null, DevConfigIlluminanceSensor::getOutControlStatus, bo.getOutControlStatus());
        lqw.eq(bo.getOutControlChannel() != null, DevConfigIlluminanceSensor::getOutControlChannel, bo.getOutControlChannel());
        lqw.eq(bo.getOutControlAddr() != null, DevConfigIlluminanceSensor::getOutControlAddr, bo.getOutControlAddr());
        return lqw;
    }

    /**
     * 新增系统设置-控制方式-照度模式 传感器
     */
    @Override
    public Boolean insertByBo(DevConfigIlluminanceSensorBo bo) {
        DevConfigIlluminanceSensor add = BeanUtil.toBean(bo, DevConfigIlluminanceSensor.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改系统设置-控制方式-照度模式 传感器
     */
    @Override
    public Boolean updateByBo(DevConfigIlluminanceSensorBo bo) {
        DevConfigIlluminanceSensor update = BeanUtil.toBean(bo, DevConfigIlluminanceSensor.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigIlluminanceSensor entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除系统设置-控制方式-照度模式 传感器
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
