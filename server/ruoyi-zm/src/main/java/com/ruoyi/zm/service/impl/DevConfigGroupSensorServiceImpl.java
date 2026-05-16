package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigGroupSensor;
import com.ruoyi.zm.domain.bo.DevConfigGroupSensorBo;
import com.ruoyi.zm.domain.vo.DevConfigGroupSensorVo;
import com.ruoyi.zm.mapper.DevConfigGroupSensorMapper;
import com.ruoyi.zm.service.IDevConfigGroupSensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 传感器与分组对应Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@RequiredArgsConstructor
@Service
public class DevConfigGroupSensorServiceImpl implements IDevConfigGroupSensorService {

    private final DevConfigGroupSensorMapper baseMapper;

    /**
     * 查询传感器与分组对应
     */
    @Override
    public DevConfigGroupSensorVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询传感器与分组对应列表
     */
    @Override
    public TableDataInfo<DevConfigGroupSensorVo> queryPageList(DevConfigGroupSensorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigGroupSensor> lqw = buildQueryWrapper(bo);
        Page<DevConfigGroupSensorVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询传感器与分组对应列表
     */
    @Override
    public List<DevConfigGroupSensorVo> queryList(DevConfigGroupSensorBo bo) {
        LambdaQueryWrapper<DevConfigGroupSensor> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigGroupSensor> buildQueryWrapper(DevConfigGroupSensorBo bo) {
        LambdaQueryWrapper<DevConfigGroupSensor> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigGroupSensor::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getGroupId() != null, DevConfigGroupSensor::getGroupId, bo.getGroupId());
        lqw.eq(bo.getInfraredSensorId() != null, DevConfigGroupSensor::getInfraredSensorId, bo.getInfraredSensorId());
        lqw.eq(bo.getIlluminanceSensorId() != null, DevConfigGroupSensor::getIlluminanceSensorId, bo.getIlluminanceSensorId());
        return lqw;
    }

    /**
     * 新增传感器与分组对应
     */
    @Override
    public Boolean insertByBo(DevConfigGroupSensorBo bo) {
        DevConfigGroupSensor add = BeanUtil.toBean(bo, DevConfigGroupSensor.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改传感器与分组对应
     */
    @Override
    public Boolean updateByBo(DevConfigGroupSensorBo bo) {
        DevConfigGroupSensor update = BeanUtil.toBean(bo, DevConfigGroupSensor.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigGroupSensor entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除传感器与分组对应
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
