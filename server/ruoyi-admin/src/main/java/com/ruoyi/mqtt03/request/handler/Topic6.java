package com.ruoyi.mqtt03.request.handler;

import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.ruoyi.cache.SceneCache.SCENE_NAME_ADDR_ARR;

@Service("Topic6")
@RequiredArgsConstructor
public class Topic6 implements RequestHandler {
    private final WriteController w;
    private final Key key;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        int id = payload.get("id").getAsInt();
        w.sceneName(deviceNo, id, n(deviceNo, id));
    }

    public String n(Integer deviceId, int id) {
        String ip = key.getCreateTCP(deviceId).getIp();
        String name = stringRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + ip + ":" + deviceId + ":" + SCENE_NAME_ADDR_ARR[id - 1]);
        if (name == null || name.isEmpty()) name = "场景" + id;
        if (name.startsWith("\"")) name = name.substring(1, name.length() - 1);
        return name;
    }
}
