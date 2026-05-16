package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevConfigGroup;
import com.ruoyi.zm.domain.bo.DevConfigGroupBo;
import com.ruoyi.zm.domain.vo.DevConfigGroupVo;
import com.ruoyi.zm.mapper.DevConfigGroupMapper;
import com.ruoyi.zm.service.IDevConfigGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 回路分组Service业务层处理
 *
 * @author ruoyi
 * @date 2024-08-16
 */
@RequiredArgsConstructor
@Service
public class DevConfigGroupServiceImpl implements IDevConfigGroupService {

    private final DevConfigGroupMapper baseMapper;


    /**
     * 查询分组回路
     */
    @Override
    public DevConfigGroupVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询分组回路列表
     */
    @Override
    public TableDataInfo<DevConfigGroupVo> queryPageList(DevConfigGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevConfigGroup> lqw = buildQueryWrapper(bo);
        Page<DevConfigGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询分组回路列表
     */
    @Override
    public List<DevConfigGroupVo> queryList(DevConfigGroupBo bo) {
        LambdaQueryWrapper<DevConfigGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevConfigGroup> buildQueryWrapper(DevConfigGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevConfigGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevConfigGroup::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getGroupId() != null, DevConfigGroup::getGroupId, bo.getGroupId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevConfigGroup::getName, bo.getName());
        lqw.eq(bo.getLoopNum() != null, DevConfigGroup::getLoopNum, bo.getLoopNum());
        lqw.eq(bo.getLux() != null, DevConfigGroup::getLux, bo.getLux());
        return lqw;
    }

    /**
     * 新增分组回路
     */
    @Override
    public Boolean insertByBo(DevConfigGroupBo bo) {
        DevConfigGroup add = BeanUtil.toBean(bo, DevConfigGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改分组回路
     */
    @Override
    public Boolean updateByBo(DevConfigGroupBo bo) {
        DevConfigGroup update = BeanUtil.toBean(bo, DevConfigGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevConfigGroup entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除分组回路
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }


}
