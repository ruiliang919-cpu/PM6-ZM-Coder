package com.ruoyi.zm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.StatusAcDc;
import com.ruoyi.zm.domain.bo.StatusAcDcBo;
import com.ruoyi.zm.domain.vo.StatusAcDcVo;
import com.ruoyi.zm.mapper.StatusAcDcMapper;
import com.ruoyi.zm.service.StatusAcDcService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class StatusAcDcServiceImpl implements StatusAcDcService {
    @Resource
    private StatusAcDcMapper baseMapper;


    @Override
    public TableDataInfo<StatusAcDcVo> queryPageList(StatusAcDcBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<StatusAcDc> lqw = buildQueryWrapper(bo);
        Page<StatusAcDcVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<StatusAcDc> queryList(int salveId) {
        LambdaQueryWrapper<StatusAcDc> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StatusAcDc::getDeviceId,salveId);
        return baseMapper.selectList(wrapper);
    }

    private LambdaQueryWrapper<StatusAcDc> buildQueryWrapper(StatusAcDcBo bo) {
        LambdaQueryWrapper<StatusAcDc> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, StatusAcDc::getDeviceId, bo.getDeviceId());
        return lqw;
    }

}
