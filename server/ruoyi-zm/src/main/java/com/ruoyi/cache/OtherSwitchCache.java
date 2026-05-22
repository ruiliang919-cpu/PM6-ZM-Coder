package com.ruoyi.cache;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.vo.PageWithIdReqVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtherSwitchCache {
    private final static String[] SWITCH_FLAG_ARR = {"",
        "调光信息-交流控制-手动开关标志01",
        "调光信息-交流控制-手动开关标志02",
        "调光信息-交流控制-手动开关标志03",
        "调光信息-交流控制-手动开关标志04",
        "调光信息-交流控制-手动开关标志05",
        "调光信息-交流控制-手动开关标志06",
        "调光信息-交流控制-手动开关标志07",
        "调光信息-交流控制-手动开关标志08"
    };
    private final static String[] SWITCH_ARR = {"",
        "调光信息-交流控制-开关01",
        "调光信息-交流控制-开关02",
        "调光信息-交流控制-开关03",
        "调光信息-交流控制-开关04",
        "调光信息-交流控制-开关05",
        "调光信息-交流控制-开关06",
        "调光信息-交流控制-开关07",
        "调光信息-交流控制-开关08"
    };
    private final Key key;

    // 直流机柜-机柜控制-其他开关控制-列表
    public TableDataInfo<Integer> table(PageWithIdReqVo reqVo) {
        List<Integer> result = new ArrayList<>();
        try {
            Integer num = (Integer) key.getTelemeter(reqVo.getDeviceId(), "0x0025");
            for (int i = 0; i < num; i++) {
                result.add(i + 1);
            }
        } catch (Exception ignored) {
            for (int i = 0; i < 48; i++) {
                result.add(i + 1);
            }
        }
        return key.getPageTable(result, reqVo.getPageNum(), reqVo.getPageSize());
    }
}
