package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevConfigTimeControlScene;
import com.ruoyi.zm.domain.bo.DevConfigTimeControlSceneBo;
import com.ruoyi.zm.domain.vo.DevConfigTimeControlSceneVo;
import com.ruoyi.zm.mapper.DevConfigTimeControlSceneMapper;
import com.ruoyi.zm.service.IDevConfigTimeControlSceneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 场景模式-时控配置Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@RequiredArgsConstructor
@Service
public class DevConfigTimeControlSceneServiceImpl implements IDevConfigTimeControlSceneService {

    private final DevConfigTimeControlSceneMapper baseMapper;

    /**
     * 查询场景模式-时控配置
     */
    @Override
    public DevConfigTimeControlSceneVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询场景模式-时控配置列表
     */
    @Override
    public TableDataInfo<DevConfigTimeControlSceneVo> queryPageList(DevConfigTimeControlSceneBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigTimeControlScene> lqw = buildQueryWrapper(bo);
        Page<DevConfigTimeControlSceneVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询场景模式-时控配置列表
     */
    @Override
    public List<DevConfigTimeControlSceneVo> queryList(DevConfigTimeControlSceneBo bo) {
        LambdaQueryWrapper<DevConfigTimeControlScene> lqw = buildQueryWrapper(bo);
        lqw.orderByAsc(DevConfigTimeControlScene::getId);
        lqw.groupBy(DevConfigTimeControlScene::getTimeControlId);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigTimeControlScene> buildQueryWrapper(DevConfigTimeControlSceneBo bo) {
        LambdaQueryWrapper<DevConfigTimeControlScene> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigTimeControlScene::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getTimeControlId() != null, DevConfigTimeControlScene::getTimeControlId, bo.getTimeControlId());
        lqw.eq(bo.getTimeFrameId() != null, DevConfigTimeControlScene::getTimeFrameId, bo.getTimeFrameId());
        lqw.eq(bo.getEnabledStatus() != null, DevConfigTimeControlScene::getEnabledStatus, bo.getEnabledStatus());
        lqw.eq(bo.getSceneSelect() != null, DevConfigTimeControlScene::getSceneSelect, bo.getSceneSelect());
        lqw.eq(StringUtils.isNotBlank(bo.getStime()), DevConfigTimeControlScene::getStime, bo.getStime());
        lqw.eq(StringUtils.isNotBlank(bo.getEtime()), DevConfigTimeControlScene::getEtime, bo.getEtime());
        return lqw;
    }

    /**
     * 新增场景模式-时控配置
     */
    @Override
    public Boolean insertByBo(DevConfigTimeControlSceneBo bo) {
        DevConfigTimeControlScene add = BeanUtil.toBean(bo, DevConfigTimeControlScene.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改场景模式-时控配置
     */
    @Override
    public Boolean updateByBo(DevConfigTimeControlSceneBo bo) {
        DevConfigTimeControlScene update = BeanUtil.toBean(bo, DevConfigTimeControlScene.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigTimeControlScene entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除场景模式-时控配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
