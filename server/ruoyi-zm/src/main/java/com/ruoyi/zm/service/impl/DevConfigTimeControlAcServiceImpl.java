package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigTimeControlAc;
import com.ruoyi.zm.domain.bo.DevConfigTimeControlAcBo;
import com.ruoyi.zm.domain.vo.DevConfigTimeControlAcVo;
import com.ruoyi.zm.mapper.DevConfigTimeControlAcMapper;
import com.ruoyi.zm.service.IDevConfigTimeControlAcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 系统设置-控制方式-交流开关-时段信息 Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@RequiredArgsConstructor
@Service
public class DevConfigTimeControlAcServiceImpl implements IDevConfigTimeControlAcService {

    private final DevConfigTimeControlAcMapper baseMapper;

    /**
     * 查询系统设置-控制方式-交流开关-时段信息
     */
    @Override
    public DevConfigTimeControlAcVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询系统设置-控制方式-交流开关-时段信息 列表
     */
    @Override
    public TableDataInfo<DevConfigTimeControlAcVo> queryPageList(DevConfigTimeControlAcBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigTimeControlAc> lqw = buildQueryWrapper(bo);
        Page<DevConfigTimeControlAcVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询系统设置-控制方式-交流开关-时段信息 列表
     */
    @Override
    public List<DevConfigTimeControlAcVo> queryList(DevConfigTimeControlAcBo bo) {
        LambdaQueryWrapper<DevConfigTimeControlAc> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigTimeControlAc> buildQueryWrapper(DevConfigTimeControlAcBo bo) {
        LambdaQueryWrapper<DevConfigTimeControlAc> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigTimeControlAc::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getFrameId() != null, DevConfigTimeControlAc::getFrameId, bo.getFrameId());
        lqw.eq(bo.getSwitchStatus() != null, DevConfigTimeControlAc::getSwitchStatus, bo.getSwitchStatus());
        lqw.eq(bo.getEnable() != null, DevConfigTimeControlAc::getEnable, bo.getEnable());
        lqw.eq(bo.getStimeHour() != null, DevConfigTimeControlAc::getStimeHour, bo.getStimeHour());
        lqw.eq(bo.getStimeMin() != null, DevConfigTimeControlAc::getStimeMin, bo.getStimeMin());
        lqw.eq(bo.getEtimeHour() != null, DevConfigTimeControlAc::getEtimeHour, bo.getEtimeHour());
        lqw.eq(bo.getEtimeMin() != null, DevConfigTimeControlAc::getEtimeMin, bo.getEtimeMin());
        return lqw;
    }

    /**
     * 新增系统设置-控制方式-交流开关-时段信息
     */
    @Override
    public Boolean insertByBo(DevConfigTimeControlAcBo bo) {
        DevConfigTimeControlAc add = BeanUtil.toBean(bo, DevConfigTimeControlAc.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改系统设置-控制方式-交流开关-时段信息
     */
    @Override
    public Boolean updateByBo(DevConfigTimeControlAcBo bo) {
        DevConfigTimeControlAc update = BeanUtil.toBean(bo, DevConfigTimeControlAc.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigTimeControlAc entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除系统设置-控制方式-交流开关-时段信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
