package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevStatusBranch;
import com.ruoyi.zm.domain.bo.DevStatusBranchBo;
import com.ruoyi.zm.domain.vo.DevStatusBranchVo;
import com.ruoyi.zm.mapper.DevStatusBranchMapper;
import com.ruoyi.zm.service.IDevStatusBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 支路信息记录Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@RequiredArgsConstructor
@Service
public class DevStatusBranchServiceImpl implements IDevStatusBranchService {

    private final DevStatusBranchMapper baseMapper;

    /**
     * 查询支路信息记录
     */
    @Override
    public DevStatusBranchVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询支路信息记录列表
     */
    @Override
    public TableDataInfo<DevStatusBranchVo> queryPageList(DevStatusBranchBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevStatusBranch> lqw = buildQueryWrapper(bo);
        Page<DevStatusBranchVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询支路信息记录列表
     */
    @Override
    public List<DevStatusBranchVo> queryList(DevStatusBranchBo bo) {
        LambdaQueryWrapper<DevStatusBranch> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevStatusBranch> buildQueryWrapper(DevStatusBranchBo bo) {

        LambdaQueryWrapper<DevStatusBranch> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevStatusBranch::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getBranchNo() != null, DevStatusBranch::getBranchNo, bo.getBranchNo());
        lqw.eq(bo.getSwitchStatus() != null, DevStatusBranch::getSwitchStatus, bo.getSwitchStatus());
        lqw.eq(bo.getSwitchFault() != null, DevStatusBranch::getSwitchFault, bo.getSwitchFault());
        return lqw;
    }

    /**
     * 新增支路信息记录
     */
    @Override
    public Boolean insertByBo(DevStatusBranchBo bo) {
        DevStatusBranch add = BeanUtil.toBean(bo, DevStatusBranch.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改支路信息记录
     */
    @Override
    public Boolean updateByBo(DevStatusBranchBo bo) {
        DevStatusBranch update = BeanUtil.toBean(bo, DevStatusBranch.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevStatusBranch entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除支路信息记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
