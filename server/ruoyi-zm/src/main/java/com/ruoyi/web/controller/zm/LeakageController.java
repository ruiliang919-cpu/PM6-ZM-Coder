package com.ruoyi.web.controller.zm;


import com.ruoyi.cache.LeakageCache;
import com.ruoyi.common.core.page.TableDataInfo;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 漏电流
@RestController
@RequestMapping("/leakage")
@RequiredArgsConstructor
public class LeakageController {
    private final LeakageCache leakageCache;

    @PostMapping("/getTable")
    public TableDataInfo<?> getTable(@RequestBody Map<String, Object> req) throws ModbusTransportException {
        return leakageCache.table((Integer) req.getOrDefault("deviceId", -1),
            (Integer) req.getOrDefault("pageNum", 1),
            (Integer) req.getOrDefault("pageSize", 10));
    }

}
