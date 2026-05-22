package com.ruoyi.mqtt03.addr03.handler.addr0xA483;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import com.ruoyi.zm.domain.DevConfigScene;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.cache.Key.REMOTE_KEY;
import static com.ruoyi.cache.SceneCache.SCENE_NAME_ADDR_ARR;
import static com.ruoyi.cache.SceneCache.SCENE_PARAMS_ADDR_ARR;

@Service("AddrA483Handler")
@RequiredArgsConstructor
public class Addr0xA483Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;
    public static final String writeKey = "zm:handle:addr0xA483write:";

    @Timer("A483")
    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0xA483 body = JSONUtil.toBean(payload.toString(), Addr0xA483.class);
        List<Addr0xA483.Data> data = body.getData();
        String ip = key.getCreateTCP(deviceNo).getIp();
        String k = REMOTE_KEY + ip + ":" + deviceNo + ":";
        Map<String, Object> m = new HashMap<>();
        List<String> names = data.stream().map(Addr0xA483.Data::getName).collect(Collectors.toList());
        for (Addr0xA483.Data datum : data) {
            List<DevConfigScene> l = new ArrayList<>();
            List<Integer> gIds = Arrays.asList(datum.getGroupIds());
            Integer[] luxArr = datum.getLuxArr();
            Boolean[] switchArr = datum.getSwitchArr();
            for (int j = 0; j < 16; j++) {
                DevConfigScene s = new DevConfigScene();
                s.setSceneId((long) j);
                s.setLux(Long.valueOf(luxArr[j]));
                s.setDeviceId(Long.valueOf(deviceNo));
                s.setBtnStatus(switchArr[j] ? 1 : 0);
                s.setSelectStatus(gIds.contains(j + 1) ? 1 : 0);
                l.add(s);
            }
            int index = datum.getSceneId() - 1;
            m.put(k + SCENE_PARAMS_ADDR_ARR[index], l);
            m.put(k + SCENE_NAME_ADDR_ARR[index], names.get(index));
        }
        redisTemplate.opsForValue().multiSet(m);
        writeData(deviceNo, data);
    }

    public void writeData(int no, List<Addr0xA483.Data> data) {
        int[] id = {1};
        data.forEach(d -> {
            List<Integer> list = new ArrayList<>();
            for (int num : d.getGroupIds()) {
                if (num == 0) break;
                list.add(num);
            }
            d.setGroupIds(list.toArray(new Integer[0]));
            Integer[] i = new Integer[d.getSwitchArr().length];
            for (int i1 = 0; i1 < d.getSwitchArr().length; i1++)
                i[i1] = d.getSwitchArr()[i1] ? 1 : 0;
            d.setSwitchIntArr(i);
            redisTemplate.opsForHash().put(writeKey + no, id[0] + "m", d);
            id[0]++;
        });
    }
}
