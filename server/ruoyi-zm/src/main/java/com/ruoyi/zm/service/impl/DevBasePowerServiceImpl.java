package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBasePower;
import com.ruoyi.zm.domain.bo.DevBasePowerBo;
import com.ruoyi.zm.domain.vo.DevBasePowerVo;
import com.ruoyi.zm.mapper.DevBasePowerMapper;
import com.ruoyi.zm.service.IDevBasePowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 机柜耗电量 功率 Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@RequiredArgsConstructor
@Service
public class DevBasePowerServiceImpl implements IDevBasePowerService {

    private final DevBasePowerMapper baseMapper;

    /**
     * 查询机柜耗电量 功率
     */
    @Override
    public DevBasePowerVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询机柜耗电量 功率 列表
     */
    @Override
    public TableDataInfo<DevBasePowerVo> queryPageList(DevBasePowerBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBasePower> lqw = buildQueryWrapper(bo);
        Page<DevBasePowerVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询机柜耗电量 功率 列表
     */
    @Override
    public List<DevBasePowerVo> queryList(DevBasePowerBo bo) {
        LambdaQueryWrapper<DevBasePower> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBasePower> buildQueryWrapper(DevBasePowerBo bo) {
        LambdaQueryWrapper<DevBasePower> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevBasePower::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getType() != null, DevBasePower::getType, bo.getType());
        lqw.eq(bo.getValue() != null, DevBasePower::getValue, bo.getValue());
        lqw.eq(bo.getTimestamp() != null, DevBasePower::getTimestamp, bo.getTimestamp());
        lqw.orderByDesc(DevBasePower::getTimestamp);
        return lqw;
    }

    /**
     * 新增机柜耗电量 功率
     */
    @Override
    public Boolean insertByBo(DevBasePowerBo bo) {
        DevBasePower add = BeanUtil.toBean(bo, DevBasePower.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改机柜耗电量 功率
     */
    @Override
    public Boolean updateByBo(DevBasePowerBo bo) {
        DevBasePower update = BeanUtil.toBean(bo, DevBasePower.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevBasePower entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除机柜耗电量 功率
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
