package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.domain.bo.DevInstructBo;
import com.ruoyi.zm.domain.vo.DevInstructVo;
import com.ruoyi.zm.mapper.DevInstructMapper;
import com.ruoyi.zm.service.IDevInstructService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 读取协议的指令下发Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-05
 */
@RequiredArgsConstructor
@Service
public class DevInstructServiceImpl implements IDevInstructService {

    private final DevInstructMapper baseMapper;

    /**
     * 查询读取协议的指令下发
     */
    @Override
    public DevInstructVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询读取协议的指令下发列表
     */
    @Override
    public TableDataInfo<DevInstructVo> queryPageList(DevInstructBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevInstruct> lqw = buildQueryWrapper(bo);
        Page<DevInstructVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询读取协议的指令下发列表
     */
    @Override
    public List<DevInstructVo> queryList(DevInstructBo bo) {
        LambdaQueryWrapper<DevInstruct> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevInstruct> buildQueryWrapper(DevInstructBo bo) {
        LambdaQueryWrapper<DevInstruct> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getIp()), DevInstruct::getIp, bo.getIp());
        lqw.eq(bo.getSalveId() != null, DevInstruct::getSalveId, bo.getSalveId());
        lqw.eq(bo.getCode() != null, DevInstruct::getCode, bo.getCode());
        lqw.eq(StringUtils.isNotBlank(bo.getAddr()), DevInstruct::getAddr, bo.getAddr());
        lqw.eq(bo.getAddrNum() != null, DevInstruct::getAddrNum, bo.getAddrNum());
        lqw.eq(StringUtils.isNotBlank(bo.getWriteValue()), DevInstruct::getWriteValue, bo.getWriteValue());
        lqw.eq(bo.getType() != null, DevInstruct::getType, bo.getType());
        lqw.eq(bo.getFeedback() != null, DevInstruct::getFeedback, bo.getFeedback());
        lqw.eq(bo.getTimestamp() != null, DevInstruct::getTimestamp, bo.getTimestamp());
        return lqw;
    }

    /**
     * 新增读取协议的指令下发
     */
    @Override
    public Boolean insertByBo(DevInstructBo bo) {
        DevInstruct add = BeanUtil.toBean(bo, DevInstruct.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改读取协议的指令下发
     */
    @Override
    public Boolean updateByBo(DevInstructBo bo) {
        DevInstruct update = BeanUtil.toBean(bo, DevInstruct.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevInstruct entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除读取协议的指令下发
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
