package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.vo.WebLightStatusRespVO;

// 照明控制业务层接口
public interface LightingService {


    // 照明状态-照明控制列表（查数据库，不查设备）
    TableDataInfo<WebLightStatusRespVO> lightList(PageQuery pageQuery);
}
