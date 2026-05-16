package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigScene;
import com.ruoyi.zm.domain.bo.DevConfigSceneBo;
import com.ruoyi.zm.domain.vo.DevConfigSceneVo;
import com.ruoyi.zm.mapper.DevConfigSceneMapper;
import com.ruoyi.zm.service.IDevConfigSceneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 场景设置Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-03
 */
@RequiredArgsConstructor
@Service
public class DevConfigSceneServiceImpl implements IDevConfigSceneService {

    private final DevConfigSceneMapper baseMapper;

    /**
     * 查询场景设置
     */
    @Override
    public DevConfigSceneVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询场景设置列表
     */
    @Override
    public TableDataInfo<DevConfigSceneVo> queryPageList(DevConfigSceneBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigScene> lqw = buildQueryWrapper(bo);
        Page<DevConfigSceneVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询场景设置列表
     */
    @Override
    public List<DevConfigSceneVo> queryList(DevConfigSceneBo bo) {
        LambdaQueryWrapper<DevConfigScene> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigScene> buildQueryWrapper(DevConfigSceneBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevConfigScene> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigScene::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getSceneId() != null, DevConfigScene::getSceneId, bo.getSceneId());
        lqw.eq(bo.getGroupId() != null, DevConfigScene::getGroupId, bo.getGroupId());
        lqw.eq(bo.getLux() != null, DevConfigScene::getLux, bo.getLux());
        lqw.eq(bo.getBtnStatus() != null, DevConfigScene::getBtnStatus, bo.getBtnStatus());
        lqw.eq(bo.getSelectStatus() != null, DevConfigScene::getSelectStatus, bo.getSelectStatus());
        return lqw;
    }

    /**
     * 新增场景设置
     */
    @Override
    public Boolean insertByBo(DevConfigSceneBo bo) {
        DevConfigScene add = BeanUtil.toBean(bo, DevConfigScene.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改场景设置
     */
    @Override
    public Boolean updateByBo(DevConfigSceneBo bo) {
        DevConfigScene update = BeanUtil.toBean(bo, DevConfigScene.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigScene entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除场景设置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
