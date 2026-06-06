package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevConfigTimeControl;
import com.ruoyi.zm.domain.bo.DevConfigTimeControlBo;
import com.ruoyi.zm.domain.vo.DevConfigTimeControlVo;
import com.ruoyi.zm.mapper.DevConfigTimeControlMapper;
import com.ruoyi.zm.service.IDevConfigTimeControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 普通模式-时控配置Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-26
 */
@RequiredArgsConstructor
@Service
public class DevConfigTimeControlServiceImpl implements IDevConfigTimeControlService {

    private final DevConfigTimeControlMapper baseMapper;

    /**
     * 查询普通模式-时控配置
     */
    @Override
    public DevConfigTimeControlVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询普通模式-时控配置列表
     */
    @Override
    public TableDataInfo<DevConfigTimeControlVo> queryPageList(DevConfigTimeControlBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigTimeControl> lqw = buildQueryWrapper(bo);
        Page<DevConfigTimeControlVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询普通模式-时控配置列表
     */
    @Override
    public List<DevConfigTimeControlVo> queryList(DevConfigTimeControlBo bo) {
        LambdaQueryWrapper<DevConfigTimeControl> lqw = buildQueryWrapper(bo);
        lqw.orderByAsc(DevConfigTimeControl::getId);
        // 使用 select distinct 替代 groupBy 无聚合，避免MySQL非确定性返回
        lqw.select(DevConfigTimeControl::getTimeControlId).groupBy(DevConfigTimeControl::getTimeControlId);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigTimeControl> buildQueryWrapper(DevConfigTimeControlBo bo) {
        LambdaQueryWrapper<DevConfigTimeControl> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigTimeControl::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getTimeControlId() != null, DevConfigTimeControl::getTimeControlId, bo.getTimeControlId());
        lqw.eq(bo.getTimeFrameId() != null, DevConfigTimeControl::getTimeFrameId, bo.getTimeFrameId());
        lqw.eq(bo.getLux() != null, DevConfigTimeControl::getLux, bo.getLux());
        lqw.eq(bo.getSwitchStatus() != null, DevConfigTimeControl::getSwitchStatus, bo.getSwitchStatus());
        lqw.eq(bo.getEnabledStatus() != null, DevConfigTimeControl::getEnabledStatus, bo.getEnabledStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getStime()), DevConfigTimeControl::getStime, bo.getStime());
        lqw.eq(StringUtils.isNotBlank(bo.getEtime()), DevConfigTimeControl::getEtime, bo.getEtime());
        return lqw;
    }

    /**
     * 新增普通模式-时控配置
     */
    @Override
    public Boolean insertByBo(DevConfigTimeControlBo bo) {
        DevConfigTimeControl add = BeanUtil.toBean(bo, DevConfigTimeControl.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改普通模式-时控配置
     */
    @Override
    public Boolean updateByBo(DevConfigTimeControlBo bo) {
        DevConfigTimeControl update = BeanUtil.toBean(bo, DevConfigTimeControl.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigTimeControl entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除普通模式-时控配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
