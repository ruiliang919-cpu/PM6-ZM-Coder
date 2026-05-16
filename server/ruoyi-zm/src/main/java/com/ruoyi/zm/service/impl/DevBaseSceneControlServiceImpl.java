package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseSceneControl;
import com.ruoyi.zm.domain.bo.DevBaseSceneControlBo;
import com.ruoyi.zm.domain.vo.DevBaseSceneControlVo;
import com.ruoyi.zm.mapper.DevBaseSceneControlMapper;
import com.ruoyi.zm.service.IDevBaseSceneControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 场景时控启用Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-09
 */
@RequiredArgsConstructor
@Service
public class DevBaseSceneControlServiceImpl implements IDevBaseSceneControlService {

    private final DevBaseSceneControlMapper baseMapper;

    /**
     * 查询场景时控启用
     */
    @Override
    public DevBaseSceneControlVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询场景时控启用列表
     */
    @Override
    public TableDataInfo<DevBaseSceneControlVo> queryPageList(DevBaseSceneControlBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBaseSceneControl> lqw = buildQueryWrapper(bo);
        Page<DevBaseSceneControlVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询场景时控启用列表
     */
    @Override
    public List<DevBaseSceneControlVo> queryList(DevBaseSceneControlBo bo) {
        LambdaQueryWrapper<DevBaseSceneControl> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBaseSceneControl> buildQueryWrapper(DevBaseSceneControlBo bo) {

        LambdaQueryWrapper<DevBaseSceneControl> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevBaseSceneControl::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getControlId() != null, DevBaseSceneControl::getControlId, bo.getControlId());
        lqw.eq(bo.getEnabled() != null, DevBaseSceneControl::getEnabled, bo.getEnabled());
        return lqw;
    }

    /**
     * 新增场景时控启用
     */
    @Override
    public Boolean insertByBo(DevBaseSceneControlBo bo) {
        DevBaseSceneControl add = BeanUtil.toBean(bo, DevBaseSceneControl.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改场景时控启用
     */
    @Override
    public Boolean updateByBo(DevBaseSceneControlBo bo) {
        DevBaseSceneControl update = BeanUtil.toBean(bo, DevBaseSceneControl.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevBaseSceneControl entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除场景时控启用
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
