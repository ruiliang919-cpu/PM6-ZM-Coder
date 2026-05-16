package com.ruoyi.send;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.mapper.*;
import com.ruoyi.zm.utils.IdGenerator;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

// 更新功能码03 数据到数据库中的业务层
@Slf4j
@Service
@RequiredArgsConstructor
public class Update03DataService {
    private final DevStatusAcMapper acMapper;
    private final DevBaseDeviceMapper deviceMapper;
    private final DevStatusDccLoopMapper dccLoopMapper;
    private final DevConfigGroupMapper groupMapper;
    private final DevGroupLoopMapper groupLoopMapper;
    private final DevBaseBranchMapper bashBranchMapper;
    private final DevFaultAcdcMapper faultAcdcMapper;
    private final DevFaultDcdcMapper faultDcdcMapper;
    private final DevConfigTimeMapper configTimeMapper;
    private final DevConfigSceneMapper sceneMapper;
    private final DevConfigDistrictMapper districtMapper;
    private final DevConfigTimeControlMapper timeControlMapper;
    private final DevConfigTimeControlSceneMapper controlSceneMapper;
    private final DevConfigInfraredSensorMapper infraredSensorMapper;
    private final DevDeviceRemoteReadMapper remoteReadMapper;
    private final DevConfigTimeControlAcMapper controlAcMapper;
    private final DevBasePowerMapper powerMapper;
    private final DevConfigIlluminanceSensorMapper configIlluminanceSensorMapper;
    private final DevConfigSimpleGroupMapper simpleGroupMapper;
    private final DevInfraredGroupMapper infraredGroupMapper;
    private final DevIlluminanceGroupMapper illuminanceGroupMapper;
    private final DevBaseSceneControlMapper sceneControlMapper;
    private final DevBaseSimpleControlMapper simpleControlMapper;
    private final DevEnergyMeterDayMapper energyMeterDayMapper;
    private final DevEnergyMeterWeekMapper energyMeterWeekMapper;
    private final DevEnergyMeterMonthMapper energyMeterMonthMapper;
    private final DevEnergyMeterQuarterMapper energyMeterQuarterMapper;
    private final DevEnergyMeterYearMapper energyMeterYearMapper;

