package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevEnergyMeterTotal;
import com.ruoyi.zm.domain.bo.DevEnergyMeterTotalBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterTotalVo;
import com.ruoyi.zm.mapper.DevEnergyMeterTotalMapper;
import com.ruoyi.zm.service.IDevEnergyMeterTotalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 十六电总耗电量Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@RequiredArgsConstructor
@Service
public class DevEnergyMeterTotalServiceImpl implements IDevEnergyMeterTotalService {

    private final DevEnergyMeterTotalMapper baseMapper;

    /**
     * 查询十六电总耗电量
     */
    @Override
    public DevEnergyMeterTotalVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询十六电总耗电量列表
     */
    @Override
    public TableDataInfo<DevEnergyMeterTotalVo> queryPageList(DevEnergyMeterTotalBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevEnergyMeterTotal> lqw = buildQueryWrapper(bo);
        Page<DevEnergyMeterTotalVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询十六电总耗电量列表
     */
    @Override
    public List<DevEnergyMeterTotalVo> queryList(DevEnergyMeterTotalBo bo) {
        LambdaQueryWrapper<DevEnergyMeterTotal> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevEnergyMeterTotal> buildQueryWrapper(DevEnergyMeterTotalBo bo) {

        LambdaQueryWrapper<DevEnergyMeterTotal> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceNo() != null, DevEnergyMeterTotal::getDeviceNo, bo.getDeviceNo());
        lqw.eq(bo.getEnergyMeterNo() != null, DevEnergyMeterTotal::getEnergyMeterNo, bo.getEnergyMeterNo());
        lqw.eq(bo.getPower() != null, DevEnergyMeterTotal::getPower, bo.getPower());
        lqw.eq(bo.getTimestamp() != null, DevEnergyMeterTotal::getTimestamp, bo.getTimestamp());
        lqw.orderByDesc(DevEnergyMeterTotal::getTimestamp);
        lqw.orderByAsc(DevEnergyMeterTotal::getEnergyMeterNo);
        return lqw;
    }

    /**
     * 新增十六电总耗电量
     */
    @Override
    public Boolean insertByBo(DevEnergyMeterTotalBo bo) {
        DevEnergyMeterTotal add = BeanUtil.toBean(bo, DevEnergyMeterTotal.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改十六电总耗电量
     */
    @Override
    public Boolean updateByBo(DevEnergyMeterTotalBo bo) {
        DevEnergyMeterTotal update = BeanUtil.toBean(bo, DevEnergyMeterTotal.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevEnergyMeterTotal entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除十六电总耗电量
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
