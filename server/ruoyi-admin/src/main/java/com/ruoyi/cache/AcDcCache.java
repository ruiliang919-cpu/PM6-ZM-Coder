package com.ruoyi.cache;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.web.websocket.DeviceStatusPushService;
import com.ruoyi.zm.domain.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AcDcCache {
    private final Key key;

    @Autowired(required = false)
    private DeviceStatusPushService deviceStatusPushService;

    // 交流信息-交流 1 路与 2 路（电源柜）
    public R<List<DevStatusAcVo>> getAlternating(Long slaveId) {
        try {
            int type = key.getTelecommand(Math.toIntExact(slaveId), 822);
            if (type == 0) {
                return R.ok();
            }
            // 交流输入路数
            Integer acInputLoop = key.getTelemeterByInt(Math.toIntExact(slaveId), "0x0028");
            // 交流电流显示
            Integer acCurrentDisplay = key.getTelemeterByInt(Math.toIntExact(slaveId), "0x0029");
            // 交流采样模式
            Integer acSamplingMode = key.getTelemeterByInt(Math.toIntExact(slaveId), "0x002A");

            // 交流一路Uab
            BigDecimal uab1 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x000E", 1);
            // 交流一路Ubc
            BigDecimal ubc1 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x000F", 1);
            // 交流一路Uca
            BigDecimal uca1 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0010", 1);
            // 交流二路Uab
            BigDecimal uab2 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0011", 1);
            // 交流二路Ubc
            BigDecimal ubc2 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0012", 1);
            // 交流二路Uca
            BigDecimal uca2 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0013", 1);
            // 交流一路Ia
            BigDecimal ia1 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0014", 1);
            // 交流一路Ib
            BigDecimal ib1 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0015", 1);
            // 交流一路Ic
            BigDecimal ic1 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0016", 1);
            // 交流二路Ia
            BigDecimal ia2 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0017", 1);
            // 交流二路Ib
            BigDecimal ib2 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0018", 1);
            // 交流二路Ic
            BigDecimal ic2 = key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0019", 1);
            // PM6US交流一路输入状态
            String workStatus1 = key.getTelecommand(Math.toIntExact(slaveId), 141) == 0 ? "备用" : "工作";
            // PM6US交流二路输入状态
            String workStatus2 = key.getTelecommand(Math.toIntExact(slaveId), 142) == 0 ? "备用" : "工作";

            // 交流电路列表
            List<DevStatusAcVo> result = new ArrayList<>();
            // 交流电路1
            DevStatusAcVo vo1 = new DevStatusAcVo();
            vo1.setId(1L);
            vo1.setCircuitNum(1L);
            vo1.setDeviceId(slaveId);
            vo1.setUac(uca1);
            vo1.setUab(uab1);
            vo1.setUbc(ubc1);
            vo1.setIa(ia1);
            vo1.setIb(ib1);
            vo1.setIc(ic1);
            vo1.setWorkStatus(workStatus1);
            // 交流电路2
            DevStatusAcVo vo2 = new DevStatusAcVo();
            vo2.setId(1L);
            vo2.setCircuitNum(2L);
            vo2.setDeviceId(slaveId);
            vo2.setUac(uca2);
            vo2.setUab(uab2);
            vo2.setUbc(ubc2);
            vo2.setIa(ia2);
            vo2.setIb(ib2);
            vo2.setIc(ic2);

            vo2.setWorkStatus(workStatus2);
            if (acInputLoop == 0) {
                vo2.setUac(BigDecimal.valueOf(-1));
                vo2.setUab(BigDecimal.valueOf(-1));
                vo2.setUbc(BigDecimal.valueOf(-1));
                vo2.setIa(BigDecimal.valueOf(-1));
                vo2.setIb(BigDecimal.valueOf(-1));
                vo2.setIc(BigDecimal.valueOf(-1));
            }
            if (acCurrentDisplay == 0) {
                vo1.setIa(BigDecimal.valueOf(-1));
                vo1.setIb(BigDecimal.valueOf(-1));
                vo1.setIc(BigDecimal.valueOf(-1));
                vo2.setIa(BigDecimal.valueOf(-1));
                vo2.setIb(BigDecimal.valueOf(-1));
                vo2.setIc(BigDecimal.valueOf(-1));
            }
            if (acSamplingMode == 1) {
                vo1.setUac(BigDecimal.valueOf(-1));
                vo1.setUbc(BigDecimal.valueOf(-1));
                vo2.setUac(BigDecimal.valueOf(-1));
                vo2.setUbc(BigDecimal.valueOf(-1));
                vo1.setIb(BigDecimal.valueOf(-1));
                vo1.setIc(BigDecimal.valueOf(-1));
                vo2.setIb(BigDecimal.valueOf(-1));
                vo2.setIc(BigDecimal.valueOf(-1));
            }
            result.add(vo1);
            result.add(vo2);
            return R.ok(result);
        } catch (Exception e) {
//            log.error("AcDcCache → getAlternating", e);
        }
        return R.ok();
    }

    // 绝缘信息-母线绝缘（电源柜）
    public R<BusInsulationRespVo> getBusInsulation(Long slaveId) {
        try {
            int type = key.getTelecommand(Math.toIntExact(slaveId), 822);
            if (type == 0) {
                return R.ok();
            }
            BusInsulationRespVo result = new BusInsulationRespVo();
            // 母线负极绝缘电阻值
            result.setNegativePoleResistance(key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x001F", 1));
            // 母线正极绝电阻值
            result.setPositivePoleResistance(key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x001E", 1));
            return R.ok(result);
        } catch (Exception e) {
            //            log.error("AcDcCache → getBusInsulation", e);
        }
        return R.ok();
    }

    // 直流信息-DC/DC信息（AC/DC信息）
    // 根据机柜类型判断，电源柜AC/DC 1，配电箱DC/DC 0
    public R<TreeMap<String, Object>> getDcAc(PageQuery pageQuery, Integer slaveId) {
        TreeMap<String, Object> result = new TreeMap<>();
        result.put("data", Collections.EMPTY_LIST);
        try {
            int type = key.getTelecommand(Math.toIntExact(slaveId), 822);
            result.put("cabinetType", type);
            if (type == 1) {
                // AC/DC
                List<DevFaultAcdcVo> source = new ArrayList<>();
                // 交流模块回路数,即整流模块数量
                Integer acModuleNum = key.getTelemeterByInt(slaveId, "0x0026");
                // 整流模块通讯故障数组
                int[] onlineTypeArr = key.getTelecommandByIntArr(slaveId, 15, 15 + 15);
                int[] onlineTypeArr2 = key.getTelecommandByIntArr(slaveId, 39, 39 + 4);
                onlineTypeArr = ArrayUtils.addAll(onlineTypeArr, onlineTypeArr2);
                // 整流模块故障数组
                int[] moduleTypeArr = key.getTelecommandByIntArr(slaveId, 87, 87 + 15);
                int[] moduleTypeArr2 = key.getTelecommandByIntArr(slaveId, 111, 111 + 4);
                moduleTypeArr = ArrayUtils.addAll(moduleTypeArr, moduleTypeArr2);
                // 整流模块开关机状态数组
                int[] switchStatusArr = key.getTelecommandByIntArr(slaveId, 173, 173 + 20);
                // 整流模块输出电压列表
                List<BigDecimal> voltageList = key.getTelemeterBigDecimalList(slaveId, "0x0400");
                // 整流模块输出电流列表
                List<BigDecimal> etlectricityList = key.getTelemeterBigDecimalList(slaveId, "0x0410");
                for (int i = 0; i < acModuleNum; i++) {
                    DevFaultAcdcVo vo = new DevFaultAcdcVo();
                    vo.setId((long) (i + 1));
                    vo.setDeviceId(slaveId);
                    vo.setNo(i + 1);
                    // 模块故障判定，即运行状态（0正常 1故障）
                    vo.setModuleType(moduleTypeArr[i]);
                    // 通讯模块故障判定，即通讯状态（0正常 1故障）
                    vo.setOnlineType(onlineTypeArr[i]);
                    // 整流模块输出电压
                    vo.setVoltage(voltageList.get(i).setScale(1, RoundingMode.HALF_UP));
                    // 整流模块输出电流
                    vo.setElectricity(etlectricityList.get(i).setScale(1, RoundingMode.HALF_UP));
                    // 整流模块开关机状态
                    vo.setSwitchStatus(switchStatusArr[i] == 0 ? 1 : 0);
                    source.add(vo);
                }
                result.put("data", key.getPageList(source, pageQuery));
                return R.ok(result);
            } else if (type == 0) {
                // DC/DC
                List<DevFaultDcdcVo> source = new ArrayList<>();
                // DC/DC模块数量
                int dcModuleNum = key.getTelemeterByInt(slaveId, "0x0027");
                // DC/DC模块故障数组
                int[] moduleTypeArr = key.getTelecommandByIntArr(slaveId, 103, 103 + dcModuleNum);
                // DC/DC通讯模块故障数组
                int[] onlineTypeArr = key.getTelecommandByIntArr(slaveId, 31, 31 + dcModuleNum);
                // DC/DC模块输出电压列表
                List<BigDecimal> voltageList = key.getTelemeterBigDecimalList(slaveId, "0x0420");
                // DC/DC模块输出电流列表
                List<BigDecimal> etlectricityList = key.getTelemeterBigDecimalList(slaveId, "0x0430");
                for (int i = 0; i < dcModuleNum; i++) {
                    DevFaultDcdcVo vo = new DevFaultDcdcVo();
                    vo.setId((long) (i + 1));
                    vo.setDeviceId(slaveId);
                    vo.setNo(i + 1);
                    vo.setModuleType(moduleTypeArr[i]);
                    vo.setOnlineType(onlineTypeArr[i]);
                    vo.setVoltage(voltageList.get(i).setScale(1, RoundingMode.HALF_UP));
                    vo.setElectricity(etlectricityList.get(i).setScale(1, RoundingMode.HALF_UP));
                    source.add(vo);
                }
                result.put("data", key.getPageList(source, pageQuery));
                return R.ok(result);
            }
        } catch (Exception e) {
//            log.error("AcDcCache → getDcAc", e);
        }
        return R.ok(result);
    }

    // 母线信息列表
    public R<BusRespVo> getBusInfo(Long slaveId) {
        try {
            int type = key.getTelecommand(Math.toIntExact(slaveId), 822);
            if (type == 0) {
                return R.ok();
            }
            BusRespVo result = new BusRespVo();
            // 母线电压
            result.setBusVoltage(key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x001A", 1));
            // 母线电流
            result.setBusElectricity(key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x001B", 1));
            // 母线正对地电压
            result.setPositivePoleResistance(key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x001C", 1));
            // 母线负对地电压
            result.setNegativePoleResistance(key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x001D", 1));
            // 环境温度
            result.setTemperature(key.getTelemeterByBigDecimalWithDigits(Math.toIntExact(slaveId), "0x0021", 1));
            return R.ok(result);
        } catch (Exception e) {
            //            log.error("AcDcCache → getBusInfo", e);
        }
        return null;
    }

    /**
     * D-3: 聚合推送设备机柜实时数据
     * 将设备的母线、交流、绝缘、模块、回路数据聚合后通过 WebSocket 推送
     */
    public void pushCabinetData(Integer slaveId) {
        if (deviceStatusPushService == null) return;
        try {
            Long sid = Long.valueOf(slaveId);
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceNo", slaveId);
            payload.put("timestamp", System.currentTimeMillis());

            Map<String, Object> data = new HashMap<>();

            // 母线信息
            R<BusRespVo> busInfo = getBusInfo(sid);
            if (busInfo != null && busInfo.getData() != null) {
                data.put("bus", busInfo.getData());
            }

            // 交流信息
            R<List<DevStatusAcVo>> alternating = getAlternating(sid);
            if (alternating != null && alternating.getData() != null) {
                data.put("alternating", alternating.getData());
            }

            // 绝缘信息
            R<BusInsulationRespVo> insulation = getBusInsulation(sid);
            if (insulation != null && insulation.getData() != null) {
                data.put("insulation", insulation.getData());
            }

            // 模块信息（DC/AC 或 DC/DC）
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageSize(100);
            R<TreeMap<String, Object>> dcAc = getDcAc(pageQuery, slaveId);
            if (dcAc != null && dcAc.getData() != null) {
                TreeMap<String, Object> modulesData = new TreeMap<>(dcAc.getData());
                Object listData = modulesData.get("data");
                if (listData instanceof List) {
                    TableDataInfo<Object> tableData = new TableDataInfo<>();
                    tableData.setRows((List<Object>) listData);
                    tableData.setTotal(((List<?>) listData).size());
                    modulesData.put("data", tableData);
                }
                data.put("modules", modulesData);
            }

            // 直流回路
            TableDataInfo<DevStatusDccLoopVo> dccList = getDccList(pageQuery, sid);
            if (dccList != null && dccList.getRows() != null) {
                data.put("dccList", dccList);
            }

            // 交流回路
            TableDataInfo<DevStatusAcLoopVo> acList = getAcList(sid);
            if (acList != null && acList.getRows() != null) {
                data.put("acList", acList);
            }

            payload.put("data", data);
            deviceStatusPushService.pushCabinetData(slaveId, payload);
        } catch (Exception e) {
            log.error("AcDcCache推送机柜数据失败, slaveId={}", slaveId, e);
        }
    }

    // 直流回路信息列表
    public TableDataInfo<DevStatusDccLoopVo> getDccList(PageQuery pageQuery, Long slaveId) {
        try {
            int type = key.getTelecommand(Math.toIntExact(slaveId), 822);
            if (type == 1) return TableDataInfo.build();
            List<DevStatusDccLoopVo> source = new ArrayList<>();
            // 直流调光回路数量
            int dimmerNum = key.getTelemeterByInt(Math.toIntExact(slaveId), "0x0022");
            // 直流开关回路数量
            int dcSwitchNum = key.getTelemeterByInt(Math.toIntExact(slaveId), "0x0023");
            // 亮度设定列表
            List<BigDecimal> brightnessSettingList = key.getTelemeterBigDecimalList(Math.toIntExact(slaveId), "0X04A0");
            // 亮度反馈列表
            List<BigDecimal> brightnessFeedbackList = key.getTelemeterBigDecimalList(Math.toIntExact(slaveId), "0X04C8");
            // 输出电压列表
            List<BigDecimal> outputVoltageList = key.getTelemeterBigDecimalList(Math.toIntExact(slaveId), "0X04F0");
            // 输出电流列表
            List<BigDecimal> outputCurrentList = key.getTelemeterBigDecimalList(Math.toIntExact(slaveId), "0X0518");
            // 内部温度列表
            List<BigDecimal> internalTemperatureList = key.getTelemeterBigDecimalList(Math.toIntExact(slaveId), "0X0540");
            // 开关设定数组
            int[] switchSettingArr = key.getTelecommandByIntArr(Math.toIntExact(slaveId), 434, 434 + dimmerNum + dcSwitchNum);
            // 开关反馈数组
            int[] switchFeedbackArr = key.getTelecommandByIntArr(Math.toIntExact(slaveId), 474, 474 + dimmerNum + dcSwitchNum);
            // 实时功率列表
            List<BigDecimal> realTimeList = key.getTelemeterBigDecimalList(Math.toIntExact(slaveId), "0X007B");
            for (int i = 0; i < dimmerNum + dcSwitchNum; i++) {
                DevStatusDccLoopVo vo = new DevStatusDccLoopVo();
                vo.setId((long) (i + 1));
                // 调光回路
                if (i < dimmerNum) {
                    // 亮度设定
                    vo.setBrightnessSetting(brightnessSettingList.get(i).longValue());
                    // 亮度反馈
                    vo.setBrightnessFeedback(brightnessFeedbackList.get(i).longValue());
                    // 输出电压
                    vo.setOutputVoltage(outputVoltageList.get(i).setScale(1, RoundingMode.HALF_UP));
                    // 输出电流
                    vo.setOutputCurrent(outputCurrentList.get(i).setScale(1, RoundingMode.HALF_UP));
                    // 内部温度
                    vo.setInternalTemperature(internalTemperatureList.get(i).setScale(1, RoundingMode.HALF_UP));
                    // 开关设定
                    vo.setSwitchSetting(switchSettingArr[i]);
                    // 开关反馈
                    vo.setSwitchFeedback(switchFeedbackArr[i]);
                    // 实时功率
                    vo.setRealTime(realTimeList.get(i).setScale(2, RoundingMode.HALF_UP));
                }
                // 开关回路
                else {
                    // 开关设定
                    vo.setSwitchSetting(switchSettingArr[i]);
                    // 开关反馈
                    vo.setSwitchFeedback(switchFeedbackArr[i]);
                }
                source.add(vo);
            }
            return key.getPageTable(source, pageQuery);
        } catch (Exception e) {
            // log.error("AcDcCache → getDccList", e);
        }
        return TableDataInfo.build();
    }

    // 交流回路信息列表
    public TableDataInfo<DevStatusAcLoopVo> getAcList(Long slaveId) {
        try {
            int type = key.getTelecommand(Math.toIntExact(slaveId), 822);
            if (type == 1) {
                return TableDataInfo.build();
            }
            int acLoopNum = 0;
            Object remote = key.getRemote(Math.toIntExact(slaveId), "0XAF1Enum");
            if (remote != null) acLoopNum = Integer.parseInt(remote.toString());
            int[] switchSettingArr = key.getTelecommandByIntArr(Math.toIntExact(slaveId), 514, 521);
            int[] switchFeedbackArr = key.getTelecommandByIntArr(Math.toIntExact(slaveId), 522, 529);
            List<DevStatusAcLoopVo> source = new ArrayList<>();
            for (int i = 0; i < acLoopNum; i++) {
                DevStatusAcLoopVo vo = new DevStatusAcLoopVo();
                vo.setId((long) (i + 1));
                vo.setDeviceId(slaveId);
                vo.setNo(String.valueOf(i + 1));
                vo.setSwitchSetting(switchSettingArr[i]);
                vo.setSwitchFeedback(switchFeedbackArr[i]);
                source.add(vo);
            }
            return key.getPageTable(source, 1, 100);
        } catch (Exception e) {
            // log.error("AcDcCache → getAcList", e);
        }
        return TableDataInfo.build();
    }
}
