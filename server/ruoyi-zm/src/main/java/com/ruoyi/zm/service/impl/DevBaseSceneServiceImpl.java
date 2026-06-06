package com.ruoyi.zm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevBaseScene;
import com.ruoyi.zm.domain.bo.DevBaseSceneBo;
import com.ruoyi.zm.domain.vo.DevBaseSceneVo;
import com.ruoyi.zm.mapper.DevBaseSceneMapper;
import com.ruoyi.zm.service.IDevBaseSceneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 场景Service业务层处理
 *
 * @author mophi
 * @date 2024-07-11
 */
@RequiredArgsConstructor
@Service
public class DevBaseSceneServiceImpl implements IDevBaseSceneService {

    private final DevBaseSceneMapper baseMapper;

    /**
     * 查询场景
     */
    @Override
    public DevBaseSceneVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询场景列表
     */
    @Override
    public TableDataInfo<DevBaseSceneVo> queryPageList(DevBaseSceneBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBaseScene> lqw = buildQueryWrapper(bo);
        Page<DevBaseSceneVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询场景列表
     */
    @Override
    public List<DevBaseSceneVo> queryList(DevBaseSceneBo bo) {
        LambdaQueryWrapper<DevBaseScene> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBaseScene> buildQueryWrapper(DevBaseSceneBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevBaseScene> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevBaseScene::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增场景
     */
    @Override
    public Boolean insertByBo(DevBaseSceneBo bo) {
        DevBaseScene add = new DevBaseScene();
        add.setName(bo.getName());
        // 手动生成ID：查询当前最大ID并+1，空表从1开始
        Long maxId = baseMapper.selectObjs(
            new LambdaQueryWrapper<DevBaseScene>().select(DevBaseScene::getId)
                .orderByDesc(DevBaseScene::getId).last("LIMIT 1")
        ).stream().map(id -> ((Number) id).longValue()).findFirst().orElse(0L);
        Long newId = maxId + 1;
        add.setId(newId);
        add.setSceneId(Math.toIntExact(newId));
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            bo.setSceneId(add.getSceneId());
        }
        return flag;
    }

    /**
     * 修改场景
     */
    @Override
    public Boolean updateByBo(DevBaseSceneBo bo) {
        Integer id = null;
        DevBaseScene update = new DevBaseScene();
        update.setName(bo.getName());
        if (bo.getId() != null) {
            id = Math.toIntExact(bo.getId());
            update.setId(Long.valueOf(id));
            update.setSceneId(id);
        } else if (bo.getSceneId() != null) {
            id = bo.getSceneId();
            update.setId(Long.valueOf(id));
            update.setSceneId(id);
        }
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除场景
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
