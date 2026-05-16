package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigTime;
import com.ruoyi.zm.domain.bo.DevConfigTimeBo;
import com.ruoyi.zm.domain.vo.DevConfigTimeVo;
import com.ruoyi.zm.mapper.DevConfigTimeMapper;
import com.ruoyi.zm.service.IDevConfigTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 机柜时间设置Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-26
 */
@RequiredArgsConstructor
@Service
public class DevConfigTimeServiceImpl implements IDevConfigTimeService {

    private final DevConfigTimeMapper baseMapper;

    /**
     * 查询机柜时间设置
     */
    @Override
    public DevConfigTimeVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询机柜时间设置列表
     */
    @Override
    public TableDataInfo<DevConfigTimeVo> queryPageList(DevConfigTimeBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigTime> lqw = buildQueryWrapper(bo);
        Page<DevConfigTimeVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询机柜时间设置列表
     */
    @Override
    public List<DevConfigTimeVo> queryList(DevConfigTimeBo bo) {
        LambdaQueryWrapper<DevConfigTime> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigTime> buildQueryWrapper(DevConfigTimeBo bo) {
        LambdaQueryWrapper<DevConfigTime> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceNo() != null, DevConfigTime::getDeviceNo, bo.getDeviceNo());
        lqw.eq(bo.getYear() != null, DevConfigTime::getYear, bo.getYear());
        lqw.eq(bo.getMonth() != null, DevConfigTime::getMonth, bo.getMonth());
        lqw.eq(bo.getDay() != null, DevConfigTime::getDay, bo.getDay());
        lqw.eq(bo.getHour() != null, DevConfigTime::getHour, bo.getHour());
        lqw.eq(bo.getMinute() != null, DevConfigTime::getMinute, bo.getMinute());
        lqw.eq(bo.getSecond() != null, DevConfigTime::getSecond, bo.getSecond());
        lqw.eq(bo.getTimeSwitch() != null, DevConfigTime::getTimeSwitch, bo.getTimeSwitch());
        return lqw;
    }

    /**
     * 新增机柜时间设置
     */
    @Override
    public Boolean insertByBo(DevConfigTimeBo bo) {
        DevConfigTime add = BeanUtil.toBean(bo, DevConfigTime.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改机柜时间设置
     */
    @Override
    public Boolean updateByBo(DevConfigTimeBo bo) {
        DevConfigTime update = BeanUtil.toBean(bo, DevConfigTime.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigTime entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除机柜时间设置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
