package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevDeviceRemoteRead;
import com.ruoyi.zm.domain.bo.DevDeviceRemoteReadBo;
import com.ruoyi.zm.domain.vo.DevDeviceRemoteReadVo;
import com.ruoyi.zm.mapper.DevDeviceRemoteReadMapper;
import com.ruoyi.zm.service.IDevDeviceRemoteReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@RequiredArgsConstructor
@Service
public class DevDeviceRemoteReadServiceImpl implements IDevDeviceRemoteReadService {

    private final DevDeviceRemoteReadMapper baseMapper;

    /**
     * 查询交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应
     */
    @Override
    public DevDeviceRemoteReadVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应列表
     */
    @Override
    public TableDataInfo<DevDeviceRemoteReadVo> queryPageList(DevDeviceRemoteReadBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevDeviceRemoteRead> lqw = buildQueryWrapper(bo);
        Page<DevDeviceRemoteReadVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应列表
     */
    @Override
    public List<DevDeviceRemoteReadVo> queryList(DevDeviceRemoteReadBo bo) {
        LambdaQueryWrapper<DevDeviceRemoteRead> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevDeviceRemoteRead> buildQueryWrapper(DevDeviceRemoteReadBo bo) {
        LambdaQueryWrapper<DevDeviceRemoteRead> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevDeviceRemoteRead::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getAcSwitchNum() != null, DevDeviceRemoteRead::getAcSwitchNum, bo.getAcSwitchNum());
        lqw.eq(bo.getLoopLux() != null, DevDeviceRemoteRead::getLoopLux, bo.getLoopLux());
        lqw.eq(bo.getGroupLux() != null, DevDeviceRemoteRead::getGroupLux, bo.getGroupLux());
        lqw.eq(bo.getInfraredSensingMode() != null, DevDeviceRemoteRead::getInfraredSensingMode, bo.getInfraredSensingMode());
        lqw.eq(bo.getIlluminanceSensingMode() != null, DevDeviceRemoteRead::getIlluminanceSensingMode, bo.getIlluminanceSensingMode());
        lqw.eq(bo.getManualMode() != null, DevDeviceRemoteRead::getManualMode, bo.getManualMode());
        return lqw;
    }

    /**
     * 新增交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应
     */
    @Override
    public Boolean insertByBo(DevDeviceRemoteReadBo bo) {
        DevDeviceRemoteRead add = BeanUtil.toBean(bo, DevDeviceRemoteRead.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应
     */
    @Override
    public Boolean updateByBo(DevDeviceRemoteReadBo bo) {
        DevDeviceRemoteRead update = BeanUtil.toBean(bo, DevDeviceRemoteRead.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevDeviceRemoteRead entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
