package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevWriteInstruct;
import com.ruoyi.zm.domain.bo.DevWriteInstructBo;
import com.ruoyi.zm.domain.vo.DevWriteInstructVo;
import com.ruoyi.zm.mapper.DevWriteInstructMapper;
import com.ruoyi.zm.service.IDevWriteInstructService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 写入协议的指令下发Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-05
 */
@RequiredArgsConstructor
@Service
public class DevWriteInstructServiceImpl implements IDevWriteInstructService {

    private final DevWriteInstructMapper baseMapper;

    /**
     * 查询写入协议的指令下发
     */
    @Override
    public DevWriteInstructVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询写入协议的指令下发列表
     */
    @Override
    public TableDataInfo<DevWriteInstructVo> queryPageList(DevWriteInstructBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevWriteInstruct> lqw = buildQueryWrapper(bo);
        Page<DevWriteInstructVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询写入协议的指令下发列表
     */
    @Override
    public List<DevWriteInstructVo> queryList(DevWriteInstructBo bo) {
        LambdaQueryWrapper<DevWriteInstruct> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevWriteInstruct> buildQueryWrapper(DevWriteInstructBo bo) {
        LambdaQueryWrapper<DevWriteInstruct> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getIp()), DevWriteInstruct::getIp, bo.getIp());
        lqw.eq(bo.getSalveId() != null, DevWriteInstruct::getSalveId, bo.getSalveId());
        lqw.eq(bo.getCode() != null, DevWriteInstruct::getCode, bo.getCode());
        lqw.eq(StringUtils.isNotBlank(bo.getAddr()), DevWriteInstruct::getAddr, bo.getAddr());
        lqw.eq(bo.getAddrNum() != null, DevWriteInstruct::getAddrNum, bo.getAddrNum());
        lqw.eq(StringUtils.isNotBlank(bo.getWriteValue()), DevWriteInstruct::getWriteValue, bo.getWriteValue());
        lqw.eq(bo.getType() != null, DevWriteInstruct::getType, bo.getType());
        lqw.eq(bo.getFeedback() != null, DevWriteInstruct::getFeedback, bo.getFeedback());
        lqw.eq(bo.getTimestamp() != null, DevWriteInstruct::getTimestamp, bo.getTimestamp());
        lqw.orderByDesc(DevWriteInstruct::getTimestamp);
        return lqw;
    }

    /**
     * 新增写入协议的指令下发
     */
    @Override
    public Boolean insertByBo(DevWriteInstructBo bo) {
        DevWriteInstruct add = BeanUtil.toBean(bo, DevWriteInstruct.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改写入协议的指令下发
     */
    @Override
    public Boolean updateByBo(DevWriteInstructBo bo) {
        DevWriteInstruct update = BeanUtil.toBean(bo, DevWriteInstruct.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevWriteInstruct entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除写入协议的指令下发
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
