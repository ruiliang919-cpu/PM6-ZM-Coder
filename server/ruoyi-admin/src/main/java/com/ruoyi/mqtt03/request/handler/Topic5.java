package com.ruoyi.mqtt03.request.handler;

import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.vo.GroupNameVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("Topic5")
@RequiredArgsConstructor
public class Topic5 implements RequestHandler {
    private final Key key;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WriteController w;

    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        int id = payload.get("id").getAsInt();
        GroupNameVo g = new GroupNameVo();
        g.setGroupId(id);
        g.setName(n(deviceNo, id));
        g.setDeviceId(deviceNo);
        w.groupName(g);
    }

    public String n(Integer deviceId, int groupId) {
        String ip = key.getCreateTCP(deviceId).getIp();
        List<String> names = (List<String>) redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(ip, "0xA1F0", deviceId));
        if (names != null && !names.isEmpty()) return names.get(groupId - 1);
        return "分组" + groupId;
    }
}
