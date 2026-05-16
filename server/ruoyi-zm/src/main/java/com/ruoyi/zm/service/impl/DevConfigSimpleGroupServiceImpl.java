package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevConfigSimpleGroup;
import com.ruoyi.zm.domain.bo.DevConfigSimpleGroupBo;
import com.ruoyi.zm.domain.vo.DevConfigSimpleGroupVo;
import com.ruoyi.zm.mapper.DevConfigSimpleGroupMapper;
import com.ruoyi.zm.service.IDevConfigSimpleGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 普通时控模式与分组配置Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-03
 */
@RequiredArgsConstructor
@Service
public class DevConfigSimpleGroupServiceImpl implements IDevConfigSimpleGroupService {

    private final DevConfigSimpleGroupMapper baseMapper;

    /**
     * 查询普通时控模式与分组配置
     */
    @Override
    public DevConfigSimpleGroupVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询普通时控模式与分组配置列表
     */
    @Override
    public TableDataInfo<DevConfigSimpleGroupVo> queryPageList(DevConfigSimpleGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigSimpleGroup> lqw = buildQueryWrapper(bo);
        Page<DevConfigSimpleGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询普通时控模式与分组配置列表
     */
    @Override
    public List<DevConfigSimpleGroupVo> queryList(DevConfigSimpleGroupBo bo) {
        LambdaQueryWrapper<DevConfigSimpleGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigSimpleGroup> buildQueryWrapper(DevConfigSimpleGroupBo bo) {
        LambdaQueryWrapper<DevConfigSimpleGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigSimpleGroup::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getSimpleId() != null, DevConfigSimpleGroup::getSimpleId, bo.getSimpleId());
        lqw.eq(bo.getGroupId() != null, DevConfigSimpleGroup::getGroupId, bo.getGroupId());
        lqw.like(StringUtils.isNotBlank(bo.getGroupName()), DevConfigSimpleGroup::getGroupName, bo.getGroupName());
        lqw.eq(bo.getSelectStatus() != null, DevConfigSimpleGroup::getSelectStatus, bo.getSelectStatus());
        return lqw;
    }

    /**
     * 新增普通时控模式与分组配置
     */
    @Override
    public Boolean insertByBo(DevConfigSimpleGroupBo bo) {
        DevConfigSimpleGroup add = BeanUtil.toBean(bo, DevConfigSimpleGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改普通时控模式与分组配置
     */
    @Override
    public Boolean updateByBo(DevConfigSimpleGroupBo bo) {
        DevConfigSimpleGroup update = BeanUtil.toBean(bo, DevConfigSimpleGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigSimpleGroup entity) {
        // 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除普通时控模式与分组配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
