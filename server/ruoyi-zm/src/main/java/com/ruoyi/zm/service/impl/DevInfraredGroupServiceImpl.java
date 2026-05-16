package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevInfraredGroup;
import com.ruoyi.zm.domain.bo.DevInfraredGroupBo;
import com.ruoyi.zm.domain.vo.DevInfraredGroupVo;
import com.ruoyi.zm.mapper.DevInfraredGroupMapper;
import com.ruoyi.zm.service.IDevInfraredGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 红外模式传感器与分组选择Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-04
 */
@RequiredArgsConstructor
@Service
public class DevInfraredGroupServiceImpl implements IDevInfraredGroupService {

    private final DevInfraredGroupMapper baseMapper;

    /**
     * 查询红外模式传感器与分组选择
     */
    @Override
    public DevInfraredGroupVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询红外模式传感器与分组选择列表
     */
    @Override
    public TableDataInfo<DevInfraredGroupVo> queryPageList(DevInfraredGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevInfraredGroup> lqw = buildQueryWrapper(bo);
        Page<DevInfraredGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询红外模式传感器与分组选择列表
     */
    @Override
    public List<DevInfraredGroupVo> queryList(DevInfraredGroupBo bo) {
        LambdaQueryWrapper<DevInfraredGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevInfraredGroup> buildQueryWrapper(DevInfraredGroupBo bo) {
        LambdaQueryWrapper<DevInfraredGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevInfraredGroup::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getSensorId() != null, DevInfraredGroup::getSensorId, bo.getSensorId());
        lqw.eq(bo.getGroupId() != null, DevInfraredGroup::getGroupId, bo.getGroupId());
        lqw.like(StringUtils.isNotBlank(bo.getGroupName()), DevInfraredGroup::getGroupName, bo.getGroupName());
        lqw.eq(bo.getSelectStatus() != null, DevInfraredGroup::getSelectStatus, bo.getSelectStatus());
        return lqw;
    }

    /**
     * 新增红外模式传感器与分组选择
     */
    @Override
    public Boolean insertByBo(DevInfraredGroupBo bo) {
        DevInfraredGroup add = BeanUtil.toBean(bo, DevInfraredGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改红外模式传感器与分组选择
     */
    @Override
    public Boolean updateByBo(DevInfraredGroupBo bo) {
        DevInfraredGroup update = BeanUtil.toBean(bo, DevInfraredGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevInfraredGroup entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除红外模式传感器与分组选择
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
