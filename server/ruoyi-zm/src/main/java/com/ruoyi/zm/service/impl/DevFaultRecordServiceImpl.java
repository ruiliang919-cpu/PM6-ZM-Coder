package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevFaultRecord;
import com.ruoyi.zm.domain.bo.DevFaultRecordBo;
import com.ruoyi.zm.domain.vo.DevFaultRecordVo;
import com.ruoyi.zm.mapper.DevFaultRecordMapper;
import com.ruoyi.zm.service.IDevFaultRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 告警记录Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-04
 */
@RequiredArgsConstructor
@Service
public class DevFaultRecordServiceImpl implements IDevFaultRecordService {

    private final DevFaultRecordMapper baseMapper;

    /**
     * 查询告警记录
     */
    @Override
    public DevFaultRecordVo queryById(Integer id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询告警记录列表
     */
    @Override
    public TableDataInfo<DevFaultRecordVo> queryPageList(DevFaultRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevFaultRecord> lqw = buildQueryWrapper(bo);
        Page<DevFaultRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询告警记录列表
     */
    @Override
    public List<DevFaultRecordVo> queryList(DevFaultRecordBo bo) {
        LambdaQueryWrapper<DevFaultRecord> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevFaultRecord> buildQueryWrapper(DevFaultRecordBo bo) {
        LambdaQueryWrapper<DevFaultRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DevFaultRecord::getDeviceId, bo.getDeviceId());
        lqw.like(StringUtils.isNotBlank(bo.getMessage()), DevFaultRecord::getMessage, bo.getMessage());
        if (bo.getStime() != null) {
            lqw.ge(DevFaultRecord::getStime, bo.getStime());
        }
        if (bo.getStime() != null && bo.getEtime() != null) {
            lqw.between(DevFaultRecord::getStime, bo.getStime(), bo.getEtime());
        }

        lqw.eq(bo.getShowType() != null, DevFaultRecord::getShowType, bo.getShowType());
        lqw.eq(bo.getType() != null, DevFaultRecord::getType, bo.getType());
        lqw.orderByDesc(DevFaultRecord::getStime);
        return lqw;
    }

    /**
     * 新增告警记录
     */
    @Override
    public Boolean insertByBo(DevFaultRecordBo bo) {
        DevFaultRecord add = BeanUtil.toBean(bo, DevFaultRecord.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改告警记录
     */
    @Override
    public Boolean updateByBo(DevFaultRecordBo bo) {
        DevFaultRecord update = BeanUtil.toBean(bo, DevFaultRecord.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevFaultRecord entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除告警记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public TableDataInfo<DevFaultRecordVo> queryPageList(DevFaultRecordBo bo, Integer pageSize, Integer pageNum) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageSize(pageSize);
        pageQuery.setPageNum(pageNum);
        return queryPageList(bo, pageQuery);
    }
}
