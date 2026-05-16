package com.ruoyi.mqtt03.request.handler;

import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.DevConfigTimeControlAc;
import com.ruoyi.zm.domain.vo.TimeControlAcRespVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("Topic12")
@RequiredArgsConstructor
public class Topic12 implements RequestHandler {
    private final WriteController w;
    private final Key k;

    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        Map<String, Object> jlMap = (Map<String, Object>) k.getRemote(deviceNo, "0XAF1E");
        if (jlMap != null && !jlMap.isEmpty()) {
            List<DevConfigTimeControlAc> l = (List<DevConfigTimeControlAc>) jlMap.get("controlAc");
            if (l != null && !l.isEmpty()) {
                List<TimeControlAcRespVo> collect = l.stream().map(d -> new TimeControlAcRespVo() {{
                    setDeviceId(deviceNo);
                    setNo(d.getFrameId());
                    setEnabled(d.getEnable() == 1 ? 0 : 1);
                    setStime((d.getStimeHour() < 10 ? "0" + d.getStimeHour() : "" + d.getStimeHour()) + ":" + (d.getStimeMin() < 10 ? "0" + d.getStimeMin() : "" + d.getStimeMin()));
                    setEtime((d.getEtimeHour() < 10 ? "0" + d.getEtimeHour() : "" + d.getEtimeHour()) + ":" + (d.getEtimeMin() < 10 ? "0" + d.getEtimeMin() : "" + d.getEtimeMin()));
                    setStatus(d.getSwitchStatus() == 1 ? 0 : 1);
                }}).collect(Collectors.toList());
                w.updateAcControl(collect);
            }
        }
    }
}
