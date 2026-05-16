package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevBaseRegion;
import com.ruoyi.zm.domain.bo.DevBaseRegionBo;
import com.ruoyi.zm.domain.vo.DevBaseRegionVo;
import com.ruoyi.zm.mapper.DevBaseRegionMapper;
import com.ruoyi.zm.service.IDevBaseRegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 设备区域Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-11
 */
@RequiredArgsConstructor
@Service
public class DevBaseRegionServiceImpl implements IDevBaseRegionService {

    private final DevBaseRegionMapper baseMapper;

    /**
     * 查询设备区域
     */
    @Override
    public DevBaseRegionVo queryById(Long id) {
        return baseMapper.selectVoById(id);
        // return  null;
    }

    /**
     * 查询设备区域列表
     */
    @Override
    public TableDataInfo<DevBaseRegionVo> queryPageList(DevBaseRegionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBaseRegion> lqw = buildQueryWrapper(bo);
        Page<DevBaseRegionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询设备区域列表
     */
    @Override
    public List<DevBaseRegionVo> queryList(DevBaseRegionBo bo) {
        LambdaQueryWrapper<DevBaseRegion> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBaseRegion> buildQueryWrapper(DevBaseRegionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevBaseRegion> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevBaseRegion::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增设备区域
     */
    @Override
    public Boolean insertByBo(DevBaseRegionBo bo) {
        LambdaQueryWrapper<DevBaseRegion> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(DevBaseRegion::getId);
        List<DevBaseRegion> devBaseRegions = baseMapper.selectList(lqw);
        long id = 1;
        if (devBaseRegions != null && !devBaseRegions.isEmpty()) {
            id = devBaseRegions.get(0).getId() + 1;
        }
        DevBaseRegion add = BeanUtil.toBean(bo, DevBaseRegion.class);
        add.setId(id);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备区域
     */
    @Override
    public Boolean updateByBo(DevBaseRegionBo bo) {
        DevBaseRegion update = BeanUtil.toBean(bo, DevBaseRegion.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevBaseRegion entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除设备区域
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