    public void updateData(DevInstruct instruct, Object data) {
        try {
            switch (instruct.getAddr()) {
                // ------------------------------------------------遥测的读取协议------------------------------------------------
                case "0x0000":
                    // 总功率 每天统计一次
                    // DevBaseLoss loss = new DevBaseLoss();
                    // loss.setId(instruct.getSalveId());
                    // loss.setDeviceId(instruct.getSalveId());
                    // loss.setLoss(((ArrayList<BigDecimal>) data).get(1));
                    // lossMapper.insertOrUpdate(loss);
                    Fun0x0000(instruct, data);
                    break;
                // case "0X002B":
                //     // 1#电能表总电量
                //     List<BigDecimal> totalData = (List<BigDecimal>) data;
                //     // log.error("~~~~~~~~~~~0X002B~~~~~~~~~~~" + totalData);
                //     long currentTimeMillis0 = System.currentTimeMillis() / 1000;
                //     List<DevEnergyMeterTotal> meterTotals = new ArrayList<>();
                //     int id = 1;
                //     for (BigDecimal totalDatum : totalData) {
                //         DevEnergyMeterTotal total = new DevEnergyMeterTotal();
                //         total.setId(IdGenerator.UUIDId());
                //         total.setDeviceNo(instruct.getSalveId());
                //         total.setEnergyMeterNo(id++);
                //         total.setPower(totalDatum);
                //         total.setTimestamp(currentTimeMillis0);
                //         meterTotals.add(total);
                //     }
                //     energyMeterTotalMapper.insertOrUpdateBatch(meterTotals);
                //     break;
                // case "0X007B":
                //     // 直流回路实时功率
                //     try {
                //         ArrayList<DevStatusDccLoop> result3 = new ArrayList<>();
                //         for (int i = 0; i < ((ArrayList<BigDecimal>) data).size(); i++) {
                //             DevStatusDccLoop loop6 = new DevStatusDccLoop();
                //             loop6.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                //             loop6.setDeviceId((long) instruct.getSalveId());
                //             loop6.setNo(String.valueOf(i + 1));
                //             loop6.setRealTime(((ArrayList<BigDecimal>) data).get(i));
                //             result3.add(loop6);
                //         }
                //         dccLoopMapper.insertOrUpdateBatch(result3);
                //
                //         // 电能表即直流回路，电能表总功率即直流回路实时功率
                //         List<BigDecimal> powerData = (List<BigDecimal>) data;
                //         // log.error("电能表总功率~~~~" + powerData);
                //         List<DevEnergyMeterPower> powers = new ArrayList<>();
                //         int id3 = 1;
                //         int id4 = 1;
                //         for (int index = 0; index < powerData.size(); index++) {
                //             DevEnergyMeterPower meterPower = new DevEnergyMeterPower();
                //             meterPower.setId(Integer.valueOf("" + (id3++) + instruct.getSalveId()));
                //             meterPower.setDeviceNo(instruct.getSalveId());
                //             meterPower.setEnergyMeterNo(id4++);
                //             meterPower.setPower(powerData.get(index));
                //             powers.add(meterPower);
                //         }
                //         energyMeterPowerMapper.insertOrUpdateBatch(powers);
                //     } catch (Exception e) {
                //         log.error("~~~~~~~0X007B~~~~chucuo出错~~~~~~~~~" + e);
                //     }
                //     break;
                case "0X00CB":
                    Fun0X00CB(instruct, data);
                    break;
                case "0X011B":
                    Fun0X011B(instruct, data);
                    break;
                case "0X016B":
                    Fun0X016B(instruct, data);
                    break;
                case "0X01BB":
                    Fun0X01BB(instruct, data);
                    break;
                case "0X020B":
                    Fun0X020B(instruct, data);
                    break;
                case "0x000E":
                    DevStatusAc uab1 = new DevStatusAc();
                    uab1.setId(Long.valueOf((long) instruct.getSalveId() + "" + 1L));
                    uab1.setDeviceId((long) instruct.getSalveId());
                    uab1.setCircuitNum(1L);
                    uab1.setUab(((BigDecimal) data));
                    acMapper.insertOrUpdate(uab1);
                    break;
                case "0x000F":
                    DevStatusAc ubc1 = new DevStatusAc();
                    ubc1.setId(Long.valueOf((long) instruct.getSalveId() + "" + 1L));
                    ubc1.setDeviceId((long) instruct.getSalveId());
                    ubc1.setCircuitNum(1L);
                    ubc1.setUbc(((BigDecimal) data));
                    acMapper.insertOrUpdate(ubc1);
                    break;
                case "0x0010":
                    DevStatusAc uca1 = new DevStatusAc();
                    uca1.setId(Long.valueOf((long) instruct.getSalveId() + "" + 1L));
                    uca1.setDeviceId((long) instruct.getSalveId());
                    uca1.setCircuitNum(1L);
                    uca1.setUac(((BigDecimal) data));
                    acMapper.insertOrUpdate(uca1);
                    break;
                case "0x0011":
                    DevStatusAc uab2 = new DevStatusAc();
                    uab2.setId(Long.valueOf((long) instruct.getSalveId() + "" + 2L));
                    uab2.setDeviceId((long) instruct.getSalveId());
                    uab2.setCircuitNum(2L);
                    uab2.setUab(((BigDecimal) data));
                    acMapper.insertOrUpdate(uab2);
                    break;
                case "0x0012":
                    DevStatusAc ubc2 = new DevStatusAc();
                    ubc2.setId(Long.valueOf((long) instruct.getSalveId() + "" + 2L));
                    ubc2.setDeviceId((long) instruct.getSalveId());
                    ubc2.setCircuitNum(2L);
                    ubc2.setUbc(((BigDecimal) data));
                    acMapper.insertOrUpdate(ubc2);
                    break;
                case "0x0013":
                    DevStatusAc uca2 = new DevStatusAc();
                    uca2.setId(Long.valueOf((long) instruct.getSalveId() + "" + 2L));
                    uca2.setDeviceId((long) instruct.getSalveId());
                    uca2.setCircuitNum(2L);
                    uca2.setUac(((BigDecimal) data));
                    acMapper.insertOrUpdate(uca2);
                    break;


                case "0x0014":
                    // // // System.out.println("~~~~~~~~~~~~~~~~~~0x0014~~~~~~~~~~~~~~~~~~~：" + data);
                    DevStatusAc ia1 = new DevStatusAc();
                    ia1.setId(Long.valueOf((long) instruct.getSalveId() + "" + 1L));
                    ia1.setDeviceId((long) instruct.getSalveId());
                    ia1.setCircuitNum(1L);
                    ia1.setIa(((BigDecimal) data));
                    acMapper.insertOrUpdate(ia1);
                    break;
                case "0x0015":
                    DevStatusAc ib1 = new DevStatusAc();
                    ib1.setId(Long.valueOf((long) instruct.getSalveId() + "" + 1L));
                    ib1.setDeviceId((long) instruct.getSalveId());
                    ib1.setCircuitNum(1L);
                    ib1.setIb(((BigDecimal) data));
                    acMapper.insertOrUpdate(ib1);
                    break;
                case "0x0016":
                    DevStatusAc ic1 = new DevStatusAc();
                    ic1.setId(Long.valueOf((long) instruct.getSalveId() + "" + 1L));
                    ic1.setDeviceId((long) instruct.getSalveId());
                    ic1.setCircuitNum(1L);
                    ic1.setIc(((BigDecimal) data));
                    acMapper.insertOrUpdate(ic1);
                    break;
                case "0x0017":
                    DevStatusAc ia2 = new DevStatusAc();
                    ia2.setId(Long.valueOf((long) instruct.getSalveId() + "" + 2L));
                    ia2.setDeviceId((long) instruct.getSalveId());
                    ia2.setCircuitNum(2L);
                    ia2.setIa(((BigDecimal) data));
                    acMapper.insertOrUpdate(ia2);
                    break;
                case "0x0018":
                    // // // System.out.println("~~~~~~~~~~~~~~~~~~0x0018~~~~~~~~~~~~~~~~~~~：" + data);
                    DevStatusAc ib2 = new DevStatusAc();
                    ib2.setId(Long.valueOf((long) instruct.getSalveId() + "" + 2L));
                    ib2.setDeviceId((long) instruct.getSalveId());
                    ib2.setCircuitNum(2L);
                    ib2.setIb(((BigDecimal) data));
                    acMapper.insertOrUpdate(ib2);
                    break;
                case "0x0019":
                    DevStatusAc ic2 = new DevStatusAc();
                    ic2.setId(Long.valueOf((long) instruct.getSalveId() + "" + 2L));
                    ic2.setDeviceId((long) instruct.getSalveId());
                    ic2.setCircuitNum(2L);
                    ic2.setIc(((BigDecimal) data));
                    acMapper.insertOrUpdate(ic2);
                    break;


                case "0x001A":
                    DevBaseDevice device1 = new DevBaseDevice();
                    device1.setId((long) instruct.getSalveId());
                    device1.setDeviceNo((long) instruct.getSalveId());
                    device1.setDcBusVoltage((BigDecimal) data);
                    // // // System.out.println("~~~~~~~~~~~~0x001A~~~~~~~~~device1~~~~~~~~~~~~~~~~~~~" + device1);
                    deviceMapper.updateById(device1);
                    break;

                case "0x001B":
                    DevBaseDevice device2 = new DevBaseDevice();
                    device2.setId((long) instruct.getSalveId());
                    device2.setDeviceNo((long) instruct.getSalveId());
                    device2.setDcBusCurrent((BigDecimal) data);
                    // log.error("~~~~~~~~~~~~0x001B~~~~~~~~~device2~~~~~~~~~~~~~~~~~~~" + device2);
                    deviceMapper.updateById(device2);
                    break;

                case "0x001C":
                    DevBaseDevice device3 = new DevBaseDevice();
                    device3.setId((long) instruct.getSalveId());
                    device3.setDeviceNo((long) instruct.getSalveId());
                    device3.setBusDirectVoltageToEarth((BigDecimal) data);
                    deviceMapper.updateById(device3);
                    break;

                case "0x001D":
                    DevBaseDevice device4 = new DevBaseDevice();
                    device4.setId((long) instruct.getSalveId());
                    device4.setDeviceNo((long) instruct.getSalveId());
                    device4.setBusNegativeVoltageToEarth((BigDecimal) data);
                    deviceMapper.updateById(device4);
                    break;

                case "0x001E":
                    DevBaseDevice device5 = new DevBaseDevice();
                    device5.setId((long) instruct.getSalveId());
                    device5.setDeviceNo((long) instruct.getSalveId());
                    device5.setPositivePoleResistance((BigDecimal) data);
                    deviceMapper.updateById(device5);
                    break;

                case "0x001F":
                    DevBaseDevice device6 = new DevBaseDevice();
                    device6.setId((long) instruct.getSalveId());
                    device6.setDeviceNo((long) instruct.getSalveId());
                    device6.setNegativePoleResistance((BigDecimal) data);
                    deviceMapper.updateById(device6);
                    break;

                case "0x0020":
                    DevBaseDevice device7 = new DevBaseDevice();
                    device7.setId((long) instruct.getSalveId());
                    device7.setDeviceNo((long) instruct.getSalveId());
                    device7.setBusbarCrossoverVoltage((BigDecimal) data);
                    deviceMapper.updateById(device7);
                    break;

                case "0x0021":
                    DevBaseDevice device8 = new DevBaseDevice();
                    device8.setId((long) instruct.getSalveId());
                    device8.setDeviceNo((long) instruct.getSalveId());
                    device8.setTemperature((BigDecimal) data);
                    deviceMapper.updateById(device8);
                    break;

                case "0x0022":
                    // // // System.out.println("~~~~~~~~~~~~0x0022~~~~~~~~~~~~：" + data);
                    DevBaseDevice device9 = new DevBaseDevice();
                    device9.setId((long) instruct.getSalveId());
                    device9.setDeviceNo((long) instruct.getSalveId());
                    device9.setDimmerNum(((BigDecimal) data).intValue());
                    // System.out.println(device9);

                    deviceMapper.updateById(device9);
                    break;

                case "0x0023":
                    // // System.out.println("~~~~~~~~~~~0x0023~~~~~~~~~~~~~：" + data);
                    DevBaseDevice device10 = new DevBaseDevice();
                    device10.setId((long) instruct.getSalveId());
                    device10.setDeviceNo((long) instruct.getSalveId());
                    device10.setDcSwitchNum(((BigDecimal) data).intValue());
                    // System.out.println(device10);

                    deviceMapper.updateById(device10);
                    break;

                case "0x0024":
                    // // // System.out.println("~~~~~~~~~0x0024~~~~~~~~~~:" + data);
                    DevBaseDevice device11 = new DevBaseDevice();
                    device11.setId((long) instruct.getSalveId());
                    device11.setDeviceNo((long) instruct.getSalveId());
                    device11.setAcLoopNum(((BigDecimal) data).intValue());
                    // System.out.println(device11);

                    deviceMapper.updateById(device11);
                    break;

                case "0x0025":
                    // // // System.out.println("~~~~~~~~~0x0025~~~~~~~~~~:" + data);
                    DevBaseDevice device12 = new DevBaseDevice();
                    device12.setId((long) instruct.getSalveId());
                    device12.setDeviceNo((long) instruct.getSalveId());
                    device12.setOtherSwitchNum(((BigDecimal) data).intValue());
                    // System.out.println(device12);

                    deviceMapper.updateById(device12);
                    break;

                case "0x0026":
                    DevBaseDevice device13 = new DevBaseDevice();
                    device13.setId((long) instruct.getSalveId());
                    device13.setDeviceNo((long) instruct.getSalveId());
                    device13.setAcModuleNum(((BigDecimal) data).intValue());
                    // System.out.println(device13);

                    deviceMapper.updateById(device13);
                    break;

                case "0x0027":
                    DevBaseDevice device14 = new DevBaseDevice();
                    device14.setId((long) instruct.getSalveId());
                    device14.setDeviceNo((long) instruct.getSalveId());
                    // int dcModuleNum = ((BigDecimal) data).intValue() >= 40 ? ((BigDecimal) data).intValue() : 0;
                    int dcModuleNum = ((BigDecimal) data).intValue();
                    device14.setDcModuleNum(dcModuleNum);

                    // System.out.println(device14);

                    deviceMapper.updateById(device14);
                    break;

                case "0x0028":
                    DevBaseDevice device15 = new DevBaseDevice();
                    device15.setId((long) instruct.getSalveId());
                    device15.setDeviceNo((long) instruct.getSalveId());
                    device15.setAcInputLoop(((BigDecimal) data).intValue());
                    deviceMapper.updateById(device15);
                    break;

                case "0x0029":
                    DevBaseDevice device16 = new DevBaseDevice();
                    device16.setId((long) instruct.getSalveId());
                    device16.setDeviceNo((long) instruct.getSalveId());
                    device16.setAcCurrentDisplay(((BigDecimal) data).intValue());
                    deviceMapper.updateById(device16);
                    break;

                case "0x002A":
                    DevBaseDevice device17 = new DevBaseDevice();
                    device17.setId((long) instruct.getSalveId());
                    device17.setDeviceNo((long) instruct.getSalveId());
                    device17.setAcSamplingMode(((BigDecimal) data).intValue());
                    deviceMapper.updateById(device17);
                    break;

                case "0x0400":
                case "0x0428":
                    try {
                        ArrayList<BigDecimal> shortList = (ArrayList<BigDecimal>) data;
                        ArrayList<DevFaultAcdc> result = new ArrayList<>();
                        for (int i = 0; i < shortList.size(); i++) {
                            DevFaultAcdc acdc = new DevFaultAcdc();
                            acdc.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                            acdc.setDeviceId(instruct.getSalveId());
                            acdc.setNo(i + 1);
                            acdc.setVoltage(shortList.get(i));
                            result.add(acdc);
                        }
                        // log.error("~~~0x0400~~~~~~~~~~~0x0400~~~~~0x0400~~~~~~result~~~~~~~~~{}",result);
                        faultAcdcMapper.insertOrUpdateBatch(result);
                    } catch (Exception e) {
                        log.error("0x0400 | 0x0428 出错", e);
                    }
                    break;

                case "0x0410":
                case "0x0438":
                    try {
                        ArrayList<BigDecimal> shortList1 = (ArrayList<BigDecimal>) data;
                        ArrayList<DevFaultAcdc> resul1t = new ArrayList<>();
                        for (int i = 0; i < shortList1.size(); i++) {
                            DevFaultAcdc acdc1 = new DevFaultAcdc();
                            acdc1.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                            acdc1.setDeviceId(instruct.getSalveId());
                            acdc1.setNo(i + 1);
                            acdc1.setElectricity(shortList1.get(i));
                            resul1t.add(acdc1);
                        }
                        faultAcdcMapper.insertOrUpdateBatch(resul1t);
                    } catch (Exception e) {
                        log.error("0x0410 | 0x0438 出错", e);
                    }
                    break;

                case "0x0420":
                    ArrayList<DevFaultDcdc> result1 = new ArrayList<>();
                    for (int i = 0; i < ((ArrayList<BigDecimal>) data).size(); i++) {
                        DevFaultDcdc dcdc = new DevFaultDcdc();
                        dcdc.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                        dcdc.setDeviceId(instruct.getSalveId());
                        dcdc.setNo(i + 1);
                        dcdc.setVoltage(((ArrayList<BigDecimal>) data).get(i));
                        result1.add(dcdc);
                    }
                    faultDcdcMapper.insertOrUpdateBatch(result1);
                    break;

                case "0x0430":

                    ArrayList<DevFaultDcdc> result2 = new ArrayList<>();
                    for (int i = 0; i < ((ArrayList<BigDecimal>) data).size(); i++) {
                        DevFaultDcdc dcdc1 = new DevFaultDcdc();
                        dcdc1.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                        dcdc1.setDeviceId(instruct.getSalveId());
                        dcdc1.setNo(i + 1);
                        dcdc1.setElectricity(((ArrayList<BigDecimal>) data).get(i));
                        result2.add(dcdc1);
                    }
                    faultDcdcMapper.insertOrUpdateBatch(result2);
                    break;

                case "0X04A0":
                    ArrayList<DevStatusDccLoop> result3 = new ArrayList<>();
                    for (int i = 0; i < ((ArrayList<Short>) data).toArray().length; i++) {
                        DevStatusDccLoop loop6 = new DevStatusDccLoop();
                        loop6.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                        loop6.setDeviceId((long) instruct.getSalveId());
                        loop6.setNo(String.valueOf(i + 1));
                        loop6.setBrightnessSetting(Long.valueOf(String.valueOf(((ArrayList<Short>) data).toArray()[i])));
                        result3.add(loop6);
                    }
                    dccLoopMapper.insertOrUpdateBatch(result3);
                    break;

                case "0X04C8":

                    ArrayList<DevStatusDccLoop> loop7result3 = new ArrayList<>();
                    for (int i = 0; i < ((ArrayList<Short>) data).toArray().length; i++) {
                        DevStatusDccLoop loop7 = new DevStatusDccLoop();
                        loop7.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                        loop7.setDeviceId((long) instruct.getSalveId());
                        loop7.setNo(String.valueOf(i + 1));
                        loop7.setBrightnessFeedback(Long.valueOf(String.valueOf(((ArrayList<Short>) data).toArray()[i])));
                        loop7result3.add(loop7);
                    }
                    dccLoopMapper.insertOrUpdateBatch(loop7result3);
                    break;

                case "0X04F0":
                    ArrayList<DevStatusDccLoop> loop8result4 = new ArrayList<>();

                    // // // System.out.println("~~~~~~~~~0X04F0~~~~~~~~~~:" + data);
                    for (int i = 0; i < ((ArrayList<BigDecimal>) data).size(); i++) {
                        DevStatusDccLoop loop8 = new DevStatusDccLoop();
                        loop8.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                        loop8.setDeviceId((long) instruct.getSalveId());
                        loop8.setNo(String.valueOf(i + 1));
                        loop8.setOutputVoltage(((ArrayList<BigDecimal>) data).get(i));
                        loop8result4.add(loop8);
                    }
                    dccLoopMapper.insertOrUpdateBatch(loop8result4);
                    break;

                case "0X0518":
                    ArrayList<DevStatusDccLoop> loop9result4 = new ArrayList<>();
                    for (int i = 0; i < ((ArrayList<BigDecimal>) data).size(); i++) {
                        DevStatusDccLoop loop9 = new DevStatusDccLoop();
                        loop9.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                        loop9.setDeviceId((long) instruct.getSalveId());
                        loop9.setNo(String.valueOf(i + 1));
                        loop9.setOutputCurrent(((ArrayList<BigDecimal>) data).get(i));
                        loop9result4.add(loop9);
                    }
                    dccLoopMapper.insertOrUpdateBatch(loop9result4);
                    break;

                case "0X0540":

                    ArrayList<DevStatusDccLoop> loop10result4 = new ArrayList<>();
                    for (int i = 0; i < ((ArrayList<BigDecimal>) data).size(); i++) {
                        DevStatusDccLoop loop10 = new DevStatusDccLoop();
                        loop10.setId(Long.valueOf(instruct.getSalveId() + "" + (i + 1)));
                        loop10.setDeviceId((long) instruct.getSalveId());
                        loop10.setNo(String.valueOf(i + 1));
                        loop10.setInternalTemperature(((ArrayList<BigDecimal>) data).get(i));
                        loop10result4.add(loop10);
                    }
                    dccLoopMapper.insertOrUpdateBatch(loop10result4);
                    break;

                case "0x0600":
                    // // System.out.println("馈线支路名称01" + data);
                    updateBranch(1L, instruct, data);
                    break;
                case "0x060A":
                    // // System.out.println("馈线支路名称02" + data);
                    updateBranch(2L, instruct, data);
                    break;
                case "0x0614":
                    // // System.out.println("馈线支路名称03" + data);
                    updateBranch(3L, instruct, data);
                    break;
                case "0x061E":
                    // // System.out.println("馈线支路名称04" + data);
                    updateBranch(4L, instruct, data);
                    break;
                case "0x0628":
                    // // System.out.println("馈线支路名称05" + data);
                    updateBranch(5L, instruct, data);
                    break;
                case "0x0632":
                    // // System.out.println("馈线支路名称06" + data);
                    updateBranch(6L, instruct, data);
                    break;
                case "0x063C":
                    // // System.out.println("馈线支路名称07" + data);
                    updateBranch(7L, instruct, data);
                    break;
                case "0x0646":
                    // // System.out.println("馈线支路名称08" + data);
                    updateBranch(8L, instruct, data);
                    break;
                case "0x0650":
                    // // System.out.println("馈线支路名称09" + data);
                    updateBranch(9L, instruct, data);
                    break;
                case "0x065A":
                    // // System.out.println("馈线支路名称10" + data);
                    updateBranch(10L, instruct, data);
                    break;
                case "0x0664":
                    // // System.out.println("馈线支路名称11" + data);
                    updateBranch(11L, instruct, data);
                    break;
                case "0x066E":
                    // // System.out.println("馈线支路名称12" + data);
                    updateBranch(12L, instruct, data);
                    break;
                case "0x0678":
                    // // System.out.println("馈线支路名称13" + data);
                    updateBranch(13L, instruct, data);
                    break;
                case "0x0682":
                    // // System.out.println("馈线支路名称14" + data);
                    updateBranch(14L, instruct, data);
                    break;
                case "0x068C":
                    // // System.out.println("馈线支路名称15" + data);
                    updateBranch(15L, instruct, data);
                    break;
                case "0x0696":
                    // // System.out.println("馈线支路名称16" + data);
                    updateBranch(16L, instruct, data);
                    break;
                case "0x06A0":
                    // // System.out.println("馈线支路名称17" + data);
                    updateBranch(17L, instruct, data);
                    break;
                case "0x06AA":
                    // // System.out.println("馈线支路名称18" + data);
                    updateBranch(18L, instruct, data);
                    break;
                case "0x06B4":
                    // // System.out.println("馈线支路名称19" + data);
                    updateBranch(19L, instruct, data);
                    break;
                case "0x06BE":
                    // // System.out.println("馈线支路名称20" + data);
                    updateBranch(20L, instruct, data);
                    break;
                case "0x06C8":
                    // // System.out.println("馈线支路名称21" + data);
                    updateBranch(21L, instruct, data);
                    break;
                case "0x06D2":
                    // // System.out.println("馈线支路名称22" + data);
                    updateBranch(22L, instruct, data);
                    break;
                case "0x06DC":
                    // // System.out.println("馈线支路名称23" + data);
                    updateBranch(23L, instruct, data);
                    break;
                case "0x06E6":
                    // // System.out.println("馈线支路名称24" + data);
                    updateBranch(24L, instruct, data);
                    break;
                case "0x06F0":
                    // // System.out.println("馈线支路名称25" + data);
                    updateBranch(25L, instruct, data);
                    break;
                case "0x06FA":
                    // // System.out.println("馈线支路名称26" + data);
                    updateBranch(26L, instruct, data);
                    break;
                case "0x0704":
                    // // System.out.println("馈线支路名称27" + data);
                    updateBranch(27L, instruct, data);
                    break;
                case "0x070E":
                    // // System.out.println("馈线支路名称28" + data);
                    updateBranch(28L, instruct, data);
                    break;
                case "0x0718":
                    // // System.out.println("馈线支路名称29" + data);
                    updateBranch(29L, instruct, data);
                    break;
                case "0x0722":
                    // // System.out.println("馈线支路名称30" + data);
                    updateBranch(30L, instruct, data);
                    break;
                case "0x072C":
                    // // System.out.println("馈线支路名称31" + data);
                    updateBranch(31L, instruct, data);
                    break;
                case "0x0736":
                    // // System.out.println("馈线支路名称32" + data);
                    updateBranch(32L, instruct, data);
                    break;
                case "0x0740":
                    // // System.out.println("馈线支路名称33" + data);
                    updateBranch(33L, instruct, data);
                    break;
                case "0x074A":
                    // // System.out.println("馈线支路名称34" + data);
                    updateBranch(34L, instruct, data);
                    break;
                case "0x0754":
                    // // System.out.println("馈线支路名称35" + data);
                    updateBranch(35L, instruct, data);
                    break;
                case "0x075E":
                    // // System.out.println("馈线支路名称36" + data);
                    updateBranch(36L, instruct, data);
                    break;
                case "0x0768":
                    // // System.out.println("馈线支路名称37" + data);
                    updateBranch(37L, instruct, data);
                    break;
                case "0x0772":
                    // // System.out.println("馈线支路名称38" + data);
                    updateBranch(38L, instruct, data);
                    break;
                case "0x077C":
                    // // System.out.println("馈线支路名称39" + data);
                    updateBranch(39L, instruct, data);
                    break;
                case "0x0786":
                    // // System.out.println("馈线支路名称40" + data);
                    updateBranch(40L, instruct, data);
                    break;
                case "0x0790":
                    // // System.out.println("馈线支路名称41" + data);
                    updateBranch(41L, instruct, data);
                    break;
                case "0x079A":
                    // // System.out.println("馈线支路名称42" + data);
                    updateBranch(42L, instruct, data);
                    break;
                case "0x07A4":
                    // // System.out.println("馈线支路名称43" + data);
                    updateBranch(43L, instruct, data);
                    break;
                case "0x07AE":
                    // // System.out.println("馈线支路名称44" + data);
                    updateBranch(44L, instruct, data);
                    break;
                case "0x07B8":
                    // // System.out.println("馈线支路名称45" + data);
                    updateBranch(45L, instruct, data);
                    break;
                case "0x07C2":
                    // // System.out.println("馈线支路名称46" + data);
                    updateBranch(46L, instruct, data);
                    break;
                case "0x07CC":
                    // // System.out.println("馈线支路名称47" + data);
                    updateBranch(47L, instruct, data);
                    break;
                case "0x07D6":
                    // // System.out.println("馈线支路名称48" + data);
                    updateBranch(48L, instruct, data);
                    break;
                case "0x07E0":
                    // // System.out.println("馈线支路名称49" + data);
                    updateBranch(49L, instruct, data);
                    break;
                case "0x07EA":
                    // // System.out.println("馈线支路名称50" + data);
                    updateBranch(50L, instruct, data);
                    break;
                case "0x07F4":
                    // // System.out.println("馈线支路名称51" + data);
                    updateBranch(51L, instruct, data);
                    break;
                case "0x07FE":
                    // // System.out.println("馈线支路名称52" + data);
                    updateBranch(52L, instruct, data);
                    break;
                case "0x0808":
                    // // System.out.println("馈线支路名称53" + data);
                    updateBranch(53L, instruct, data);
                    break;
                case "0x0812":
                    // // System.out.println("馈线支路名称54" + data);
                    updateBranch(54L, instruct, data);
                    break;
                case "0x081C":
                    // // System.out.println("馈线支路名称55" + data);
                    updateBranch(55L, instruct, data);
                    break;
                case "0x0826":
                    // // System.out.println("馈线支路名称56" + data);
                    updateBranch(56L, instruct, data);
                    break;
                case "0x0830":
                    // // System.out.println("馈线支路名称57" + data);
                    updateBranch(57L, instruct, data);
                    break;
                case "0x083A":
                    // // System.out.println("馈线支路名称58" + data);
                    updateBranch(58L, instruct, data);
                    break;
                case "0x0844":
                    // // System.out.println("馈线支路名称59" + data);
                    updateBranch(59L, instruct, data);
                    break;
                case "0x084E":
                    // // System.out.println("馈线支路名称60" + data);
                    updateBranch(60L, instruct, data);
                    break;
                case "0x0858":
                    // // System.out.println("馈线支路名称61" + data);
                    updateBranch(61L, instruct, data);
                    break;
                case "0x0862":
                    // // System.out.println("馈线支路名称62" + data);
                    updateBranch(62L, instruct, data);
                    break;
                case "0x086C":
                    // // System.out.println("馈线支路名称63" + data);
                    updateBranch(63L, instruct, data);
                    break;
                case "0x0876":
                    // // System.out.println("馈线支路名称64" + data);
                    updateBranch(64L, instruct, data);
                    break;

                case "0x2000":
                    // // // System.out.println("~~~主监控版本~~~" + data);
                    DevBaseDevice device = new DevBaseDevice();
                    device.setId((long) instruct.getSalveId());
                    device.setDeviceNo((long) instruct.getSalveId());
                    device.setVersion(data + "");
                    deviceMapper.updateById(device);
                    break;

                case "0x2100":
                    try {
                        DevConfigIlluminanceSensor sensor = new DevConfigIlluminanceSensor();
                        sensor.setId(Integer.valueOf(instruct.getSalveId() + "" + 1));
                        sensor.setDeviceId(instruct.getSalveId());
                        sensor.setSensorId(1);
                        sensor.setOutControlChannel(((ArrayList<BigDecimal>) data).get(0));
                        sensor.setOutControlAddr(((ArrayList<BigDecimal>) data).get(1));
                        configIlluminanceSensorMapper.insertOrUpdate(sensor);

                        sensor.setId(Integer.valueOf(instruct.getSalveId() + "" + 2));
                        sensor.setDeviceId(instruct.getSalveId());
                        sensor.setSensorId(2);
                        sensor.setOutControlChannel(((ArrayList<BigDecimal>) data).get(2));
                        sensor.setOutControlAddr(((ArrayList<BigDecimal>) data).get(3));
                        configIlluminanceSensorMapper.insertOrUpdate(sensor);

                        sensor.setId(Integer.valueOf(instruct.getSalveId() + "" + 3));
                        sensor.setDeviceId(instruct.getSalveId());
                        sensor.setSensorId(3);
                        sensor.setOutControlChannel(((ArrayList<BigDecimal>) data).get(4));
                        sensor.setOutControlAddr(((ArrayList<BigDecimal>) data).get(5));
                        configIlluminanceSensorMapper.insertOrUpdate(sensor);

                        sensor.setId(Integer.valueOf(instruct.getSalveId() + "" + 4));
                        sensor.setDeviceId(instruct.getSalveId());
                        sensor.setSensorId(4);
                        sensor.setOutControlChannel(((ArrayList<BigDecimal>) data).get(6));
                        sensor.setOutControlAddr(((ArrayList<BigDecimal>) data).get(7));
                        configIlluminanceSensorMapper.insertOrUpdate(sensor);

                        sensor.setId(Integer.valueOf(instruct.getSalveId() + "" + 5));
                        sensor.setDeviceId(instruct.getSalveId());
                        sensor.setSensorId(5);
                        sensor.setOutControlChannel(((ArrayList<BigDecimal>) data).get(8));
                        sensor.setOutControlAddr(((ArrayList<BigDecimal>) data).get(9));
                        configIlluminanceSensorMapper.insertOrUpdate(sensor);
                    } catch (Exception e) {
                        System.out.println("~~~~~~~~~~~~0x2100~~~~~~~~~~~出错：" + e);
                    }
                    break;

                case "0x23B4":
                    // // System.out.println("系统设置 -回路分组-分组选择：" + data);
                    List<DevConfigGroup> listtt = new ArrayList<>();
                    if (data.toString().length() >= 2) {
                        for (int i = 0; i < data.toString().length(); i += 2) {
                            String groupId = data.toString().substring(i, i + 2);
                            if (groupId.startsWith("0")) {
                                groupId = groupId.substring(1);
                            }
                            DevConfigGroup group = new DevConfigGroup();
                            // System.out.println("~~~~~~~~~test groupId~~~~~~~~" + groupId);
                            group.setId(Long.valueOf(instruct.getSalveId() + groupId));
                            // group.setDeviceId((long) instruct.getSalveId());
                            group.setGroupId(Long.valueOf(groupId));
                            listtt.add(group);
                        }
                    }
                    groupMapper.insertOrUpdateBatch(listtt);
                    break;

                case "0x23F5":
                    // System.out.println("系统设置 -回路分组-分组回路编号01：" + data);
                    updateGroupLoop(1L, instruct, data);
                    break;
                case "0x241D":
                    // // System.out.println("系统设置 -回路分组-分组回路编号02：" + data);
                    updateGroupLoop(2L, instruct, data);
                    break;
                case "0x2445":
                    // // System.out.println("系统设置 -回路分组-分组回路编号03：" + data);
                    updateGroupLoop(3L, instruct, data);
                    break;
                case "0x246D":
                    // // System.out.println("系统设置 -回路分组-分组回路编号04：" + data);
                    updateGroupLoop(4L, instruct, data);
                    break;
                case "0x2495":
                    // // System.out.println("系统设置 -回路分组-分组回路编号05：" + data);
                    updateGroupLoop(5L, instruct, data);
                    break;
                case "0x24BD":
                    // // System.out.println("系统设置 -回路分组-分组回路编号06：" + data);
                    updateGroupLoop(6L, instruct, data);
                    break;
                case "0x24E5":
                    // // System.out.println("系统设置 -回路分组-分组回路编号07：" + data);
                    updateGroupLoop(7L, instruct, data);
                    break;
                case "0x250D":
                    // // System.out.println("系统设置 -回路分组-分组回路编号08：" + data);
                    updateGroupLoop(8L, instruct, data);
                    break;
                case "0x2535":
                    // // System.out.println("系统设置 -回路分组-分组回路编号09：" + data);
                    updateGroupLoop(9L, instruct, data);
                    break;
                case "0x255D":
                    // // System.out.println("系统设置 -回路分组-分组回路编号10：" + data);
                    updateGroupLoop(10L, instruct, data);
                    break;
                case "0x2585":
                    // // System.out.println("系统设置 -回路分组-分组回路编号11：" + data);
                    updateGroupLoop(11L, instruct, data);
                    break;
                case "0x25AD":
                    // // System.out.println("系统设置 -回路分组-分组回路编号12：" + data);
                    updateGroupLoop(12L, instruct, data);
                    break;
                case "0x25D5":
                    // // System.out.println("系统设置 -回路分组-分组回路编号13：" + data);
                    updateGroupLoop(13L, instruct, data);
                    break;
                case "0x25FD":
                    // // System.out.println("系统设置 -回路分组-分组回路编号14：" + data);
                    updateGroupLoop(14L, instruct, data);
                    break;
                case "0x2625":
                    // // System.out.println("系统设置 -回路分组-分组回路编号15：" + data);
                    updateGroupLoop(15L, instruct, data);
                    break;
                case "0x264D":
                    // // System.out.println("系统设置 -回路分组-分组回路编号16：" + data);
                    updateGroupLoop(16L, instruct, data);
                    break;

                // ------------------------------------------------遥调的读取协议------------------------------------------------
                case "0xA000":
                    DevConfigTime time = new DevConfigTime();
                    time.setId(instruct.getSalveId());
                    time.setDeviceNo(instruct.getSalveId());
                    time.setYear((int) ((short[]) data)[0]);
                    time.setMonth((int) ((short[]) data)[1]);
                    time.setDay((int) ((short[]) data)[2]);
                    time.setHour((int) ((short[]) data)[3]);
                    time.setMinute((int) ((short[]) data)[4]);
                    time.setSecond((int) ((short[]) data)[5]);
                    time.setTimeSwitch((int) ((short[]) data)[6]);
                    configTimeMapper.insertOrUpdate(time);
                    break;

                case "0xA1F0":
                    // System.out.println("~~~~~0xA1F0~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    updateGroupName(1L, instruct, data);
                    break;
                case "0xA1FA":
                    updateGroupName(2L, instruct, data);
                    break;
                case "0xA204":
                    updateGroupName(3L, instruct, data);
                    break;
                case "0xA20E":
                    updateGroupName(4L, instruct, data);
                    break;
                case "0xA218":
                    updateGroupName(5L, instruct, data);
                    break;
                case "0xA222":
                    updateGroupName(6L, instruct, data);
                    break;
                case "0xA22C":
                    updateGroupName(7L, instruct, data);
                    break;
                case "0xA236":
                    updateGroupName(8L, instruct, data);
                    break;
                case "0xA240":
                    updateGroupName(9L, instruct, data);
                    break;
                case "0xA24A":
                    updateGroupName(10L, instruct, data);
                    break;
                case "0xA254":
                    updateGroupName(11L, instruct, data);
                    break;
                case "0xA25E":
                    updateGroupName(12L, instruct, data);
                    break;
                case "0xA268":
                    updateGroupName(13L, instruct, data);
                    break;
                case "0xA272":
                    updateGroupName(14L, instruct, data);
                    break;
                case "0xA27C":
                    updateGroupName(15L, instruct, data);
                    break;
                case "0xA286":
                    updateGroupName(16L, instruct, data);
                    break;

                case "0xA483":
                    updateScene(1L, instruct, data);
                    break;
                case "0xA48D":
                    updateScene(2L, instruct, data);
                    break;
                case "0xA497":
                    updateScene(3L, instruct, data);
                    break;
                case "0xA4A1":
                    updateScene(4L, instruct, data);
                    break;
                case "0xA4AB":
                    updateScene(5L, instruct, data);
                    break;
                case "0xA4B5":
                    updateScene(6L, instruct, data);
                    break;
                case "0xA4BF":
                    updateScene(7L, instruct, data);
                    break;
                case "0xA4C9":
                    updateScene(8L, instruct, data);
                    break;
                case "0xA4D3":
                    updateScene(9L, instruct, data);
                    break;
                case "0xA4DD":
                    updateScene(10L, instruct, data);
                    break;

                case "0xA5AE":
                    try {
                        short[] list = (short[]) data;
                        // System.out.println("~~~~~~~~~~~~~~0xA5AE~~~~~~~~~~" + Arrays.toString(list));
                        List<DevConfigDistrict> districtList = new ArrayList<>();
                        List<DevConfigGroup> groupList = new ArrayList<>();
                        for (int i = 0; i < list.length; i++) {
                            DevConfigDistrict district = new DevConfigDistrict();
                            district.setId(Long.valueOf("" + instruct.getSalveId() + (i + 1)));
                            district.setDeviceId(Long.valueOf(instruct.getSalveId()));
                            district.setDistrictId(i + 1L);
                            district.setAddr((int) list[i]);
                            districtList.add(district);

                            DevConfigGroup groupDistrict = new DevConfigGroup();
                            groupDistrict.setId(Long.valueOf("" + instruct.getSalveId() + (i + 1L)));
                            groupDistrict.setDistrictAddr((int) list[i]);
                            groupList.add(groupDistrict);
                        }

                        districtMapper.insertOrUpdateBatch(districtList);
                        groupMapper.insertOrUpdateBatch(groupList);
                    } catch (Exception e) {
                        System.out.println("~~~0xA5AE~出错出错出错~~~" + e);
                    }
                    break;

                case "0XA80A":
                case "0XA853":
                case "0XA89C":
                    try {
                        // System.out.println("~~~~~~~~~~~~~~~0XA853~~~~~~~~~~~~~" + Arrays.toString((short[])data));
                        // 系统设置-控制方式-普通模式-时控？-时段信息？-亮度
                        Map<String, Object> resul12t = (Map<String, Object>) data;
                        ArrayList<DevConfigTimeControl> timeControls = (ArrayList<DevConfigTimeControl>) resul12t.get("data1");
                        timeControlMapper.insertOrUpdateBatch(timeControls);
                        ArrayList<DevConfigSimpleGroup> simpleGroups = (ArrayList<DevConfigSimpleGroup>) resul12t.get("data2");
                        simpleGroupMapper.insertOrUpdateBatch(simpleGroups);
                    } catch (Exception e) {
                        System.out.println("~~~0XA80A~0XA853~0XA89C~~~~~~Exception~~~~~~~~" + e);
                    }
                    break;

                case "0XAAEE":
                    ArrayList<DevConfigTimeControlScene> timeControls2 = (ArrayList<DevConfigTimeControlScene>) data;
                    System.out.println(JSONUtil.toJsonPrettyStr(timeControls2));
                    controlSceneMapper.insertOrUpdateBatch(timeControls2);
                    break;

                case "0XAB2E":
                    ArrayList<DevConfigTimeControlScene> timeControls3 = (ArrayList<DevConfigTimeControlScene>) data;
                    controlSceneMapper.insertOrUpdateBatch(timeControls3);
                    break;

                case "0XAB6E":
                    ArrayList<DevConfigTimeControlScene> timeControls4 = (ArrayList<DevConfigTimeControlScene>) data;
                    controlSceneMapper.insertOrUpdateBatch(timeControls4);
                    break;

                case "0XAE7A":
                    // System.out.println(data);
                    ArrayList<DevConfigInfraredSensor> infraredSensors = (ArrayList<DevConfigInfraredSensor>) ((Map<String, Object>) data).get("data");
                    infraredSensorMapper.insertOrUpdateBatch(infraredSensors);
                    // 设置分组与红外传感器对应表的数据
                    writeSensorGroupData(instruct, (int[]) ((Map<String, Object>) data).get("data1"), 1, 1);
                    writeSensorGroupData(instruct, (int[]) ((Map<String, Object>) data).get("data2"), 1, 2);
                    writeSensorGroupData(instruct, (int[]) ((Map<String, Object>) data).get("data3"), 1, 3);
                    break;

                case "0XAE8F":
                    ArrayList<DevConfigIlluminanceSensor> illuminanceSensors = (ArrayList<DevConfigIlluminanceSensor>) ((Map<String, Object>) data).get("data");
                    configIlluminanceSensorMapper.insertOrUpdateBatch(illuminanceSensors);
                    // 设置分组与照度传感器对应表的数据
                    writeSensorGroupData(instruct, (int[]) ((Map<String, Object>) data).get("data1"), 2, 1);
                    writeSensorGroupData(instruct, (int[]) ((Map<String, Object>) data).get("data2"), 2, 2);
                    writeSensorGroupData(instruct, (int[]) ((Map<String, Object>) data).get("data3"), 2, 3);
                    writeSensorGroupData(instruct, (int[]) ((Map<String, Object>) data).get("data4"), 2, 4);
                    writeSensorGroupData(instruct, (int[]) ((Map<String, Object>) data).get("data5"), 2, 5);
                    break;

                case "0XAF1E":
                    remoteReadMapper.insertOrUpdate((DevDeviceRemoteRead) ((Map<String, Object>) data).get("acNum"));
                    controlAcMapper.insertOrUpdateBatch((List<DevConfigTimeControlAc>) ((Map<String, Object>) data).get("controlAc"));
                    break;

                case "0xB63C":
                    DevDeviceRemoteRead deviceRemoteRead = new DevDeviceRemoteRead();
                    deviceRemoteRead.setId(instruct.getSalveId());
                    deviceRemoteRead.setDeviceId(instruct.getSalveId());
                    deviceRemoteRead.setLoopLux((int) ((short[]) data)[0]);
                    deviceRemoteRead.setGroupLux((int) ((short[]) data)[1]);
                    remoteReadMapper.insertOrUpdate(deviceRemoteRead);
                    break;


                case "0XB716":
                    DevDeviceRemoteRead deviceRemoteRead1 = new DevDeviceRemoteRead();
                    deviceRemoteRead1.setId(instruct.getSalveId());
                    deviceRemoteRead1.setDeviceId(instruct.getSalveId());
                    deviceRemoteRead1.setInfraredSensingMode((int) ((short[]) data)[0]);
                    deviceRemoteRead1.setIlluminanceSensingMode((int) ((short[]) data)[1]);
                    deviceRemoteRead1.setManualMode((int) ((short[]) data)[2]);
                    remoteReadMapper.insertOrUpdate(deviceRemoteRead1);
                    break;

                case "0xAFC2":
                case "0xAFE3":
                case "0xB004":
                case "0xB025":
                case "0xB046":
                case "0xB067":
                case "0xB088":
                case "0xB0A9":
                case "0xB0CA":
                case "0xB0EB":
                    // System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    updateSceneConfig(data);
                    break;

                case "0XA8E6":
                    List<DevBaseSimpleControl> simpleControls;
                    if (((short[]) data)[0] == 1) {
                        simpleControls = (List<DevBaseSimpleControl>) updateTimeControlEnabled(instruct, 1, 1);
                    } else if (((short[]) data)[0] == 2) {
                        simpleControls = (List<DevBaseSimpleControl>) updateTimeControlEnabled(instruct, 1, 2);
                    } else if (((short[]) data)[0] == 3) {
                        simpleControls = (List<DevBaseSimpleControl>) updateTimeControlEnabled(instruct, 1, 3);
                    } else {
                        simpleControls = (List<DevBaseSimpleControl>) updateTimeControlEnabled(instruct, 1, 0);
                    }
                    simpleControlMapper.insertOrUpdateBatch(simpleControls);
                    break;
                case "0XABAE":
                    List<DevBaseSceneControl> sceneControls = new ArrayList<>();
                    if (((short[]) data)[0] == 1) {
                        sceneControls = (List<DevBaseSceneControl>) updateTimeControlEnabled(instruct, 2, 1);
                    } else if (((short[]) data)[0] == 2) {
                        sceneControls = (List<DevBaseSceneControl>) updateTimeControlEnabled(instruct, 2, 2);
                    } else if (((short[]) data)[0] == 3) {
                        sceneControls = (List<DevBaseSceneControl>) updateTimeControlEnabled(instruct, 2, 3);
                    } else {
                        sceneControls = (List<DevBaseSceneControl>) updateTimeControlEnabled(instruct, 2, 0);
                    }
                    sceneControlMapper.insertOrUpdateBatch(sceneControls);
                    break;

                default:
                    log.error("遥测/遥调读协议写入/更新数据库失败，报文信息：{}", instruct);
            }
        } catch (Exception e) {
            // log.error("Update03DataService→updateData：", e);
        }
    }

