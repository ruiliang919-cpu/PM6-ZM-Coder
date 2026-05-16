package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseDistrictAssist;
import com.ruoyi.zm.domain.bo.DevBaseDistrictAssistBo;
import com.ruoyi.zm.domain.vo.DevBaseDistrictAssistVo;
import com.ruoyi.zm.mapper.DevBaseDistrictAssistMapper;
import com.ruoyi.zm.service.IDevBaseDistrictAssistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 照明控制的分区控制的辅助Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-31
 */
@RequiredArgsConstructor
@Service
public class DevBaseDistrictAssistServiceImpl implements IDevBaseDistrictAssistService {

    private final DevBaseDistrictAssistMapper baseMapper;

    /**
     * 查询照明控制的分区控制的辅助
     */
    @Override
    public DevBaseDistrictAssistVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询照明控制的分区控制的辅助列表
     */
    @Override
    public TableDataInfo<DevBaseDistrictAssistVo> queryPageList(DevBaseDistrictAssistBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBaseDistrictAssist> lqw = buildQueryWrapper(bo);
        Page<DevBaseDistrictAssistVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询照明控制的分区控制的辅助列表
     */
    @Override
    public List<DevBaseDistrictAssistVo> queryList(DevBaseDistrictAssistBo bo) {
        LambdaQueryWrapper<DevBaseDistrictAssist> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBaseDistrictAssist> buildQueryWrapper(DevBaseDistrictAssistBo bo) {
        LambdaQueryWrapper<DevBaseDistrictAssist> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getLux() != null, DevBaseDistrictAssist::getLux, bo.getLux());
        lqw.eq(bo.getSwitchStatus() != null, DevBaseDistrictAssist::getSwitchStatus, bo.getSwitchStatus());
        return lqw;
    }

    /**
     * 新增照明控制的分区控制的辅助
     */
    @Override
    public Boolean insertByBo(DevBaseDistrictAssistBo bo) {
        DevBaseDistrictAssist add = BeanUtil.toBean(bo, DevBaseDistrictAssist.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改照明控制的分区控制的辅助
     */
    @Override
    public Boolean updateByBo(DevBaseDistrictAssistBo bo) {
        DevBaseDistrictAssist update = BeanUtil.toBean(bo, DevBaseDistrictAssist.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevBaseDistrictAssist entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除照明控制的分区控制的辅助
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
