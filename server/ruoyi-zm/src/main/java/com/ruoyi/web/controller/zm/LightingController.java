package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaCheckPermission;

import com.ruoyi.cache.LightingCache;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.vo.ConfigDistrictRespVo;
import com.ruoyi.zm.domain.vo.WebLightStatusRespVO;
import com.ruoyi.zm.service.ConfigDistrictService;
import com.ruoyi.zm.service.LightingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 照明状态与控制 控制层
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/lighting")
public class LightingController {
    private final ConfigDistrictService configDistrictService;
    private final LightingService lightingService;
    private final LightingCache cache;

    // 查询照明控制-控制分区列表
    @SaCheckPermission("zm:lighting:zoneControlList")
    @PostMapping("/zoneControlList")
    public TableDataInfo<ConfigDistrictRespVo> zoneControlList(@RequestBody PageQuery pageQuery) {
        return configDistrictService.queryPageList(pageQuery);
    }

    // 照明状态-照明控制列表（查数据库，不查设备）
    @SaCheckPermission("zm:lighting:lightList")
    @PostMapping("/lightList")
    public TableDataInfo<WebLightStatusRespVO> lightList(@RequestBody PageQuery pageQuery) {
        TableDataInfo<WebLightStatusRespVO> lightList = cache.getLightList(pageQuery);
        if (lightList != null) return lightList;
        return lightingService.lightList(pageQuery);
    }
}
