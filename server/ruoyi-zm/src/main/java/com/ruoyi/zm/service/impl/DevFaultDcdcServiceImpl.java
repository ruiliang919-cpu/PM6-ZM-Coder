package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevFaultDcdc;
import com.ruoyi.zm.domain.bo.DevFaultDcdcBo;
import com.ruoyi.zm.domain.vo.DevFaultDcdcVo;
import com.ruoyi.zm.mapper.DevFaultDcdcMapper;
import com.ruoyi.zm.service.IDevFaultDcdcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * DC/DC故障信息Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-24
 */
@RequiredArgsConstructor
@Service
public class DevFaultDcdcServiceImpl implements IDevFaultDcdcService {

    private final DevFaultDcdcMapper baseMapper;

    /**
     * 查询DC/DC故障信息
     */
    @Override
    public DevFaultDcdcVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询DC/DC故障信息列表
     */
    @Override
    public TableDataInfo<DevFaultDcdcVo> queryPageList(DevFaultDcdcBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevFaultDcdc> lqw = buildQueryWrapper(bo);
        Page<DevFaultDcdcVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询DC/DC故障信息列表
     */
    @Override
    public List<DevFaultDcdcVo> queryList(DevFaultDcdcBo bo) {
        LambdaQueryWrapper<DevFaultDcdc> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevFaultDcdc> buildQueryWrapper(DevFaultDcdcBo bo) {
        LambdaQueryWrapper<DevFaultDcdc> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevFaultDcdc::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getNo() != null, DevFaultDcdc::getNo, bo.getNo());
        lqw.eq(bo.getModuleType() != null, DevFaultDcdc::getModuleType, bo.getModuleType());
        lqw.eq(bo.getOnlineType() != null, DevFaultDcdc::getOnlineType, bo.getOnlineType());
        lqw.eq(bo.getVoltage() != null, DevFaultDcdc::getVoltage, bo.getVoltage());
        lqw.eq(bo.getElectricity() != null, DevFaultDcdc::getElectricity, bo.getElectricity());
        return lqw;
    }

    /**
     * 新增DC/DC故障信息
     */
    @Override
    public Boolean insertByBo(DevFaultDcdcBo bo) {
        DevFaultDcdc add = BeanUtil.toBean(bo, DevFaultDcdc.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改DC/DC故障信息
     */
    @Override
    public Boolean updateByBo(DevFaultDcdcBo bo) {
        DevFaultDcdc update = BeanUtil.toBean(bo, DevFaultDcdc.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevFaultDcdc entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除DC/DC故障信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
