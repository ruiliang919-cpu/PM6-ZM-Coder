package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevStatusAc;
import com.ruoyi.zm.domain.bo.DevStatusAcBo;
import com.ruoyi.zm.domain.vo.DevStatusAcVo;
import com.ruoyi.zm.mapper.DevStatusAcMapper;
import com.ruoyi.zm.service.IDevStatusAcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 交流信息Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-19
 */
@RequiredArgsConstructor
@Service
public class DevStatusAcServiceImpl implements IDevStatusAcService {

    private final DevStatusAcMapper baseMapper;

    /**
     * 查询交流信息
     */
    @Override
    public DevStatusAcVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询交流信息列表
     */
    @Override
    public TableDataInfo<DevStatusAcVo> queryPageList(DevStatusAcBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevStatusAc> lqw = buildQueryWrapper(bo);
        Page<DevStatusAcVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询交流信息列表
     */
    @Override
    public List<DevStatusAcVo> queryList(DevStatusAcBo bo) {
        LambdaQueryWrapper<DevStatusAc> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevStatusAc> buildQueryWrapper(DevStatusAcBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevStatusAc> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevStatusAc::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getCircuitNum() != null, DevStatusAc::getCircuitNum, bo.getCircuitNum());
        lqw.eq(bo.getUab() != null, DevStatusAc::getUab, bo.getUab());
        lqw.eq(bo.getUbc() != null, DevStatusAc::getUbc, bo.getUbc());
        lqw.eq(bo.getUac() != null, DevStatusAc::getUac, bo.getUac());
        lqw.eq(bo.getIa() != null, DevStatusAc::getIa, bo.getIa());
        lqw.eq(bo.getIb() != null, DevStatusAc::getIb, bo.getIb());
        lqw.eq(bo.getIc() != null, DevStatusAc::getIc, bo.getIc());
        return lqw;
    }

    /**
     * 新增交流信息
     */
    @Override
    public Boolean insertByBo(DevStatusAcBo bo) {
        DevStatusAc add = BeanUtil.toBean(bo, DevStatusAc.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改交流信息
     */
    @Override
    public Boolean updateByBo(DevStatusAcBo bo) {
        DevStatusAc update = BeanUtil.toBean(bo, DevStatusAc.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevStatusAc entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除交流信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
