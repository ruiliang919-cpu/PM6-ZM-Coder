package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevBaseBranch;
import com.ruoyi.zm.domain.bo.DevBaseBranchBo;
import com.ruoyi.zm.domain.vo.DevBaseBranchVo;
import com.ruoyi.zm.mapper.DevBaseBranchMapper;
import com.ruoyi.zm.service.IDevBaseBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 馈线支路名称Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@RequiredArgsConstructor
@Service
public class DevBaseBranchServiceImpl implements IDevBaseBranchService {

    private final DevBaseBranchMapper baseMapper;

    /**
     * 查询馈线支路名称
     */
    @Override
    public DevBaseBranchVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询馈线支路名称列表
     */
    @Override
    public TableDataInfo<DevBaseBranchVo> queryPageList(DevBaseBranchBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBaseBranch> lqw = buildQueryWrapper(bo);
        Page<DevBaseBranchVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询馈线支路名称列表
     */
    @Override
    public List<DevBaseBranchVo> queryList(DevBaseBranchBo bo) {
        LambdaQueryWrapper<DevBaseBranch> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBaseBranch> buildQueryWrapper(DevBaseBranchBo bo) {
        LambdaQueryWrapper<DevBaseBranch> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevBaseBranch::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getBranchNo() != null, DevBaseBranch::getBranchNo, bo.getBranchNo());
        lqw.like(StringUtils.isNotBlank(bo.getBranchName()), DevBaseBranch::getBranchName, bo.getBranchName());
        return lqw;
    }

    /**
     * 新增馈线支路名称
     */
    @Override
    public Boolean insertByBo(DevBaseBranchBo bo) {
        DevBaseBranch add = BeanUtil.toBean(bo, DevBaseBranch.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改馈线支路名称
     */
    @Override
    public Boolean updateByBo(DevBaseBranchBo bo) {
        DevBaseBranch update = BeanUtil.toBean(bo, DevBaseBranch.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevBaseBranch entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除馈线支路名称
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
