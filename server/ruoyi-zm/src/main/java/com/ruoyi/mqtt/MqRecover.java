//package com.ruoyi.mqtt;
//
//import com.ruoyi.cache.Key;
//import com.ruoyi.cache.SceneCache;
//import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
//import com.ruoyi.mqtt03.addr03.handler.addr0xA483.Addr0xA483;
//import com.ruoyi.mqttwrite.inf.InfValue;
//import com.ruoyi.schedule.util.InstructAddrUtil;
//import com.ruoyi.web.controller.zm.WriteController;
//import com.ruoyi.zm.domain.*;
//import com.ruoyi.zm.domain.vo.*;
//import com.ruoyi.zm.utils.ScaleUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//import static com.ruoyi.cache.Key.REMOTE_KEY;
//import static com.ruoyi.mqtt03.addr03.handler.addr0XA80A.Addr0XA80AHandler.addr;
//import static com.ruoyi.mqtt03.addr03.handler.addr0xA483.Addr0xA483Handler.writeKey;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class MqRecover {
//    private final WriteController w;
//    private final Key k;
//    private final InstructAddrUtil instructAddrUtil;
//    private final SceneCache sceneCache;
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public void recover(int no, DevBaseDeviceTCPVo tcp) {
//        try {
//            w.timeSaveNow(no);
//            short[] codes = k.getRemoteByArr(no, "0xA5AE");
//            List<String> groupNames = (List<String>) k.getTelemeter(no, "0xA1F0");
//            List<String> sceneNames = sceneCache.getSceneNamesList(no);
//            IntStream.range(0, 16).forEach(i -> {
//                try {
//                    UpdateLoopReqVo u = new UpdateLoopReqVo();
//                    u.setDeviceId(no);
//                    if (codes[0] != -1) u.setZoneId(codes[i]);
//                    int groupId = i + 1;
//                    u.setGroupId(groupId);
//                    String addr = instructAddrUtil.AddrByName(InstructAddrUtil.MAP03, "系统设置 -回路分组-分组回路编号" + groupId + "_01");
//                    String sourceLoopStr = (String) k.getTelemeter(no, addr);
//                    if (sourceLoopStr != null && !sourceLoopStr.isEmpty())
//                        u.setLoopNo(ScaleUtil.splitIntoIntPairs(sourceLoopStr));
//                    w.updateLoop(u);
//                    GroupNameVo g = new GroupNameVo();
//                    g.setDeviceId(no);
//                    if (codes[0] != -1) g.setId(codes[i]);
//                    g.setName(groupNames.get(i));
//                    g.setGroupId(groupId);
//                    w.groupName(g);
//                } catch (Exception ignored) {
//
//                }
//            });
//            IntStream.range(0, 10).forEach(i -> {
//                int index = i + 1;
//                w.sceneName(no, index, sceneNames.get(i));
//            });
//            List<Addr0xA483.Data> addr0xA483Data = (List<Addr0xA483.Data>) redisTemplate.opsForValue().get(writeKey + no);
//            if (addr0xA483Data != null && !addr0xA483Data.isEmpty()) {
//                addr0xA483Data.forEach(d -> {
//                    SceneParamsReqVo s = new SceneParamsReqVo();
//                    s.setDeviceId(no);
//                    s.setSceneId(d.getSceneId());
//                    s.setName(sceneNames.get(d.getSceneId()));
//                    s.setSelectGroupIdArr(d.getGroupIds());
//                    s.setSelectLuxArr(d.getLuxArr());
//                    s.setSelectSwitchArr(d.getSwitchIntArr());
//                    w.updateSceneParams(s);
//                });
//            }
//
//            IntStream.rangeClosed(1, 3).forEach(i -> {
//                HashMap<String, Object> simple = (HashMap<String, Object>) k.getRemote(no, addr[i]);
//                if (simple != null && !simple.isEmpty()) {
//                    SimpleControlReqVo s = new SimpleControlReqVo();
//                    s.setControlId(i);
//                    s.setDeviceId(no);
//                    s.setEnabled((Boolean) simple.getOrDefault("data3", false));
//                    s.setGroupIds(null);
//                    List<DevConfigSimpleGroup> data2 = (List<DevConfigSimpleGroup>) simple.getOrDefault("data2", null);
//                    if (data2 != null && !data2.isEmpty())
//                        s.setGroupIds(data2.stream().filter(d -> d.getSelectStatus() == 1)
//                            .map(DevConfigSimpleGroup::getGroupId).toArray(Integer[]::new));
//                    List<DevConfigTimeControl> data1 = (List<DevConfigTimeControl>) simple.getOrDefault("data1", null);
//                    s.setTable(null);
//                    if (data1 != null && !data1.isEmpty())
//                        s.setTable(data1.stream().map(d -> new SimpleControlTable() {{
//                            setControlId(d.getTimeControlId());
//                            setEnabledStatus(d.getEnabledStatus());
//                            setFrameId(d.getTimeFrameId());
//                            setEtime(d.getEtime());
//                            setStime(d.getStime());
//                            setSwitchStatus(d.getSwitchStatus());
//                            setLux(d.getLux());
//                        }}).collect(Collectors.toList()));
//                    w.updateSimpleControl(s);
//                }
//
//                SceneControlReqVo s = new SceneControlReqVo();
//                s.setControlId(i);
//                s.setDeviceId(no);
//                boolean sEnabled = false;
//                Map<String, Integer> sEnabledM = (HashMap<String, Integer>) redisTemplate.opsForValue().get(REMOTE_KEY + tcp.getIp() + ":" + no + ":" + addr[i] + "Enabled");
//                if (sEnabledM != null && !sEnabledM.isEmpty()) sEnabled = sEnabledM.get("data3") != null && sEnabledM.get("data3").equals(i);
//                s.setEnabled(sEnabled);
//                List<DevConfigTimeControlScene> sceneData = (List<DevConfigTimeControlScene>) redisTemplate.opsForValue().get(REMOTE_KEY + tcp.getIp() + ":" + no + ":" + addr[i]);
//                s.setTable(null);
//                if (sceneData != null && !sceneData.isEmpty()) {
//                    s.setTable(sceneData.stream().map(d -> new SceneControlTable() {{
//                        setTimeFrameId(d.getTimeFrameId());
//                        setEnabledStatus(d.getEnabledStatus());
//                        setEtime(d.getEtime());
//                        setStime(d.getStime());
//                        setSceneSelect(d.getSceneSelect());
//                    }}).collect(Collectors.toList()));
//                    w.updateSceneControl(s);
//                }
//            });
//
//            Map<String, Object> hwMap = (Map<String, Object>) k.getRemote(no, "0XAE7A");
//            if (hwMap != null && !hwMap.isEmpty()) {
//                List<DevConfigInfraredSensor> data = (List<DevConfigInfraredSensor>) hwMap.getOrDefault("data", null);
//                if (data != null && !data.isEmpty()) {
//                    IntStream.rangeClosed(1, 3).forEach(i -> {
//                        DevConfigInfraredSensor d = data.get(i - 1);
//                        InfraredParamsReqVo r = new InfraredParamsReqVo();
//                        r.setDeviceId(no);
//                        r.setEnabled(((Integer) hwMap.getOrDefault("select", -1)) == i);
//                        r.setSensorId(d.getSensorId());
//                        Integer[] orDefault = (Integer[]) hwMap.getOrDefault("data" + i, new Integer[16]);
//                        r.setGroupIds(InfValue.selectArr(orDefault));
//                        InfraredParamsTable t = new InfraredParamsTable();
//                        t.setSensorId(d.getSensorId());
//                        t.setDelayedTime(d.getDelayedTime());
//                        t.setEnabled(d.getEnabled());
//                        t.setInductiveLux(d.getInductiveLux());
//                        t.setInductiveSwitchStatus(d.getInductiveSwitchStatus());
//                        t.setUninductionLux(d.getUninductionLux());
//                        t.setUninductionSwitchStatus(d.getUninductionSwitchStatus());
//                        r.setTable(t);
//                        w.updateInfraredParams(r);
//                    });
//                }
//            }
//            Map<String, Object> zdMap = (Map<String, Object>) k.getRemote(no, "0XAE8F");
//            if (zdMap != null && !zdMap.isEmpty()) {
//                List<DevConfigIlluminanceSensor> data = (List<DevConfigIlluminanceSensor>) zdMap.getOrDefault("data", null);
//                if (data != null && !data.isEmpty()) {
//                    IntStream.rangeClosed(1, 5).forEach(i -> {
//                        DevConfigIlluminanceSensor d = data.get(i - 1);
//                        IlluminanceParamsReqVo p = new IlluminanceParamsReqVo();
//                        p.setDeviceId(no);
//                        p.setEnabled(((Integer) zdMap.getOrDefault("select", -1)) == i);
//                        Integer[] orDefault = (Integer[]) zdMap.getOrDefault("data" + i, new Integer[16]);
//                        p.setGroupIds(InfValue.selectArr(orDefault));
//                        p.setSensorId(d.getSensorId());
//                        IlluminanceParamsTable ill = new IlluminanceParamsTable();
//                        ill.setSensorId(d.getSensorId());
//                        ill.setConstantIlluminanceLux(d.getConstantIlluminanceLux());
//                        ill.setHysteresisLux(d.getHysteresisLux());
//                        ill.setAdjustmentTimeSeconds(d.getAdjustmentTimeSeconds());
//                        ill.setEnabled(d.getEnabled());
//                        ill.setOutControlStatus(d.getOutControlStatus());
//                        p.setTable(ill);
//                        w.updateIlluminanceParams(p);
//                    });
//                }
//            }
//
//            w.moduleSetting(no, (Addr0XB715.Data) k.getRemote(no, "0XB715"));
//
//            Map<String, Object> jlMap = (Map<String, Object>) k.getRemote(no, "0XAF1E");
//            if (jlMap != null && !jlMap.isEmpty()) {
//                List<DevConfigTimeControlAc> l = (List<DevConfigTimeControlAc>) jlMap.get("controlAc");
//                if (l != null && !l.isEmpty()) {
//                    w.updateAcControl(l.stream().map(d -> new TimeControlAcRespVo() {{
//                        setDeviceId(no);
//                        setNo(d.getFrameId());
//                        setEnabled(d.getEnable());
//                        setStime((d.getStimeHour() < 10 ? "0" + d.getStimeHour() : "" + d.getStimeHour()) + (d.getStimeMin() < 10 ? "0" + d.getStimeMin() : "" + d.getStimeMin()));
//                        setEtime((d.getEtimeHour() < 10 ? "0" + d.getEtimeHour() : "" + d.getEtimeHour()) + (d.getEtimeMin() < 10 ? "0" + d.getEtimeMin() : "" + d.getEtimeMin()));
//                        setStatus(d.getSwitchStatus());
//                    }}).collect(Collectors.toList()));
//                }
//            }
//        } catch (Exception e) {
//            log.error("", e);
//        }
//    }
//}
