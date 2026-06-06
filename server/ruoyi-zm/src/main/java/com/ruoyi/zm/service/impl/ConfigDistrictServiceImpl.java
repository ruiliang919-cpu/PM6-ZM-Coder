package com.ruoyi.zm.service.impl;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseDistrict;
import com.ruoyi.zm.domain.vo.ConfigDistrictRespVo;
import com.ruoyi.zm.mapper.ConfigDistrictMapper;
import com.ruoyi.zm.mapper.DevBaseDistrictAssistMapper;
import com.ruoyi.zm.mapper.DevBaseDistrictMapper;
import com.ruoyi.zm.service.ConfigDistrictService;
import com.ruoyi.zm.service.IDevBaseDistrictService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigDistrictServiceImpl implements ConfigDistrictService {
    @Resource
    private ConfigDistrictMapper configDistrictMapper;
    @Resource
    private DevBaseDistrictAssistMapper devBaseDistrictAssistMapper;
    @Resource
    private DevBaseDistrictMapper districtMapper;
    @Resource
    private IDevBaseDistrictService districtService;


    @Override
    public TableDataInfo<ConfigDistrictRespVo> queryPageList(PageQuery pageQuery) {
        List<DevBaseDistrict> source = districtMapper.selectList();
        List<ConfigDistrictRespVo> result = new ArrayList<>();
        source.forEach(item -> {
            ConfigDistrictRespVo vo = new ConfigDistrictRespVo();
            vo.setOrderNo(item.getOrderNo());
            vo.setId(Math.toIntExact(item.getId()));
            vo.setName(item.getName());
            Integer switchStatus = item.getSwitchStatus();
            if (switchStatus == null) switchStatus = 0;
            // 前端页面 0 开 1 关
            vo.setSwitchStatus(switchStatus == 1 ? 0 : 1);
            Integer lux = item.getLux();
            if (lux == null) lux = 0;
            vo.setLux(lux);
            result.add(vo);
        });
        return getPageTable(result, pageQuery);
    }

    public <T> TableDataInfo<T> getPageTable(List<T> source, PageQuery page) {
        int pageNum = page.getPageNum() != null ? page.getPageNum() : 1;
        int pageSize = page.getPageSize() != null ? page.getPageSize() : 10;
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, source.size());
        TableDataInfo<T> table = TableDataInfo.build();
        table.setTotal(source.size());
        table.setRows(source.subList(start, end));
        return table;
    }
}




