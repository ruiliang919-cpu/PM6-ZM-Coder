package com.ruoyi.web.controller.zm.write;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.ModuleGuard;
import com.ruoyi.cache.LoopByGroupCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqttwrite.common.CommonDataInt;
import com.ruoyi.mqttwrite.common.CommonDataString;
import com.ruoyi.mqttwrite.loopcontrol.LoopControlVO;
import com.ruoyi.zm.domain.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.DevBaseDistrict;
import com.ruoyi.zm.domain.vo.GroupNameVo;
import com.ruoyi.zm.domain.vo.UpdateLoopReqVo;
import com.ruoyi.zm.domain.vo.WriteGroupReqVo;
import com.ruoyi.zm.mapper.DevBaseDistrictMapper;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.ruoyi.zm.utils.ScaleUtil.combineIDs;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write/group")
public class WriteGroupController {

    private final MqttPublisher mqttPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, int[]> intArrayRedisTemplate;
    private final RedisTemplate<String, short[]> shortArrayRedisTemplate;
    private final Key key;
    private final DevBaseDistrictMapper districtMapper;

    public static final Integer[] DELETE_LOOP_NOS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33,
        34, 35, 36, 37, 38, 39, 40};

    private final ModuleGuard moduleGuard;

    @PostMapping("/groupName")
    public R<?> groupName1(@RequestBody GroupNameVo groupNameVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return groupName(groupNameVo);
    }

    public R<?> groupName(@RequestBody GroupNameVo groupNameVo) {
        CommonDataString d = new CommonDataString();
        d.setAddr("0xA1F0");
        CommonDataString.D d1 = new CommonDataString.D();
        d1.setId(groupNameVo.getGroupId());
        d1.setValue(groupNameVo.getName());
        d.setData(d1);
        mqttPublisher.publish(groupNameVo.getDeviceId(), PublishKey.分组命名, d);

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(groupNameVo.getDeviceId());

        List<String> names = (List<String>) redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(tcpVo.getIp(), "0xA1F0", groupNameVo.getDeviceId()));
        if (names == null || names.isEmpty()) {
            names = new ArrayList<>();
            for (int i = 0; i < 16; i++) names.add("分组" + (i + 1));
            if (groupNameVo.getGroupId() <= 16) {
                if (names.size() > groupNameVo.getGroupId() - 1) {
                    names.set(groupNameVo.getGroupId() - 1, groupNameVo.getName());
                } else names.add(groupNameVo.getName());
            }
        } else {
            if (groupNameVo.getGroupId() > 0 && groupNameVo.getGroupId() <= names.size()) {
                names.set(groupNameVo.getGroupId() - 1, groupNameVo.getName());
            } else {
                while (names.size() < groupNameVo.getGroupId())
                    names.add("分组" + (names.size() + 1));
                names.add(groupNameVo.getName());
            }
        }
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(tcpVo.getIp(), "0xA1F0", groupNameVo.getDeviceId()), names);

        DevBaseDistrict devBaseDistrict = districtMapper.selectById(groupNameVo.getId());
        short[] codes = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        try {
            short[] temp = key.getRemoteByArr(groupNameVo.getDeviceId(), "0xA5AE");
            if (temp[0] != -1) {
                codes = temp;
            }
            codes[groupNameVo.getGroupId() - 1] = (short) ((long) devBaseDistrict.getId());
        } catch (Exception e) {
            log.error("Error reading remote array for groupName", e);
        }
        shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0xA5AE", codes);

        return R.ok("指令已下发");
    }

    @PostMapping("/updateLoop")
    public synchronized R<?> updateLoop1(@RequestBody UpdateLoopReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateLoop(reqVo);
    }

    public R<?> updateLoop(@RequestBody UpdateLoopReqVo reqVo) {
        String groupId;
        Integer deviceId = reqVo.getDeviceId();
        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        if (reqVo.getGroupId() != null) {
            int[] ints = new int[40];
            Map<String, Object> m = new HashMap<>();
            m.put("groupId", reqVo.getGroupId());
            m.put("loopNo", ints);
            if (reqVo.getGroupId() < 10) groupId = "0" + reqVo.getGroupId();
            else groupId = "" + reqVo.getGroupId();
            if (reqVo.getLoopNo() != null && reqVo.getLoopNo().length > 0) {
                Integer[] loopNos = reqVo.getLoopNo();
                String groupStr = combineIDs(groupId, loopNos);
                StringBuilder resultGroupStr = new StringBuilder();
                for (String s : ScaleUtil.splitIntoPairs(groupStr.substring(2))) {
                    if (!"00".equals(s)) resultGroupStr.append(s);
                }
                ArrayList<Integer> integers = new ArrayList<>(java.util.Arrays.asList(loopNos));
                List<Integer> l = new ArrayList<>();
                for (int i = 1; i < 41; i++) {
                    if (integers.contains(i)) {
                        l.add(1);
                    } else l.add(0);
                }
                m.put("loopNo", l);
                redisTemplate.opsForValue().set("zm:queue:zm:cache:3:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + LoopByGroupCache.LOOP_NUMS_ADDR_ARR[reqVo.getGroupId() - 1], resultGroupStr.toString());
            } else
                redisTemplate.opsForValue().set("zm:queue:zm:cache:3:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + LoopByGroupCache.LOOP_NUMS_ADDR_ARR[reqVo.getGroupId() - 1], "");
            mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.保存回路, m);

            List<Long> loops = (List<Long>) redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(tcpVo.getIp(), "0x23F5num", reqVo.getDeviceId()));
            if (loops == null || loops.isEmpty()) {
                loops = new ArrayList<>();
                for (int i = 0; i < 16; i++) loops.add(0L);
                if (reqVo.getGroupId() <= 16) {
                    if (loops.size() > reqVo.getGroupId() - 1) {
                        loops.set(reqVo.getGroupId() - 1, (long) reqVo.getLoopNo().length);
                    } else loops.add(0L);
                }
            } else {
                if (reqVo.getGroupId() > 0 && reqVo.getGroupId() <= loops.size()) {
                    loops.set(reqVo.getGroupId() - 1, (long) reqVo.getLoopNo().length);
                } else {
                    while (loops.size() < reqVo.getGroupId())
                        loops.add((long) reqVo.getLoopNo().length);
                    loops.add((long) reqVo.getLoopNo().length);
                }
            }
            redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(tcpVo.getIp(), "0x23F5num", reqVo.getDeviceId()), loops);

            if (reqVo.getZoneId() != null) {
                CommonDataInt data = new CommonDataInt();
                data.setAddr("0xA5AE");
                CommonDataInt.D d = new CommonDataInt.D();
                d.setId(reqVo.getGroupId());
                d.setValue(reqVo.getZoneId());
                data.setData(d);
                mqttPublisher.publish(deviceId, PublishKey.分区编号, data);
                short[] codes = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
                try {
                    short[] temp = key.getRemoteByArr(reqVo.getDeviceId(), "0xA5AE");
                    if (temp[0] != -1) codes = temp;
                    codes[reqVo.getGroupId() - 1] = reqVo.getZoneId();
                } catch (Exception e) {
                    log.error("Error reading remote array for updateLoop", e);
                }
                shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0xA5AE", codes);
            }
        }
        return R.ok("指令已下发");
    }

    @PostMapping("/groupControlLux")
    public R<?> groupControlLux1(@RequestBody WriteGroupReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return groupControlLux(reqVo);
    }

    public R<?> groupControlLux(@RequestBody WriteGroupReqVo reqVo) {
        if (ObjectUtils.isEmpty(reqVo) || Arrays.isNullOrEmpty(reqVo.getGroupSelectArr())) {
            return R.warn("输入数据无效");
        }
        LoopControlVO v = new LoopControlVO();
        v.setModule(2);
        v.setAddr("0xC046");
        LoopControlVO.D d = new LoopControlVO.D();
        d.setLux(reqVo.getLux());

        int[] groupSelectArr = new int[16];
        for (int i = 0; i < reqVo.getGroupSelectArr().length; i++) {
            groupSelectArr[i] = reqVo.getGroupSelectArr()[i];
        }

        d.setGroupArr(groupSelectArr);
        v.setData(d);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.分组控制总开关, v);

        intArrayRedisTemplate.opsForValue().set("zm:select:group:" + reqVo.getDeviceId(), groupSelectArr);

        return R.ok("操作成功");
    }

    @PostMapping("/groupControlSwitch")
    public R<?> groupControlSwitch1(@RequestBody WriteGroupReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return groupControlSwitch(reqVo);
    }

    public R<?> groupControlSwitch(@RequestBody WriteGroupReqVo reqVo) {
        if (ObjectUtils.isEmpty(reqVo) || Arrays.isNullOrEmpty(reqVo.getGroupSelectArr())) {
            return R.warn("输入数据无效");
        }
        LoopControlVO v = new LoopControlVO();
        v.setModule(1);
        v.setAddr("0xC046");
        LoopControlVO.D d = new LoopControlVO.D();
        d.setValue(reqVo.getSwitchStatus());

        int[] groupSelectArr = new int[16];
        for (int i = 0; i < reqVo.getGroupSelectArr().length; i++) {
            groupSelectArr[i] = reqVo.getGroupSelectArr()[i];
        }

        d.setGroupArr(groupSelectArr);
        v.setData(d);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.分组控制总开关, v);

        intArrayRedisTemplate.opsForValue().set("zm:select:group:" + reqVo.getDeviceId(), groupSelectArr);

        return R.ok("指令下发成功");
    }

}
