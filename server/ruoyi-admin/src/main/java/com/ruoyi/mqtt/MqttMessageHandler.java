package com.ruoyi.mqtt;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt.vo.Heart;
import com.ruoyi.mqtt.vo.MqBody;
import com.ruoyi.mqtt.vo.TelecommandBody;
import com.ruoyi.mqtt03.MainChannel;
import com.ruoyi.schedule.TelecommandSendSchedule;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.config.ModbusTCPManager;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevFaultRecord;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.DevFaultRecordVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevFaultRecordMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttMessageHandler {
    private final MainChannel mainChannel;
    private final DListUtil dListUtil;
    private final ModbusTCPManager tcpManager;
    private final DevFaultRecordMapper faultRecordMapper;
    private final DevBaseDeviceMapper devBaseDeviceMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TelecommandSendSchedule telecommandSendSchedule;
    private final RedisTemplate<String, boolean[]> boolArrayRedisTemplate;
    private final Key key;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMqttMessage(Message<?> message) {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        // log.info("{}-{}", topic, message.getPayload().toString());
        if (topic == null || topic.isEmpty()) return;
        String[] parts = topic.split("/");

        Long deviceNo = dListUtil.ipMapX().getOrDefault(parts[2], -1L);
        if (deviceNo == -1) {
            log.warn("主题：{} 判断设备号 -1", topic);
            return;
        }

        MqBody event = new MqBody();
        event.setDeviceNo(Math.toIntExact(deviceNo));
        event.setBody(message.getPayload().toString());
        event.setEnd(parts[parts.length - 1]);
        handleEvent(event);
    }

    private void handleEvent(MqBody event) {
        switch (event.getEnd()) {
            case "holding":
                mainChannel.handleHoldingRegister(event.getDeviceNo(), event.getBody());
                break;
            case "coil":
                handleCoilStatus(event.getDeviceNo(), event.getBody());
                break;
            case "heart":
                handleHeartbeat(event.getDeviceNo(), event.getBody());
                break;
            case "connect":
                yz(event.getDeviceNo(), event.getBody());
                break;
            case "request":
                mainChannel.handleRequest(event.getDeviceNo(), event.getBody());
                break;
        }
    }

    private void handleHeartbeat(Integer deviceNo, String payload) {
        Heart.Data bean = JSONUtil.toBean(payload, Heart.class).getData();
        Boolean online = bean.getOnline();
        String version = bean.getVersion();
        DevBaseDeviceTCPVo tcp = tcpManager.getOneByCache(deviceNo);
        Boolean persist = redisTemplate.hasKey("zm:create-tcp:" + deviceNo);
        if (!persist || (online && 0 == tcp.getOnlineStatus())) {
            tcp.setVersion(version);
            tcp.setOnlineStatus(1);
            tcp.setLastTime(new Date());
            redisTemplate.opsForValue().set("zm:create-tcp:" + deviceNo, tcp, 2, TimeUnit.DAYS);
            redisTemplate.delete("zm:order:" + deviceNo + ":1:0x0000");
            redisTemplate.delete("zm:fault:" + deviceNo + ":0xAAAA");
        } else if (tcp.getVersion() == null || tcp.getVersion().isEmpty()) {
            tcp.setVersion(version);
            tcp.setLastTime(new Date());
            redisTemplate.opsForValue().set("zm:create-tcp:" + deviceNo, tcp, 2, TimeUnit.DAYS);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static
    class Yz {
        private Boolean connected;
    }

    private void yz(Integer deviceNo, String payload) {
        Boolean connected = JSONUtil.toBean(payload, Yz.class).getConnected();
        if (connected == null || connected) return;
        DevBaseDeviceTCPVo tcp = tcpManager.getOneByCache(deviceNo);
        String ip = tcp.getIp();
        boolean[] initData = new boolean[845];
        boolean[] sourceData = boolArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:1:" + ip + ":" + deviceNo + ":0x0000");
        if (sourceData != null) {
            sourceData[sourceData.length - 1] = !sourceData[sourceData.length - 1];
            initData = sourceData;
        }
        boolArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:1:" + ip + ":" + deviceNo + ":0x0000", initData);
        redisTemplate.delete("zm:order:" + deviceNo + ":1:0x0000");
        if (tcp.getOnlineStatus() == 1) {
            tcp.setOnlineStatus(0); // 断开
            redisTemplate.opsForValue().set("zm:create-tcp:" + deviceNo, tcp, 1, TimeUnit.DAYS);
            // 清除断线设备的告警信息
            Set<String> keys = redisTemplate.keys("zm:fault:" + deviceNo + ":*");
            redisTemplate.delete(keys);
            boolArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:1:" + ip + ":" + deviceNo + ":0x0000", initData);
            DevFaultRecordVo vo = new DevFaultRecordVo();
            vo.setDeviceId(Math.toIntExact(deviceNo));
            vo.setName(devBaseDeviceMapper.selectOne(new LambdaQueryWrapper<DevBaseDevice>()
                .select(DevBaseDevice::getDeviceName)
                .eq(DevBaseDevice::getDeviceNo, deviceNo)
            ).getDeviceName());
            vo.setMessage("设备离线");
            vo.setStime(System.currentTimeMillis());
            redisTemplate.opsForValue().set("zm:fault:" + deviceNo + ":0xAAAA", vo);
            DevFaultRecord recordFault = new DevFaultRecord();
            recordFault.setDeviceId(Math.toIntExact(deviceNo));
            recordFault.setMessage(vo.getMessage());
            recordFault.setStime(vo.getStime() / 1000);
            recordFault.setShowType(1);
            recordFault.setType(1);
            faultRecordMapper.insert(recordFault);
        }
    }

    private void handleCoilStatus(Integer deviceNo, String payload) {
        try {
            TelecommandBody bean = JSONUtil.toBean(payload, TelecommandBody.class);
            String ip = key.getCreateTCP(deviceNo).getIp();
            telecommandSendSchedule.U(ip, deviceNo, bean.getData());
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static void main(String[] args) {
        Map<String, Object> m = new HashMap<>();
        m.put("ip", 99);
        boolean[] data = new boolean[845];
        Arrays.fill(data, true);
        m.put("data", data);
        System.out.println(JSONUtil.toJsonStr(m));
    }
}
