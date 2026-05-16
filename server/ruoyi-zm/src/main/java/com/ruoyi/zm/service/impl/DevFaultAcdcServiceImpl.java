package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevFaultAcdc;
import com.ruoyi.zm.domain.bo.DevFaultAcdcBo;
import com.ruoyi.zm.domain.vo.DevFaultAcdcVo;
import com.ruoyi.zm.mapper.DevFaultAcdcMapper;
import com.ruoyi.zm.service.IDevFaultAcdcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * AC/DC故障信息Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-24
 */
@RequiredArgsConstructor
@Service
public class DevFaultAcdcServiceImpl implements IDevFaultAcdcService {

    private final DevFaultAcdcMapper baseMapper;

    /**
     * 查询AC/DC故障信息
     */
    @Override
    public DevFaultAcdcVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询AC/DC故障信息列表
     */
    @Override
    public TableDataInfo<DevFaultAcdcVo> queryPageList(DevFaultAcdcBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevFaultAcdc> lqw = buildQueryWrapper(bo);
        Page<DevFaultAcdcVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询AC/DC故障信息列表
     */
    @Override
    public List<DevFaultAcdcVo> queryList(DevFaultAcdcBo bo) {
        LambdaQueryWrapper<DevFaultAcdc> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevFaultAcdc> buildQueryWrapper(DevFaultAcdcBo bo) {
        LambdaQueryWrapper<DevFaultAcdc> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevFaultAcdc::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getNo() != null, DevFaultAcdc::getNo, bo.getNo());
        lqw.eq(bo.getModuleType() != null, DevFaultAcdc::getModuleType, bo.getModuleType());
        lqw.eq(bo.getOnlineType() != null, DevFaultAcdc::getOnlineType, bo.getOnlineType());
        lqw.eq(bo.getVoltage() != null, DevFaultAcdc::getVoltage, bo.getVoltage());
        lqw.eq(bo.getElectricity() != null, DevFaultAcdc::getElectricity, bo.getElectricity());
        lqw.eq(bo.getSwitchStatus() != null, DevFaultAcdc::getSwitchStatus, bo.getSwitchStatus());
        return lqw;
    }

    /**
     * 新增AC/DC故障信息
     */
    @Override
    public Boolean insertByBo(DevFaultAcdcBo bo) {
        DevFaultAcdc add = BeanUtil.toBean(bo, DevFaultAcdc.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改AC/DC故障信息
     */
    @Override
    public Boolean updateByBo(DevFaultAcdcBo bo) {
        DevFaultAcdc update = BeanUtil.toBean(bo, DevFaultAcdc.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevFaultAcdc entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除AC/DC故障信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
