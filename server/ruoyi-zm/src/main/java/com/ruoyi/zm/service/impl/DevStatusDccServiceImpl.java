package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevStatusDcc;
import com.ruoyi.zm.domain.bo.DevStatusDccBo;
import com.ruoyi.zm.domain.vo.DevStatusDccVo;
import com.ruoyi.zm.mapper.DevStatusDccMapper;
import com.ruoyi.zm.service.IDevStatusDccService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 直流信息Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@RequiredArgsConstructor
@Service
public class DevStatusDccServiceImpl implements IDevStatusDccService {

    private final DevStatusDccMapper baseMapper;

    /**
     * 查询直流信息
     */
    @Override
    public DevStatusDccVo queryById(Long id){
        //return baseMapper.selectVoById(id);
        return null;
    }

    /**
     * 查询直流信息列表
     */
    @Override
    public TableDataInfo<DevStatusDccVo> queryPageList(DevStatusDccBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevStatusDcc> lqw = buildQueryWrapper(bo);
        Page<DevStatusDccVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询直流信息列表
     */
    @Override
    public List<DevStatusDccVo> queryList(DevStatusDccBo bo) {
        LambdaQueryWrapper<DevStatusDcc> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevStatusDcc> buildQueryWrapper(DevStatusDccBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevStatusDcc> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevStatusDcc::getDeviceId, bo.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(bo.getNo()), DevStatusDcc::getNo, bo.getNo());
        lqw.eq(bo.getRunStatus() != null, DevStatusDcc::getRunStatus, bo.getRunStatus());
        lqw.eq(bo.getOnlineStatus() != null, DevStatusDcc::getOnlineStatus, bo.getOnlineStatus());
        lqw.eq(bo.getOnOffStatus() != null, DevStatusDcc::getOnOffStatus, bo.getOnOffStatus());
        lqw.eq(bo.getOutputVoltage() != null, DevStatusDcc::getOutputVoltage, bo.getOutputVoltage());
        lqw.eq(bo.getOutputCurrent() != null, DevStatusDcc::getOutputCurrent, bo.getOutputCurrent());
        return lqw;
    }

    /**
     * 新增直流信息
     */
    @Override
    public Boolean insertByBo(DevStatusDccBo bo) {
        DevStatusDcc add = BeanUtil.toBean(bo, DevStatusDcc.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改直流信息
     */
    @Override
    public Boolean updateByBo(DevStatusDccBo bo) {
        DevStatusDcc update = BeanUtil.toBean(bo, DevStatusDcc.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevStatusDcc entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除直流信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
