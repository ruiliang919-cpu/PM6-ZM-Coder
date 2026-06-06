package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevStatusAcLoop;
import com.ruoyi.zm.domain.bo.DevStatusAcLoopBo;
import com.ruoyi.zm.domain.vo.DevStatusAcLoopVo;
import com.ruoyi.zm.mapper.DevStatusAcLoopMapper;
import com.ruoyi.zm.service.IDevStatusAcLoopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 交流回路Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@RequiredArgsConstructor
@Service
public class DevStatusAcLoopServiceImpl implements IDevStatusAcLoopService {

    private final DevStatusAcLoopMapper baseMapper;

    /**
     * 查询交流回路
     */
    @Override
    public DevStatusAcLoopVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询交流回路列表
     */
    @Override
    public TableDataInfo<DevStatusAcLoopVo> queryPageList(DevStatusAcLoopBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevStatusAcLoop> lqw = buildQueryWrapper(bo);
        Page<DevStatusAcLoopVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询交流回路列表
     */
    @Override
    public List<DevStatusAcLoopVo> queryList(DevStatusAcLoopBo bo) {
        LambdaQueryWrapper<DevStatusAcLoop> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevStatusAcLoop> buildQueryWrapper(DevStatusAcLoopBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevStatusAcLoop> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevStatusAcLoop::getDeviceId, bo.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(bo.getNo()), DevStatusAcLoop::getNo, bo.getNo());
        lqw.eq(bo.getSwitchSetting() != null, DevStatusAcLoop::getSwitchSetting, bo.getSwitchSetting());
        lqw.eq(bo.getSwitchFeedback() != null, DevStatusAcLoop::getSwitchFeedback, bo.getSwitchFeedback());
        lqw.eq(bo.getOutputVoltage() != null, DevStatusAcLoop::getOutputVoltage, bo.getOutputVoltage());
        lqw.eq(bo.getOutputCurrent() != null, DevStatusAcLoop::getOutputCurrent, bo.getOutputCurrent());
        lqw.eq(bo.getGroupId() != null, DevStatusAcLoop::getGroupId, bo.getGroupId());
        return lqw;
    }

    /**
     * 新增交流回路
     */
    @Override
    public Boolean insertByBo(DevStatusAcLoopBo bo) {
        DevStatusAcLoop add = BeanUtil.toBean(bo, DevStatusAcLoop.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改交流回路
     */
    @Override
    public Boolean updateByBo(DevStatusAcLoopBo bo) {
        DevStatusAcLoop update = BeanUtil.toBean(bo, DevStatusAcLoop.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevStatusAcLoop entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除交流回路
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
