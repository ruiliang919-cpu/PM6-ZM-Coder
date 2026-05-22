package com.ruoyi.web.controller.zm;

import com.ruoyi.cache.OtherSwitchCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.zm.domain.vo.PageWithIdReqVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

// @SaIgnore
@RestController
@RequestMapping("/otherSwitch")
@RequiredArgsConstructor
public class OtherSwitchController {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final static String[] SWITCH_ARR = {"",
        "系统控制-其他开关-开关01",
        "系统控制-其他开关-开关02",
        "系统控制-其他开关-开关03",
        "系统控制-其他开关-开关04",
        "系统控制-其他开关-开关05",
        "系统控制-其他开关-开关06",
        "系统控制-其他开关-开关07",
        "系统控制-其他开关-开关08",
        "系统控制-其他开关-开关09",
        "系统控制-其他开关-开关10",
        "系统控制-其他开关-开关11",
        "系统控制-其他开关-开关12",
        "系统控制-其他开关-开关13",
        "系统控制-其他开关-开关14",
        "系统控制-其他开关-开关15",
        "系统控制-其他开关-开关16",
        "系统控制-其他开关-开关17",
        "系统控制-其他开关-开关18",
        "系统控制-其他开关-开关19",
        "系统控制-其他开关-开关20",
        "系统控制-其他开关-开关21",
        "系统控制-其他开关-开关22",
        "系统控制-其他开关-开关23",
        "系统控制-其他开关-开关24",
        "系统控制-其他开关-开关25",
        "系统控制-其他开关-开关26",
        "系统控制-其他开关-开关27",
        "系统控制-其他开关-开关28",
        "系统控制-其他开关-开关29",
        "系统控制-其他开关-开关30",
        "系统控制-其他开关-开关31",
        "系统控制-其他开关-开关32",
        "系统控制-其他开关-开关33",
        "系统控制-其他开关-开关34",
        "系统控制-其他开关-开关35",
        "系统控制-其他开关-开关36",
        "系统控制-其他开关-开关37",
        "系统控制-其他开关-开关38",
        "系统控制-其他开关-开关39",
        "系统控制-其他开关-开关40",
        "系统控制-其他开关-开关41",
        "系统控制-其他开关-开关42",
        "系统控制-其他开关-开关43",
        "系统控制-其他开关-开关44",
        "系统控制-其他开关-开关45",
        "系统控制-其他开关-开关46",
        "系统控制-其他开关-开关47",
        "系统控制-其他开关-开关48"
    };
    private final OtherSwitchCache otherSwitchCache;
//    private final WriteSendService send;
    private final MqttPublisher mqttPublisher;
    private final int[] arr48 = new int[48];

    @PostMapping("/getTable")
    public TableDataInfo<Integer> getTable(@RequestBody PageWithIdReqVo reqVo) {
        return otherSwitchCache.table(reqVo);
    }

    @GetMapping("on")
    public R<?> on(@NotNull(message = "设备编号未传递") Integer deviceId, Integer onNo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
//        send.writeToAddr(deviceId, 5, SWITCH_ARR[onNo], 1, new Short[]{1});
        arr48[onNo - 1] = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("data", arr48);
        mqttPublisher.publish(deviceId, PublishKey.系统控制其他开关开关0148, map);
        return R.ok("操作成功");
    }

    @GetMapping("off")
    public R<?> off(@NotNull(message = "设备编号未传递") Integer deviceId, Integer offNo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
//        send.writeToAddr(deviceId, 5, SWITCH_ARR[offNo], 1, new Short[]{0});
        arr48[offNo - 1] = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("data", arr48);
        mqttPublisher.publish(deviceId, PublishKey.系统控制其他开关开关0148, map);
        return R.ok("操作成功");
    }

    @GetMapping("allOn")
    public R<?> allOn(Integer deviceId) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        for (int i = 0; i < 48; i++) arr48[i] = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("data", arr48);
        mqttPublisher.publish(deviceId, PublishKey.系统控制其他开关开关0148, map);
        return R.ok("操作成功");
    }

    @GetMapping("allOff")
    public R<?> allOff(Integer deviceId) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        for (int i = 0; i < 48; i++) arr48[i] = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("data", arr48);
        mqttPublisher.publish(deviceId, PublishKey.系统控制其他开关开关0148, map);
        return R.ok("操作成功");
    }

    // 当设备处于远程模式时以上接口不能使用
    private Boolean module() {
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        return "on-the-line".equals(module);
    }
}
