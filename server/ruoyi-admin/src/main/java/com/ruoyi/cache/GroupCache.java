package com.ruoyi.cache;

import com.ruoyi.schedule.InstructionQueue;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupCache {
    private final Key key;
    private final InstructionQueue queue;
    public static final String[] GROUP_ADDR_ARR = new String[]{
        "0xA1F0", "0xA1FA", "0xA204", "0xA20E", "0xA218", "0xA222", "0xA22C", "0xA236",
        "0xA240", "0xA24A", "0xA254", "0xA25E", "0xA268", "0xA272", "0xA27C", "0xA286"};

    public static final String[] GROUP_NO_ARR = new String[]{
        "0x23F5",
        "0x241D",
        "0x2445",
        "0x246D",
        "0x2495",
        "0x24BD",
        "0x24E5",
        "0x250D",
        "0x2535",
        "0x255D",
        "0x2585",
        "0x25AD",
        "0x25D5",
        "0x25FD",
        "0x2625",
        "0x264D"
    };

    public static String[] GROUP_NAME_ADDR = {
        "系统设置-回路分组-分组命名01_01",
        "系统设置-回路分组-分组命名02_01",
        "系统设置-回路分组-分组命名03_01",
        "系统设置-回路分组-分组命名04_01",
        "系统设置-回路分组-分组命名05_01",
        "系统设置-回路分组-分组命名06_01",
        "系统设置-回路分组-分组命名07_01",
        "系统设置-回路分组-分组命名08_01",
        "系统设置-回路分组-分组命名09_01",
        "系统设置-回路分组-分组命名10_01",
        "系统设置-回路分组-分组命名11_01",
        "系统设置-回路分组-分组命名12_01",
        "系统设置-回路分组-分组命名13_01",
        "系统设置-回路分组-分组命名14_01",
        "系统设置-回路分组-分组命名15_01",
        "系统设置-回路分组-分组命名16_01"
    };

    // 设备ID获取分组名称列表
    public List<String> groupNames(Integer deviceId) {
        List<String> groupNames = (List<String>) key.getTelemeter(deviceId, "0xA1F0");
        if (groupNames != null) return groupNames;
        groupNames = new ArrayList<>();
        for (int i = 0; i < GROUP_ADDR_ARR.length; i++) groupNames.add("分组" + (i + 1));
        return groupNames;
    }

    // 根据设备ID与分组ID获取分组名称
    public String getGroupName(Integer deviceId, Integer groupId) {
        short[] arr = key.getRemoteByArr(deviceId, GROUP_ADDR_ARR[groupId - 1]);
        if (arr != null) {
            return ScaleUtil.toAscII(arr);
        }
        return "";
    }
}
