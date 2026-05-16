package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevEnergyMeterQuarter;
import com.ruoyi.zm.domain.bo.DevEnergyMeterQuarterBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterQuarterVo;
import com.ruoyi.zm.mapper.DevEnergyMeterQuarterMapper;
import com.ruoyi.zm.service.IDevEnergyMeterQuarterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 十六电季耗电量Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@RequiredArgsConstructor
@Service
public class DevEnergyMeterQuarterServiceImpl implements IDevEnergyMeterQuarterService {

    private final DevEnergyMeterQuarterMapper baseMapper;

    /**
     * 查询十六电季耗电量
     */
    @Override
    public DevEnergyMeterQuarterVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询十六电季耗电量列表
     */
    @Override
    public TableDataInfo<DevEnergyMeterQuarterVo> queryPageList(DevEnergyMeterQuarterBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevEnergyMeterQuarter> lqw = buildQueryWrapper(bo);
        lqw.orderByAsc(DevEnergyMeterQuarter::getEnergyMeterNo);
        lqw.orderByDesc(DevEnergyMeterQuarter::getTimestamp);
        Page<DevEnergyMeterQuarterVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询十六电季耗电量列表
     */
    @Override
    public List<DevEnergyMeterQuarterVo> queryList(DevEnergyMeterQuarterBo bo) {
        LambdaQueryWrapper<DevEnergyMeterQuarter> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevEnergyMeterQuarter> buildQueryWrapper(DevEnergyMeterQuarterBo bo) {
        LambdaQueryWrapper<DevEnergyMeterQuarter> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceNo() != null, DevEnergyMeterQuarter::getDeviceNo, bo.getDeviceNo());
        lqw.eq(bo.getEnergyMeterNo() != null, DevEnergyMeterQuarter::getEnergyMeterNo, bo.getEnergyMeterNo());
        lqw.eq(bo.getPower() != null, DevEnergyMeterQuarter::getPower, bo.getPower());
        lqw.eq(bo.getTimestamp() != null, DevEnergyMeterQuarter::getTimestamp, bo.getTimestamp());
        lqw.orderByDesc(DevEnergyMeterQuarter::getTimestamp);
        return lqw;
    }

    /**
     * 新增十六电季耗电量
     */
    @Override
    public Boolean insertByBo(DevEnergyMeterQuarterBo bo) {
        DevEnergyMeterQuarter add = BeanUtil.toBean(bo, DevEnergyMeterQuarter.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改十六电季耗电量
     */
    @Override
    public Boolean updateByBo(DevEnergyMeterQuarterBo bo) {
        DevEnergyMeterQuarter update = BeanUtil.toBean(bo, DevEnergyMeterQuarter.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevEnergyMeterQuarter entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除十六电季耗电量
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
