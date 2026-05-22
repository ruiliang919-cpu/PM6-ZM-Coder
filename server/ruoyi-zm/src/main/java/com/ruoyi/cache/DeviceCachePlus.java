package com.ruoyi.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceCachePlus {
    private final Key key;

    // 单独获取调光与开关模块
    public HashMap<String, Integer> getDcDimNum(Integer deviceId) {
        try {
            return new HashMap<String, Integer>() {{
                put("dimmerNum", ((java.math.BigDecimal) key.getTelemeter(deviceId, "0x0022")).intValue());
                put("dcModuleNum", ((java.math.BigDecimal) key.getTelemeter(deviceId, "0x0027")).intValue());
            }};
        } catch (Exception e) {
            log.error("DeviceCache → getDcDimNum", e);
        }
        return new HashMap<>();
    }
}
