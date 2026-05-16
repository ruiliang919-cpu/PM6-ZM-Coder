package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigInfraredSensor;
import com.ruoyi.zm.domain.bo.DevConfigInfraredSensorBo;
import com.ruoyi.zm.domain.vo.DevConfigInfraredSensorVo;
import com.ruoyi.zm.mapper.DevConfigInfraredSensorMapper;
import com.ruoyi.zm.service.IDevConfigInfraredSensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 时控Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@RequiredArgsConstructor
@Service
public class DevConfigInfraredSensorServiceImpl implements IDevConfigInfraredSensorService {

    private final DevConfigInfraredSensorMapper baseMapper;

    /**
     * 查询时控
     */
    @Override
    public DevConfigInfraredSensorVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询时控列表
     */
    @Override
    public TableDataInfo<DevConfigInfraredSensorVo> queryPageList(DevConfigInfraredSensorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigInfraredSensor> lqw = buildQueryWrapper(bo);
        Page<DevConfigInfraredSensorVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询时控列表
     */
    @Override
    public List<DevConfigInfraredSensorVo> queryList(DevConfigInfraredSensorBo bo) {
        LambdaQueryWrapper<DevConfigInfraredSensor> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigInfraredSensor> buildQueryWrapper(DevConfigInfraredSensorBo bo) {
        LambdaQueryWrapper<DevConfigInfraredSensor> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigInfraredSensor::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getSensorId() != null, DevConfigInfraredSensor::getSensorId, bo.getSensorId());
        // lqw.eq(bo.getGroupId() != null, DevConfigInfraredSensor::getGroupId, bo.getGroupId());
        lqw.eq(bo.getEnabled() != null, DevConfigInfraredSensor::getEnabled, bo.getEnabled());
        lqw.eq(bo.getInductiveLux() != null, DevConfigInfraredSensor::getInductiveLux, bo.getInductiveLux());
        lqw.eq(bo.getInductiveSwitchStatus() != null, DevConfigInfraredSensor::getInductiveSwitchStatus, bo.getInductiveSwitchStatus());
        lqw.eq(bo.getUninductionLux() != null, DevConfigInfraredSensor::getUninductionLux, bo.getUninductionLux());
        lqw.eq(bo.getUninductionSwitchStatus() != null, DevConfigInfraredSensor::getUninductionSwitchStatus, bo.getUninductionSwitchStatus());
        lqw.eq(bo.getDelayedTime() != null, DevConfigInfraredSensor::getDelayedTime, bo.getDelayedTime());
        return lqw;
    }

    /**
     * 新增时控
     */
    @Override
    public Boolean insertByBo(DevConfigInfraredSensorBo bo) {
        DevConfigInfraredSensor add = BeanUtil.toBean(bo, DevConfigInfraredSensor.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改时控
     */
    @Override
    public Boolean updateByBo(DevConfigInfraredSensorBo bo) {
        DevConfigInfraredSensor update = BeanUtil.toBean(bo, DevConfigInfraredSensor.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigInfraredSensor entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除时控
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
