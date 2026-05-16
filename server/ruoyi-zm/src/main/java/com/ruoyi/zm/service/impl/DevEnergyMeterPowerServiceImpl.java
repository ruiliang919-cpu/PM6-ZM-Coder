package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevEnergyMeterPower;
import com.ruoyi.zm.domain.bo.DevEnergyMeterPowerBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterPowerVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevEnergyMeterPowerMapper;
import com.ruoyi.zm.service.IDevEnergyMeterPowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 十六电总功率Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@RequiredArgsConstructor
@Service
public class DevEnergyMeterPowerServiceImpl implements IDevEnergyMeterPowerService {

    private final DevEnergyMeterPowerMapper baseMapper;
    private final DevBaseDeviceMapper deviceMapper;

    /**
     * 查询十六电总功率
     */
    @Override
    public DevEnergyMeterPowerVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询十六电总功率列表
     */
    @Override
    public TableDataInfo<DevEnergyMeterPowerVo> queryPageList(DevEnergyMeterPowerBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevEnergyMeterPower> lqw = buildQueryWrapper(bo);
        Page<DevEnergyMeterPowerVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询十六电总功率列表
     */
    @Override
    public List<DevEnergyMeterPowerVo> queryList(DevEnergyMeterPowerBo bo) {
        LambdaQueryWrapper<DevEnergyMeterPower> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevEnergyMeterPower> buildQueryWrapper(DevEnergyMeterPowerBo bo) {
        LambdaQueryWrapper<DevEnergyMeterPower> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceNo() != null, DevEnergyMeterPower::getDeviceNo, bo.getDeviceNo());
        lqw.eq(bo.getEnergyMeterNo() != null, DevEnergyMeterPower::getEnergyMeterNo, bo.getEnergyMeterNo());
        lqw.eq(bo.getPower() != null, DevEnergyMeterPower::getPower, bo.getPower());
        return lqw;
    }


    /**
     * 新增十六电总功率
     */
    @Override
    public Boolean insertByBo(DevEnergyMeterPowerBo bo) {
        DevEnergyMeterPower add = BeanUtil.toBean(bo, DevEnergyMeterPower.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改十六电总功率
     */
    @Override
    public Boolean updateByBo(DevEnergyMeterPowerBo bo) {
        DevEnergyMeterPower update = BeanUtil.toBean(bo, DevEnergyMeterPower.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevEnergyMeterPower entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除十六电总功率
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
