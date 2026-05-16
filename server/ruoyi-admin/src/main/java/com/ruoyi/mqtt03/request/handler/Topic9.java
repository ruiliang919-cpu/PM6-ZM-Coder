package com.ruoyi.mqtt03.request.handler;

import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.DevConfigTimeControlScene;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.SceneControlReqVo;
import com.ruoyi.zm.domain.vo.SceneControlTable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Service("Topic9")
@RequiredArgsConstructor
public class Topic9 implements RequestHandler {
    private final WriteController w;
    private final Key k;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String[] addr = {"", "0XAAEE", "0XAB2E", "0XAB6E"};

    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        int moduleId = payload.get("id").getAsInt();
        SceneControlReqVo s = new SceneControlReqVo();
        s.setControlId(moduleId);
        s.setDeviceId(deviceNo);
        boolean sEnabled = false;
        DevBaseDeviceTCPVo tcp = k.getCreateTCP(deviceNo);
        Map<String, Integer> sEnabledM = (HashMap<String, Integer>) redisTemplate.opsForValue().get(REMOTE_KEY + tcp.getIp() + ":" + deviceNo + ":" + addr[moduleId] + "Enabled");
        if (sEnabledM != null && !sEnabledM.isEmpty()) sEnabled = sEnabledM.get("data3") != null && sEnabledM.get("data3").equals(moduleId);
        s.setEnabled(sEnabled);
        List<DevConfigTimeControlScene> sceneData = (List<DevConfigTimeControlScene>) redisTemplate.opsForValue().get(REMOTE_KEY + tcp.getIp() + ":" + deviceNo + ":" + addr[moduleId]);
        s.setTable(null);
        if (sceneData != null && !sceneData.isEmpty()) {
            s.setTable(sceneData.stream().map(d -> new SceneControlTable() {{
                setTimeFrameId(d.getTimeFrameId());
                setEnabledStatus(d.getEnabledStatus());
                setEtime(d.getEtime());
                setStime(d.getStime());
                setSceneSelect(d.getSceneSelect());
            }}).collect(Collectors.toList()));
            w.updateSceneControl(s);
        }
    }
}
