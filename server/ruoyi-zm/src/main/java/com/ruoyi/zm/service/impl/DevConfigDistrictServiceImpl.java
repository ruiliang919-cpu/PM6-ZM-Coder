package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevConfigDistrict;
import com.ruoyi.zm.domain.bo.DevConfigDistrictBo;
import com.ruoyi.zm.domain.vo.DevConfigDistrictVo;
import com.ruoyi.zm.mapper.DevConfigDistrictMapper;
import com.ruoyi.zm.service.IDevConfigDistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 分区配置Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-26
 */
@RequiredArgsConstructor
@Service
public class DevConfigDistrictServiceImpl implements IDevConfigDistrictService {

    private final DevConfigDistrictMapper baseMapper;

    /**
     * 查询分区配置
     */
    @Override
    public DevConfigDistrictVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询分区配置列表
     */
    @Override
    public TableDataInfo<DevConfigDistrictVo> queryPageList(DevConfigDistrictBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigDistrict> lqw = buildQueryWrapper(bo);
        Page<DevConfigDistrictVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询分区配置列表
     */
    @Override
    public List<DevConfigDistrictVo> queryList(DevConfigDistrictBo bo) {
        LambdaQueryWrapper<DevConfigDistrict> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigDistrict> buildQueryWrapper(DevConfigDistrictBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevConfigDistrict> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigDistrict::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getDistrictId() != null, DevConfigDistrict::getDistrictId, bo.getDistrictId());
        lqw.eq(bo.getAddr() != null, DevConfigDistrict::getAddr, bo.getAddr());
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevConfigDistrict::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增分区配置
     */
    @Override
    public Boolean insertByBo(DevConfigDistrictBo bo) {
        DevConfigDistrict add = BeanUtil.toBean(bo, DevConfigDistrict.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改分区配置
     */
    @Override
    public Boolean updateByBo(DevConfigDistrictBo bo) {
        DevConfigDistrict update = BeanUtil.toBean(bo, DevConfigDistrict.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigDistrict entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除分区配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