    /**
     * 更新普通/场景时控启用
     * type： 1 普通  2 场景
     * flag：1/2/3
     */
    public List<?> updateTimeControlEnabled(DevInstruct instruct, int type, int flag) {
        if (type == 1) {
            List<DevBaseSimpleControl> result = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                DevBaseSimpleControl control = new DevBaseSimpleControl();
                control.setId(Integer.valueOf("" + instruct.getSalveId() + i));
                control.setDeviceId(instruct.getSalveId());
                control.setControlId(i);
                if (i == flag) {
                    control.setEnabled(1);
                } else {
                    control.setEnabled(0);
                }
                result.add(control);
            }
            return result;
        } else {
            List<DevBaseSceneControl> result = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                DevBaseSceneControl control = new DevBaseSceneControl();
                control.setId(Integer.valueOf("" + instruct.getSalveId() + i));
                control.setDeviceId(instruct.getSalveId());
                control.setControlId(i);
                if (i == flag) {
                    control.setEnabled(1);
                } else {
                    control.setEnabled(0);
                }
                result.add(control);
            }
            return result;
        }
    }

    /**
     * 更新场景配置信息
     */
    private void updateSceneConfig(Object data) {
        // System.out.println("~~~~~~~~~~~~~~~updateSceneConfig~~~~~~~~~~~~~" + (ArrayList<DevConfigScene>) data);
        ArrayList<DevConfigScene> source = (ArrayList<DevConfigScene>) data;
        sceneMapper.insertOrUpdateBatch(source);
    }

    /**
     * 更新场景信息
     */
    private void updateScene(Long sceneId, DevInstruct instruct, Object data) {
        // DevBaseScene baseScene = new DevBaseScene();
        // baseScene.setId(Long.valueOf(("" + 1 + sceneId)));
        // baseScene.setSceneId(Math.toIntExact(sceneId));
        // baseScene.setName(ScaleUtil.toAscII(((short[]) data)) + "");
        // baseSceneMapper.insertOrUpdate(baseScene);
    }

    /**
     * 更新分组名称
     *
     * @param groupId
     * @param instruct
     * @param data
     */
    private void updateGroupName(Long groupId, DevInstruct instruct, Object data) {
        DevConfigGroup group = new DevConfigGroup();
        group.setId(Long.valueOf(("" + instruct.getSalveId() + groupId)));
        group.setGroupId(groupId);
        group.setDeviceId(Long.valueOf(instruct.getSalveId()));
        group.setName(ScaleUtil.toAscII(((short[]) data)) + "");
        groupMapper.insertOrUpdate(group);
    }

    // 更新分组回路编号信息
    // groupId：分组ID
    // instruct：报文
    // data：分组回路编号
    private void updateGroupLoop(Long groupId, DevInstruct instruct, Object data) {
        DevGroupLoop groupLoop = new DevGroupLoop();
        groupLoop.setId(Long.valueOf(instruct.getSalveId() + "" + groupId));
        groupLoop.setDeviceId((long) instruct.getSalveId());
        groupLoop.setGroupId(groupId);
        groupLoop.setLoopNo(data + "");
        groupLoopMapper.insertOrUpdate(groupLoop);

        DevConfigGroup group = new DevConfigGroup();
        group.setId(Long.valueOf(instruct.getSalveId() + "" + groupId));
        group.setDeviceId((long) instruct.getSalveId());
        group.setGroupId(groupId);
        group.setLoopNum((long) (data + "").length() / 2);
        groupMapper.insertOrUpdate(group);
    }

    // 更新馈线支路名称信息
    // branchNo：馈线支路编号
    // instruct：报文
    // data：馈线名称
    private void updateBranch(Long branchNo, DevInstruct instruct, Object data) {
        DevBaseBranch bashBranch = new DevBaseBranch();
        bashBranch.setId(Long.valueOf(instruct.getSalveId() + "" + branchNo));
        bashBranch.setDeviceId((long) instruct.getSalveId());
        bashBranch.setBranchNo(branchNo);
        bashBranch.setBranchName(data + "");
        bashBranchMapper.insertOrUpdate(bashBranch);
    }

    // 判断时间统计电量 周
    private boolean shouldUpdateWeeklyData(LocalDateTime now) {
        return now.getDayOfWeek() == DayOfWeek.MONDAY;
    }

    // 判断时间统计电量 月
    private boolean shouldUpdateMonthlyData(LocalDateTime now) {
        return now.getDayOfMonth() == 1;
    }

    // 判断时间统计电量 季
    private boolean shouldUpdateQuarterlyData(LocalDateTime now) {
        Month month = now.getMonth();
        return (month == Month.JANUARY || month == Month.APRIL ||
            month == Month.JULY || month == Month.OCTOBER)
            && now.getDayOfMonth() == 1;
    }

    // 判断时间统计电量 年
    private boolean shouldUpdateYearlyData(LocalDateTime now) {
        return now.getMonth() == Month.JANUARY && now.getDayOfMonth() == 1;
    }

    // 红外传感器或照度传感器与分组的数据写入方法
    // type：1 红外传感器，2 照度传感器
    // sensorId：传感器模式的ID
    @Async
    public void writeSensorGroupData(DevInstruct instruct, int[] data, int type, int sensorId) {
        // System.out.println("~~~~~~~~~~~~~writeSensorGroupData~~~~~~~~~~~~~"+ Arrays.toString(data));
        try {
            if (type == 1) {
                List<DevInfraredGroup> list = new ArrayList<>();
                for (int i = 0; i < data.length; i++) {
                    DevInfraredGroup infraredGroup = new DevInfraredGroup();
                    infraredGroup.setId(Integer.valueOf("" + instruct.getSalveId() + sensorId + (i + 1)));
                    infraredGroup.setDeviceId(instruct.getSalveId());
                    infraredGroup.setGroupId(i + 1);
                    infraredGroup.setSensorId(sensorId);
                    infraredGroup.setSelectStatus(data[i]);
                    list.add(infraredGroup);
                }
                infraredGroupMapper.insertOrUpdateBatch(list);
            } else {
                List<DevIlluminanceGroup> list = new ArrayList<>();
                for (int i = 0; i < data.length; i++) {
                    DevIlluminanceGroup illuminanceGroup = new DevIlluminanceGroup();
                    illuminanceGroup.setId(Integer.valueOf("" + instruct.getSalveId() + sensorId + (i + 1)));
                    illuminanceGroup.setDeviceId(instruct.getSalveId());
                    illuminanceGroup.setGroupId(i + 1);
                    illuminanceGroup.setSensorId(sensorId);
                    illuminanceGroup.setSelectStatus(data[i]);
                    list.add(illuminanceGroup);
                }
                illuminanceGroupMapper.insertOrUpdateBatch(list);
            }
        } catch (Exception e) {
            // log.error("writeSensorGroupData error:", e);
        }
    }

    private void createPower(int slaveId, long timestamp, Object data, int type, int index) {
        DevBasePower p = new DevBasePower();
        p.setId(IdGenerator.UUIDId());
        p.setDeviceId(slaveId);
        p.setTimestamp(timestamp);
        p.setType(type);
        p.setValue(((ArrayList<BigDecimal>) data).get(index));
        powerMapper.insert(p);
    }

    private void deleteLastP(int deviceId, int type, int saveCount) {
        LambdaQueryWrapper<DevBasePower> lqw = new LambdaQueryWrapper<DevBasePower>()
            .eq(DevBasePower::getDeviceId, deviceId)
            .eq(DevBasePower::getType, type);
        long count = powerMapper.selectCount(lqw);
        if (count > saveCount) powerMapper.delete(lqw.orderByAsc(DevBasePower::getTimestamp).last("LIMIT 1"));
    }

    private void Fun0x0000(DevInstruct instruct, Object data) {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        long timestamp = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        LocalDate date = LocalDate.now().minusDays(1);
        long s = date.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        long e = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond();
        Integer deviceId = instruct.getSalveId();
        Map<Integer, Long> source = new HashMap<>();
        List<DevBasePower> l = powerMapper.selectList(new LambdaQueryWrapper<DevBasePower>()
            .select(DevBasePower::getId, DevBasePower::getType)
            .eq(DevBasePower::getDeviceId, deviceId)
            .in(DevBasePower::getType, Arrays.asList(2, 3, 4, 5, 6))
            .between(DevBasePower::getTimestamp, s, e)
            .orderByAsc(DevBasePower::getType)
        );
        if (null != l && !l.isEmpty())
            source = l.stream().collect(Collectors.toMap(DevBasePower::getType, DevBasePower::getId, (o1, o2) -> o2));

        if (null == source.get(2)) {
            createPower(deviceId, timestamp, data, 2, 2);
            deleteLastP(deviceId, 2, 366);
        }
        if (shouldUpdateWeeklyData(LocalDateTime.now()) && null == source.get(3)) {
            createPower(deviceId, timestamp, data, 3, 3);
            deleteLastP(deviceId, 3, 10);
        }
        if (shouldUpdateMonthlyData(LocalDateTime.now()) && null == source.get(4)) {
            createPower(deviceId, timestamp, data, 4, 4);
            deleteLastP(deviceId, 4, 10);
        }
        if (shouldUpdateQuarterlyData(LocalDateTime.now()) && null == source.get(5)) {
            createPower(deviceId, timestamp, data, 5, 5);
            deleteLastP(deviceId, 5, 10);
        }
        if (shouldUpdateYearlyData(LocalDateTime.now()) && null == source.get(6)) {
            createPower(deviceId, timestamp, data, 6, 6);
            deleteLastP(deviceId, 6, 10);
        }
    }

    private void Fun0X00CB(DevInstruct instruct, Object data) {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        long timestamp = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        LocalDate date = LocalDate.now().minusDays(1);
        long s = date.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        long e = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond();
        Integer deviceId = instruct.getSalveId();
        long count = energyMeterDayMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterDay>()
            .eq(DevEnergyMeterDay::getDeviceNo, deviceId)
            .between(DevEnergyMeterDay::getTimestamp, s, e)
            .in(DevEnergyMeterDay::getEnergyMeterNo, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40))
        );

        if (count <= 0) {
            // 1#电表日耗电量
            List<BigDecimal> d = (List<BigDecimal>) data;
            List<DevEnergyMeterDay> days = new ArrayList<>();
            int id = 1;
            for (BigDecimal b : d) {
                DevEnergyMeterDay day = new DevEnergyMeterDay();
                day.setId(IdGenerator.UUIDId());
                day.setDeviceNo(instruct.getSalveId());
                day.setEnergyMeterNo(id++);
                day.setPower(b);
                day.setTimestamp(timestamp);
                days.add(day);
            }
            energyMeterDayMapper.insertBatch(days);
            long saveCount = energyMeterDayMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterDay>()
                .eq(DevEnergyMeterDay::getDeviceNo, deviceId).eq(DevEnergyMeterDay::getEnergyMeterNo, 1));
            if (saveCount > 366) energyMeterDayMapper.delete(new LambdaQueryWrapper<DevEnergyMeterDay>()
                .eq(DevEnergyMeterDay::getDeviceNo, deviceId).orderByAsc(DevEnergyMeterDay::getTimestamp).last("LIMIT 40"));
        }
    }

    private void Fun0X011B(DevInstruct instruct, Object data) {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        long timestamp = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        LocalDate date = LocalDate.now().minusDays(1);
        long s = date.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        long e = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond();
        Integer deviceId = instruct.getSalveId();
        long count = energyMeterWeekMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterWeek>()
            .eq(DevEnergyMeterWeek::getDeviceNo, deviceId)
            .between(DevEnergyMeterWeek::getTimestamp, s, e)
            .in(DevEnergyMeterWeek::getEnergyMeterNo, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40))
        );

        if (shouldUpdateWeeklyData(LocalDateTime.now()) && count <= 0) {
            // 1#电表周耗电量
            List<BigDecimal> d = (List<BigDecimal>) data;
            List<DevEnergyMeterWeek> days = new ArrayList<>();
            int id = 1;
            for (BigDecimal b : d) {
                DevEnergyMeterWeek day = new DevEnergyMeterWeek();
                day.setId(IdGenerator.UUIDId());
                day.setDeviceNo(instruct.getSalveId());
                day.setEnergyMeterNo(id++);
                day.setPower(b);
                day.setTimestamp(timestamp);
                days.add(day);
            }
            energyMeterWeekMapper.insertBatch(days);
            long saveCount = energyMeterWeekMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterWeek>()
                .eq(DevEnergyMeterWeek::getDeviceNo, deviceId).eq(DevEnergyMeterWeek::getEnergyMeterNo, 1));
            if (saveCount > 10) energyMeterWeekMapper.delete(new LambdaQueryWrapper<DevEnergyMeterWeek>()
                .eq(DevEnergyMeterWeek::getDeviceNo, deviceId).orderByAsc(DevEnergyMeterWeek::getTimestamp).last("LIMIT 40"));
        }
    }

    private void Fun0X016B(DevInstruct instruct, Object data) {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        long timestamp = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        LocalDate date = LocalDate.now().minusDays(1);
        long s = date.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        long e = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond();
        Integer deviceId = instruct.getSalveId();
        long count = energyMeterMonthMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterMonth>()
            .eq(DevEnergyMeterMonth::getDeviceNo, deviceId)
            .between(DevEnergyMeterMonth::getTimestamp, s, e)
            .in(DevEnergyMeterMonth::getEnergyMeterNo, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40))
        );

        if (shouldUpdateMonthlyData(LocalDateTime.now()) && count <= 0) {
            // 1#电表周耗电量
            List<BigDecimal> d = (List<BigDecimal>) data;
            List<DevEnergyMeterMonth> days = new ArrayList<>();
            int id = 1;
            for (BigDecimal b : d) {
                DevEnergyMeterMonth day = new DevEnergyMeterMonth();
                day.setId(IdGenerator.UUIDId());
                day.setDeviceNo(instruct.getSalveId());
                day.setEnergyMeterNo(id++);
                day.setPower(b);
                day.setTimestamp(timestamp);
                days.add(day);
            }
            energyMeterMonthMapper.insertBatch(days);
            long saveCount = energyMeterMonthMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterMonth>()
                .eq(DevEnergyMeterMonth::getDeviceNo, deviceId).eq(DevEnergyMeterMonth::getEnergyMeterNo, 1));
            if (saveCount > 10) energyMeterMonthMapper.delete(new LambdaQueryWrapper<DevEnergyMeterMonth>()
                .eq(DevEnergyMeterMonth::getDeviceNo, deviceId).orderByAsc(DevEnergyMeterMonth::getTimestamp).last("LIMIT 40"));
        }
    }

    private void Fun0X01BB(DevInstruct instruct, Object data) {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        long timestamp = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        LocalDate date = LocalDate.now().minusDays(1);
        long s = date.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        long e = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond();
        Integer deviceId = instruct.getSalveId();
        long count = energyMeterQuarterMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterQuarter>()
            .eq(DevEnergyMeterQuarter::getDeviceNo, deviceId)
            .between(DevEnergyMeterQuarter::getTimestamp, s, e)
            .in(DevEnergyMeterQuarter::getEnergyMeterNo, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40))
        );

        if (shouldUpdateQuarterlyData(LocalDateTime.now()) && count <= 0) {
            // 1#电表周耗电量
            List<BigDecimal> d = (List<BigDecimal>) data;
            List<DevEnergyMeterQuarter> days = new ArrayList<>();
            int id = 1;
            for (BigDecimal b : d) {
                DevEnergyMeterQuarter day = new DevEnergyMeterQuarter();
                day.setId(IdGenerator.UUIDId());
                day.setDeviceNo(instruct.getSalveId());
                day.setEnergyMeterNo(id++);
                day.setPower(b);
                day.setTimestamp(timestamp);
                days.add(day);
            }
            energyMeterQuarterMapper.insertBatch(days);
            long saveCount = energyMeterQuarterMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterQuarter>()
                .eq(DevEnergyMeterQuarter::getDeviceNo, deviceId).eq(DevEnergyMeterQuarter::getEnergyMeterNo, 1));
            if (saveCount > 10) energyMeterQuarterMapper.delete(new LambdaQueryWrapper<DevEnergyMeterQuarter>()
                .eq(DevEnergyMeterQuarter::getDeviceNo, deviceId).orderByAsc(DevEnergyMeterQuarter::getTimestamp).last("LIMIT 40"));
        }
    }

    private void Fun0X020B(DevInstruct instruct, Object data) {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        long timestamp = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        LocalDate date = LocalDate.now().minusDays(1);
        long s = date.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        long e = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond();
        Integer deviceId = instruct.getSalveId();
        long count = energyMeterYearMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterYear>()
            .eq(DevEnergyMeterYear::getDeviceNo, deviceId)
            .between(DevEnergyMeterYear::getTimestamp, s, e)
            .in(DevEnergyMeterYear::getEnergyMeterNo, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40))
        );

        if (shouldUpdateYearlyData(LocalDateTime.now()) && count <= 0) {
            // 1#电表周耗电量
            List<BigDecimal> d = (List<BigDecimal>) data;
            List<DevEnergyMeterYear> days = new ArrayList<>();
            int id = 1;
            for (BigDecimal b : d) {
                DevEnergyMeterYear day = new DevEnergyMeterYear();
                day.setId(IdGenerator.UUIDId());
                day.setDeviceNo(instruct.getSalveId());
                day.setEnergyMeterNo(id++);
                day.setPower(b);
                day.setTimestamp(timestamp);
                days.add(day);
            }
            energyMeterYearMapper.insertBatch(days);
            long saveCount = energyMeterYearMapper.selectCount(new LambdaQueryWrapper<DevEnergyMeterYear>()
                .eq(DevEnergyMeterYear::getDeviceNo, deviceId).eq(DevEnergyMeterYear::getEnergyMeterNo, 1));
            if (saveCount > 10) energyMeterYearMapper.delete(new LambdaQueryWrapper<DevEnergyMeterYear>()
                .eq(DevEnergyMeterYear::getDeviceNo, deviceId).orderByAsc(DevEnergyMeterYear::getTimestamp).last("LIMIT 40"));
        }
    }
}
