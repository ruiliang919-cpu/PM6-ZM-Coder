package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseSimpleControl;
import com.ruoyi.zm.domain.bo.DevBaseSimpleControlBo;
import com.ruoyi.zm.domain.vo.DevBaseSimpleControlVo;
import com.ruoyi.zm.mapper.DevBaseSimpleControlMapper;
import com.ruoyi.zm.service.IDevBaseSimpleControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 普通时控启用Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-09
 */
@RequiredArgsConstructor
@Service
public class DevBaseSimpleControlServiceImpl implements IDevBaseSimpleControlService {

    private final DevBaseSimpleControlMapper baseMapper;

    /**
     * 查询普通时控启用
     */
    @Override
    public DevBaseSimpleControlVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询普通时控启用列表
     */
    @Override
    public TableDataInfo<DevBaseSimpleControlVo> queryPageList(DevBaseSimpleControlBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBaseSimpleControl> lqw = buildQueryWrapper(bo);
        Page<DevBaseSimpleControlVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询普通时控启用列表
     */
    @Override
    public List<DevBaseSimpleControlVo> queryList(DevBaseSimpleControlBo bo) {
        LambdaQueryWrapper<DevBaseSimpleControl> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBaseSimpleControl> buildQueryWrapper(DevBaseSimpleControlBo bo) {

        LambdaQueryWrapper<DevBaseSimpleControl> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevBaseSimpleControl::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getControlId() != null, DevBaseSimpleControl::getControlId, bo.getControlId());
        lqw.eq(bo.getEnabled() != null, DevBaseSimpleControl::getEnabled, bo.getEnabled());
        return lqw;
    }

    /**
     * 新增普通时控启用
     */
    @Override
    public Boolean insertByBo(DevBaseSimpleControlBo bo) {
        DevBaseSimpleControl add = BeanUtil.toBean(bo, DevBaseSimpleControl.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改普通时控启用
     */
    @Override
    public Boolean updateByBo(DevBaseSimpleControlBo bo) {
        DevBaseSimpleControl update = BeanUtil.toBean(bo, DevBaseSimpleControl.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevBaseSimpleControl entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除普通时控启用
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
