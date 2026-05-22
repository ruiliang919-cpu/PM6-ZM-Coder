package com.ruoyi.web.controller.zm;

import com.ruoyi.cache.AcDcCache;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.bo.DevFaultAcdcBo;
import com.ruoyi.zm.domain.bo.DevFaultDcdcBo;
import com.ruoyi.zm.domain.bo.DevStatusAcLoopBo;
import com.ruoyi.zm.domain.bo.DevStatusDccLoopBo;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

// 直流机柜-机柜信息 控制层
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/cabinetInfo")
public class CabinetInformationController {
    private final IDevStatusAcLoopService devStatusAcLoopService;
    private final IDevStatusDccLoopService devStatusDccLoopService;
    private final IDevBaseDeviceService deviceService;
    private final IDevFaultAcdcService faultAcdcService;
    private final IDevFaultDcdcService faultDcdcService;
    private final DevBaseDeviceMapper deviceMapper;
    private final AcDcCache cache;

    // 获取机柜下拉列表（设备号 deviceId 与 设备名称 name 不分页）
    @GetMapping("/getDeviceList")
    public R<?> getDeviceList() {
        List<DevBaseDevice> source = deviceMapper.selectList();
        List<DeviceItemVo> result = new ArrayList<>();
        source.forEach(item -> {
            DeviceItemVo deviceItemVo = new DeviceItemVo();
            deviceItemVo.setDeviceId(Math.toIntExact(item.getDeviceNo()));
            deviceItemVo.setName(item.getDeviceName());
            result.add(deviceItemVo);
        });
        return R.ok(result);
    }

    // 回路信息-交流回路信息（配电柜）
    // 根据设备ID获取对应机柜的交流回路信息
    // @SaCheckPermission("zm:cabinetInfo:acList")
    @PostMapping("/acList")
    public TableDataInfo<DevStatusAcLoopVo> getAcList(@RequestBody PageQuery pageQuery, Long slaveId) {
        TableDataInfo<DevStatusAcLoopVo> acList = cache.getAcList(slaveId);
        if (acList != null) return acList;
        try {
            DevBaseDeviceVo deviceVo = deviceService.queryById(slaveId);
            if (deviceVo.getType() == 1) {
                return TableDataInfo.build();
            }
            DevStatusAcLoopBo bo = new DevStatusAcLoopBo();
            bo.setDeviceId(slaveId);
            List<DevStatusAcLoopVo> source = devStatusAcLoopService.queryList(bo);
            long id = 1;
            for (DevStatusAcLoopVo devStatusAcLoopVo : source) {
                devStatusAcLoopVo.setId(id++);
            }
            return TableDataInfo.build(source.subList(0, deviceVo.getAcLoopNum()));
        } catch (Exception e) {
//            log.error("/zm/cabinetInfo/acList", e);
            return TableDataInfo.build();
        }
    }

