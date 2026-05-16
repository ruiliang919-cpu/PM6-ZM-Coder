package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.vo.ConfigDistrictRespVo;

// 分区配置表业务层接口
public interface ConfigDistrictService {
    TableDataInfo<ConfigDistrictRespVo> queryPageList(PageQuery pageQuery);


}
