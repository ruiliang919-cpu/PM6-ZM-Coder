package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevStatusDccLoop;
import com.ruoyi.zm.domain.bo.DevStatusDccLoopBo;
import com.ruoyi.zm.domain.vo.DevStatusDccLoopVo;
import com.ruoyi.zm.mapper.DevStatusDccLoopMapper;
import com.ruoyi.zm.service.IDevStatusDccLoopService;
import com.ruoyi.zm.service.telemetering.LoopService;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 直流回路Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@RequiredArgsConstructor
@Service
public class DevStatusDccLoopServiceImpl implements IDevStatusDccLoopService {

    private final DevStatusDccLoopMapper baseMapper;
    private final LoopService loopService;

    /**
     * 查询直流回路
     */
    @Override
    public DevStatusDccLoopVo queryById(Long id) {
        // return baseMapper.selectVoById(id);
        return null;
    }

    /**
     * 查询直流回路列表
     */
    @Override
    public TableDataInfo<DevStatusDccLoopVo> queryPageList(DevStatusDccLoopBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevStatusDccLoop> lqw = buildQueryWrapper(bo);
        Page<DevStatusDccLoopVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询直流回路列表
     */
    @Override
    public List<DevStatusDccLoopVo> queryList(DevStatusDccLoopBo bo) {
        LambdaQueryWrapper<DevStatusDccLoop> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevStatusDccLoop> buildQueryWrapper(DevStatusDccLoopBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevStatusDccLoop> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevStatusDccLoop::getDeviceId, bo.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(bo.getNo()), DevStatusDccLoop::getNo, bo.getNo());
        lqw.eq(bo.getGroupId() != null, DevStatusDccLoop::getGroupId, bo.getGroupId());
        lqw.eq(bo.getBrightnessSetting() != null, DevStatusDccLoop::getBrightnessSetting, bo.getBrightnessSetting());
        lqw.eq(bo.getBrightnessFeedback() != null, DevStatusDccLoop::getBrightnessFeedback, bo.getBrightnessFeedback());
        lqw.eq(bo.getOutputVoltage() != null, DevStatusDccLoop::getOutputVoltage, bo.getOutputVoltage());
        lqw.eq(bo.getOutputCurrent() != null, DevStatusDccLoop::getOutputCurrent, bo.getOutputCurrent());
        lqw.eq(bo.getInternalTemperature() != null, DevStatusDccLoop::getInternalTemperature, bo.getInternalTemperature());
        lqw.eq(bo.getSwitchSetting() != null, DevStatusDccLoop::getSwitchSetting, bo.getSwitchSetting());
        lqw.eq(bo.getSwitchFeedback() != null, DevStatusDccLoop::getSwitchFeedback, bo.getSwitchFeedback());
        lqw.eq(bo.getRealTime() != null, DevStatusDccLoop::getRealTime, bo.getRealTime());
        return lqw;
    }

    /**
     * 新增直流回路
     */
    @Override
    public Boolean insertByBo(DevStatusDccLoopBo bo) {
        DevStatusDccLoop add = BeanUtil.toBean(bo, DevStatusDccLoop.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改直流回路
     */
    @Override
    public Boolean updateByBo(DevStatusDccLoopBo bo) {
        DevStatusDccLoop update = BeanUtil.toBean(bo, DevStatusDccLoop.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevStatusDccLoop entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除直流回路
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean updateLoopByGroup(int slaveId, int groupId) throws ModbusTransportException {
        List<Integer> loopsByPacketNo = loopService.getLoopsByPacketNo(slaveId, groupId);
        LambdaQueryWrapper<DevStatusDccLoop> lqw = new LambdaQueryWrapper<>();
        for (Integer no : loopsByPacketNo) {
            lqw.eq(DevStatusDccLoop::getNo, no);
            DevStatusDccLoop devStatusDccLoop = baseMapper.selectOne(lqw);
            devStatusDccLoop.setGroupId((long) groupId);
            baseMapper.update(devStatusDccLoop, null);
        }
        return true;
    }
}
