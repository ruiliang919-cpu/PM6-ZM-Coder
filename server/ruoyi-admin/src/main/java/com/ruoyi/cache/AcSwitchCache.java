package com.ruoyi.cache;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigTimeControlAc;
import com.ruoyi.zm.domain.vo.PageWithIdReqVo;
import com.ruoyi.zm.domain.vo.TimeControlAcRespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AcSwitchCache {
    public static String[] arr = {
        "系统设置-控制方式-交流开关-时段信息1-开关",
        "系统设置-控制方式-交流开关-时段信息2-开关",
        "系统设置-控制方式-交流开关-时段信息3-开关",
        "系统设置-控制方式-交流开关-时段信息4-开关",
        "系统设置-控制方式-交流开关-时段信息5-开关",
        "系统设置-控制方式-交流开关-时段信息6-开关",
        "系统设置-控制方式-交流开关-时段信息7-开关",
        "系统设置-控制方式-交流开关-时段信息8-开关"
    };
    private final Key key;

    // 机柜设置-交流开关 列表
    public TableDataInfo<TimeControlAcRespVo> acSwitchList(PageWithIdReqVo reqVo) {
        try {
            List<TimeControlAcRespVo> source = new ArrayList<>();
            Map<String, Object> map = (Map<String, Object>) key.getRemote(reqVo.getDeviceId(), "0XAF1E");
            List<DevConfigTimeControlAc> data = (List<DevConfigTimeControlAc>) map.get("controlAc");
            String sTime, sMin, eTime, eMin;
            for (int i = 0; i < 8; i++) {
                TimeControlAcRespVo vo = new TimeControlAcRespVo();
                vo.setDeviceId(reqVo.getDeviceId());
                vo.setNo(data.get(i).getFrameId());
                vo.setStatus(data.get(i).getSwitchStatus() == 1 ? 0 : 1);
                vo.setEnabled(data.get(i).getEnable() == 1 ? 0 : 1);
                if (data.get(i).getStimeHour() <= 9) sTime = "0" + data.get(i).getStimeHour();
                else sTime = data.get(i).getStimeHour() + "";
                if (data.get(i).getStimeMin() <= 9) sMin = "0" + data.get(i).getStimeMin();
                else sMin = data.get(i).getStimeMin() + "";
                if (data.get(i).getEtimeHour() <= 9) eTime = "0" + data.get(i).getEtimeHour();
                else eTime = data.get(i).getEtimeHour() + "";
                if (data.get(i).getEtimeMin() <= 9) eMin = "0" + data.get(i).getEtimeMin();
                else eMin = data.get(i).getEtimeMin() + "";
                vo.setStime(sTime + ":" + sMin);
                vo.setEtime(eTime + ":" + eMin);
                source.add(vo);
            }
            return key.getPageTable(source, reqVo.getPageNum(), reqVo.getPageSize());
        } catch (Exception e) {
            // log.error("AcSwitchCache → acSwitchList", e);
        }
        return null;
    }

    // 直流机柜-机柜控制-交流开关控制-列表
    public TableDataInfo<Integer> table(PageWithIdReqVo reqVo) {
        List<Integer> result = new ArrayList<>();
        int acLoopNum = 0;
        Object remote = key.getRemote(reqVo.getDeviceId(), "0XAF1Enum");
        if (remote != null) acLoopNum = Integer.parseInt(remote.toString());
        for (int i = 0; i < acLoopNum; i++) result.add(i + 1);
        return key.getPageTable(result, reqVo.getPageNum(), reqVo.getPageSize());
    }
}
