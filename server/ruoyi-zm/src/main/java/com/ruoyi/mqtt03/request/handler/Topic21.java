package com.ruoyi.mqtt03.request.handler;

import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.handler.addr0xA483.Addr0xA483;
import com.ruoyi.mqtt03.addr03.handler.addr0xA483.Addr0xA483Handler;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.vo.SceneParamsReqVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.ruoyi.cache.SceneCache.SCENE_NAME_ADDR_ARR;

@Service("Topic21")
@RequiredArgsConstructor
public class Topic21 implements RequestHandler {
    private final WriteController w;
    private final Key k;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        int moduleId = payload.getInt("id");
        Addr0xA483.Data d = (Addr0xA483.Data) redisTemplate.opsForHash().get(Addr0xA483Handler.writeKey + deviceNo, moduleId + "m");
        if (d != null) {
            SceneParamsReqVo s = new SceneParamsReqVo();
            s.setDeviceId(deviceNo);
            s.setSceneId(moduleId);
            s.setName(n(deviceNo, moduleId));
            s.setSelectGroupIdArr(d.getGroupIds());
            s.setSelectLuxArr(d.getLuxArr());
            Integer[] arr = new Integer[d.getLuxArr().length];
            for (int i = 0; i < d.getSwitchArr().length; i++) {
                arr[i] = d.getSwitchArr()[i] ? 1 : 0;
            }
            s.setSelectSwitchArr(arr);
            w.updateSceneParams(s);
        }
    }

    public String n(Integer deviceId, int id) {
        String ip = k.getCreateTCP(deviceId).getIp();
        String name = stringRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + ip + ":" + deviceId + ":" + SCENE_NAME_ADDR_ARR[id - 1]);
        if (name == null || name.isEmpty()) {
            name = "场景" + id;
        }
        return name;
    }
}
