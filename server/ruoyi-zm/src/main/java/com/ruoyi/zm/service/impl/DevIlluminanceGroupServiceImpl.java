package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevIlluminanceGroup;
import com.ruoyi.zm.domain.bo.DevIlluminanceGroupBo;
import com.ruoyi.zm.domain.vo.DevIlluminanceGroupVo;
import com.ruoyi.zm.mapper.DevIlluminanceGroupMapper;
import com.ruoyi.zm.service.IDevIlluminanceGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 照度模式传感器与分组选择Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-04
 */
@RequiredArgsConstructor
@Service
public class DevIlluminanceGroupServiceImpl implements IDevIlluminanceGroupService {

    private final DevIlluminanceGroupMapper baseMapper;

    /**
     * 查询照度模式传感器与分组选择
     */
    @Override
    public DevIlluminanceGroupVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询照度模式传感器与分组选择列表
     */
    @Override
    public TableDataInfo<DevIlluminanceGroupVo> queryPageList(DevIlluminanceGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevIlluminanceGroup> lqw = buildQueryWrapper(bo);
        Page<DevIlluminanceGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询照度模式传感器与分组选择列表
     */
    @Override
    public List<DevIlluminanceGroupVo> queryList(DevIlluminanceGroupBo bo) {
        LambdaQueryWrapper<DevIlluminanceGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevIlluminanceGroup> buildQueryWrapper(DevIlluminanceGroupBo bo) {
        LambdaQueryWrapper<DevIlluminanceGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevIlluminanceGroup::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getSensorId() != null, DevIlluminanceGroup::getSensorId, bo.getSensorId());
        lqw.eq(bo.getGroupId() != null, DevIlluminanceGroup::getGroupId, bo.getGroupId());
        lqw.like(StringUtils.isNotBlank(bo.getGroupName()), DevIlluminanceGroup::getGroupName, bo.getGroupName());
        lqw.eq(bo.getSelectStatus() != null, DevIlluminanceGroup::getSelectStatus, bo.getSelectStatus());
        return lqw;
    }

    /**
     * 新增照度模式传感器与分组选择
     */
    @Override
    public Boolean insertByBo(DevIlluminanceGroupBo bo) {
        DevIlluminanceGroup add = BeanUtil.toBean(bo, DevIlluminanceGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改照度模式传感器与分组选择
     */
    @Override
    public Boolean updateByBo(DevIlluminanceGroupBo bo) {
        DevIlluminanceGroup update = BeanUtil.toBean(bo, DevIlluminanceGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevIlluminanceGroup entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除照度模式传感器与分组选择
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