    // 回路信息-直流回路状态（配电柜）
    // 根据设备ID获取对应机柜的直流回路状态
    // @SaCheckPermission("zm:cabinetInfo:dccList")
    @PostMapping("/dccList")
    public TableDataInfo<DevStatusDccLoopVo> getDccList(@RequestBody PageQuery pageQuery, @RequestParam("slaveId") Long slaveId) {
        // pageQuery.setPageSize(100);
        TableDataInfo<DevStatusDccLoopVo> dccList = cache.getDccList(pageQuery, slaveId);
        if (dccList != null) return dccList;
        try {
            DevBaseDeviceVo deviceVo = deviceService.queryById(slaveId);
            if (deviceVo.getType() == 1) {
                return TableDataInfo.build();
            }
            DevStatusDccLoopBo bo = new DevStatusDccLoopBo();
            bo.setDeviceId(slaveId);
            List<DevStatusDccLoopVo> source = devStatusDccLoopService.queryList(bo);
            long id = 1;
            for (int i = 0; i < deviceVo.getDimmerNum() + deviceVo.getDcSwitchNum(); i++) {
                if (i < deviceVo.getDimmerNum()) {
                    source.get(i).setId(id++);
                } else {
                    source.get(i).setId(id++);
                    source.get(i).setBrightnessSetting(null);
                    source.get(i).setBrightnessFeedback(null);
                    source.get(i).setOutputVoltage(null);
                    source.get(i).setOutputCurrent(null);
                    source.get(i).setInternalTemperature(null);
                    source.get(i).setRealTime(null);
                }
            }
            source = source.subList(0, deviceVo.getDimmerNum() + deviceVo.getDcSwitchNum());
            // 分页
            Pageable pageable = PageRequest.of(pageQuery.getPageNum() - 1, pageQuery.getPageSize());
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), source.size());
            List<DevStatusDccLoopVo> pagedSource = source.subList(start, end);
            TableDataInfo<DevStatusDccLoopVo> result = TableDataInfo.build(pagedSource);
            result.setTotal(deviceVo.getDimmerNum() + deviceVo.getDcSwitchNum());
            return result;
        } catch (Exception e) {
            log.error("/zm/cabinetInfo/dccList", e);
            return TableDataInfo.build();
        }
    }

    // 直流信息-母线信息（电源柜）
    // @SaCheckPermission("zm:cabinetInfo:busInfo")
    @GetMapping("/busInfo")
    public R<BusRespVo> getBusInfo(Long slaveId) {
        R<BusRespVo> busInfo = cache.getBusInfo(slaveId);
        if (busInfo != null) return busInfo;
        return R.ok(new BusRespVo());
    }

    // 根据机柜类型判断，电源柜AC/DC 1，配电箱DC/DC 0
    // 直流信息-DC/DC信息（AC/DC信息）
    // @SaCheckPermission("zm:cabinetInfo:dcAc")
    @PostMapping("/dcAc")
    public R<TreeMap<String, Object>> getDcAc(@RequestBody PageQuery pageQuery, Integer slaveId) {
        pageQuery.setPageSize(100);
        R<TreeMap<String, Object>> dcAc = cache.getDcAc(pageQuery, slaveId);
        if (dcAc != null) return dcAc;
        try {
            DevBaseDeviceVo deviceVo = deviceService.queryById(Long.valueOf(slaveId));
            TreeMap<String, Object> result = new TreeMap<>();
            TableDataInfo<DevFaultAcdcVo> table = new TableDataInfo<>();
            if (deviceVo.getType() == 1) {
                DevFaultAcdcBo bo = new DevFaultAcdcBo();
                bo.setDeviceId(slaveId);
                result.put("cabinetType", deviceVo.getType());
                List<DevFaultAcdcVo> source = faultAcdcService.queryList(bo);
                if (!source.isEmpty() && source.size() >= deviceVo.getAcModuleNum()) {
                    source = source.subList(0, Math.min(16, deviceVo.getAcModuleNum()));
                    int id = 1;
                    for (DevFaultAcdcVo devFaultAcdcVo : source) {
                        devFaultAcdcVo.setId((long) id++);
                    }
                    int start = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
                    List<DevFaultAcdcVo> pagedSource = source.subList(start, Math.min(start + pageQuery.getPageSize(), source.size()));
                    table = TableDataInfo.build(pagedSource);
                    table.setTotal(deviceVo.getAcModuleNum());
                }
                result.put("data", table);
                return R.ok(result);
            } else if (deviceVo.getType() == 0) {
                DevFaultDcdcBo bo = new DevFaultDcdcBo();
                bo.setDeviceId(slaveId);
                result.put("cabinetType", deviceVo.getType());
                List<DevFaultDcdcVo> resultList = new ArrayList<>();
                List<DevFaultDcdcVo> source = faultDcdcService.queryList(bo);
                if (!source.isEmpty() && source.size() >= deviceVo.getDcModuleNum()) {
                    List<DevFaultDcdcVo> sourceSub = source.subList(0, Math.min(16, deviceVo.getDcModuleNum()));
                    int id = 1;
                    for (DevFaultDcdcVo devFaultDcdcVo : source) {
                        devFaultDcdcVo.setId((long) id++);
                    }
                    resultList = sourceSub;
                }
                result.put("data", resultList);
                return R.ok(result);
            } else return R.ok(new TreeMap<>());
        } catch (Exception e) {
            log.error("", e);
            return R.ok(new TreeMap<>());
        }
    }

    // 绝缘信息-母线绝缘（电源柜）
    // 母线正极绝缘电阻值，母线负极绝电阻值
    // @SaCheckPermission("zm:cabinetInfo:getBusInsulation")
    @GetMapping("/getBusInsulation")
    public R<BusInsulationRespVo> getBusInsulation(Long slaveId) {
        R<BusInsulationRespVo> busInsulation = cache.getBusInsulation(slaveId);
        if (busInsulation != null) return busInsulation;
        DevBaseDeviceVo deviceVo = deviceService.queryById(slaveId);
        if (deviceVo.getType() == 0) {
            return R.ok();
        }
        return R.ok();
    }

    // 交流信息-交流 1 路与 2 路（电源柜）
    // @SaCheckPermission("zm:cabinetInfo:getAlternating")
    @GetMapping("/getAlternating")
    public R<List<DevStatusAcVo>> getAlternating(Long slaveId) {
        R<List<DevStatusAcVo>> alternating = cache.getAlternating(slaveId);
        if (alternating != null) return alternating;
        return R.ok();
    }
}
