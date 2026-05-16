package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseLoss;
import com.ruoyi.zm.domain.bo.DevBaseLossBo;
import com.ruoyi.zm.domain.vo.DevBaseLossVo;
import com.ruoyi.zm.mapper.DevBaseLossMapper;
import com.ruoyi.zm.service.IDevBaseLossService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 总功率Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@RequiredArgsConstructor
@Service
public class DevBaseLossServiceImpl implements IDevBaseLossService {

    private final DevBaseLossMapper baseMapper;

    /**
     * 查询总功率
     */
    @Override
    public DevBaseLossVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询总功率列表
     */
    @Override
    public TableDataInfo<DevBaseLossVo> queryPageList(DevBaseLossBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBaseLoss> lqw = buildQueryWrapper(bo);
        Page<DevBaseLossVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询总功率列表
     */
    @Override
    public List<DevBaseLossVo> queryList(DevBaseLossBo bo) {
        LambdaQueryWrapper<DevBaseLoss> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBaseLoss> buildQueryWrapper(DevBaseLossBo bo) {

        LambdaQueryWrapper<DevBaseLoss> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevBaseLoss::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getLoss() != null, DevBaseLoss::getLoss, bo.getLoss());
        return lqw;
    }

    /**
     * 新增总功率
     */
    @Override
    public Boolean insertByBo(DevBaseLossBo bo) {
        DevBaseLoss add = BeanUtil.toBean(bo, DevBaseLoss.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改总功率
     */
    @Override
    public Boolean updateByBo(DevBaseLossBo bo) {
        DevBaseLoss update = BeanUtil.toBean(bo, DevBaseLoss.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevBaseLoss entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除总功率
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
