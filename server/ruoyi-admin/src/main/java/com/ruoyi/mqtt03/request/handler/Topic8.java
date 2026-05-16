package com.ruoyi.mqtt03.request.handler;

import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.DevConfigSimpleGroup;
import com.ruoyi.zm.domain.DevConfigTimeControl;
import com.ruoyi.zm.domain.vo.SimpleControlReqVo;
import com.ruoyi.zm.domain.vo.SimpleControlTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.ruoyi.mqtt03.addr03.handler.addr0XA80A.Addr0XA80AHandler.addr;

@Service("Topic8")
@RequiredArgsConstructor
public class Topic8 implements RequestHandler {
    private final WriteController w;
    private final Key k;

    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        int moduleId = payload.get("id").getAsInt();
        HashMap<String, Object> simple = (HashMap<String, Object>) k.getRemote(deviceNo, addr[moduleId]);
        if (simple != null && !simple.isEmpty()) {
            SimpleControlReqVo s = new SimpleControlReqVo();
            s.setControlId(moduleId);
            s.setDeviceId(deviceNo);
            s.setEnabled((Boolean) simple.getOrDefault("data3", false));
            s.setGroupIds(null);
            List<DevConfigSimpleGroup> data2 = (List<DevConfigSimpleGroup>) simple.getOrDefault("data2", null);
            if (data2 != null && !data2.isEmpty())
                s.setGroupIds(data2.stream().filter(d -> d.getSelectStatus() == 1)
                    .map(DevConfigSimpleGroup::getGroupId).toArray(Integer[]::new));
            List<DevConfigTimeControl> data1 = (List<DevConfigTimeControl>) simple.getOrDefault("data1", null);
            s.setTable(null);
            if (data1 != null && !data1.isEmpty()) {
                int[] id = {1};
                s.setTable(data1.stream().map(d -> new SimpleControlTable() {{
                    setControlId(moduleId);
                    setFrameId(id[0]);
                    setEnabledStatus(d.getEnabledStatus());
                    setEtime(d.getEtime());
                    setStime(d.getStime());
                    setSwitchStatus(d.getSwitchStatus());
                    setLux(d.getLux());
                    id[0]++;
                }}).collect(Collectors.toList()));
            }
            w.updateSimpleControl(s);
        }
    }
}
