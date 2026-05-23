package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaIgnore;

import com.ruoyi.cache.AcSwitchCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.send.WriteSendService;
import com.ruoyi.zm.domain.vo.PageWithIdReqVo;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@SaIgnore
@RestController
@RequestMapping("/acs")
@RequiredArgsConstructor
public class ACSController {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
    private final AcSwitchCache acSwitchCache;
    private final WriteSendService send;
    private final MqttPublisher mqttPublisher;

    @PostMapping("/getTable")
    public TableDataInfo<Integer> getTable(@RequestBody PageWithIdReqVo reqVo) {
        return acSwitchCache.table(reqVo);
    }

    @GetMapping("on")
    public R<?> on(Integer deviceId, Integer onNo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
//        send.writeToAddr(deviceId, 5, SWITCH_FLAG_ARR[onNo], 1, new Short[]{1});
//        send.writeToAddr(deviceId, 5, SWITCH_ARR[onNo], 1, new Short[]{1});
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        m2.put("msgId", IdGenerator.UUIDId());
        m2.put("value", 1);
        m2.put("arr", new Integer[]{onNo});
        map.put("data", m2);
        mqttPublisher.publish(deviceId, PublishKey.交流开关控制, map);
        return R.ok("操作成功");
    }

    @GetMapping("off")
    public R<?> off(Integer deviceId, Integer offNo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        m2.put("msgId", IdGenerator.UUIDId());
        m2.put("value", 0);
        m2.put("arr", new Integer[]{offNo});
        map.put("data", m2);
        mqttPublisher.publish(deviceId, PublishKey.交流开关控制, map);
        return R.ok("操作成功");
    }

    @GetMapping("allOn")
    public R<?> allOn(@NotNull(message = "设备编号未传递") Integer deviceId) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        AllOn(deviceId);
        return R.ok("操作成功");
    }

    public void AllOn(Integer deviceId) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        m2.put("msgId", IdGenerator.UUIDId());
        m2.put("value", 1);
        m2.put("arr", new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        map.put("data", m2);
        mqttPublisher.publish(deviceId, PublishKey.交流开关控制, map);
    }

    @GetMapping("allOff")
    public R<?> allOff(@NotNull(message = "设备编号未传递") Integer deviceId) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        AllOff(deviceId);
        return R.ok("操作成功");
    }

    public void AllOff(Integer deviceId) {
//        send.writeToAddr(deviceId, 5, SWITCH_FLAG_ARR[1], 8, new Short[]{1, 1, 1, 1, 1, 1, 1, 1});
//        send.writeToAddr(deviceId, 5, SWITCH_ARR[1], 8, new Short[]{0, 0, 0, 0, 0, 0, 0, 0});
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        m2.put("msgId", IdGenerator.UUIDId());
        m2.put("value", 0);
        m2.put("arr", new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        map.put("data", m2);
        mqttPublisher.publish(deviceId, PublishKey.交流开关控制, map);
    }

    // 当设备处于远程模式时以上接口不能使用
    private Boolean module() {
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        return "on-the-line".equals(module);
    }
}
