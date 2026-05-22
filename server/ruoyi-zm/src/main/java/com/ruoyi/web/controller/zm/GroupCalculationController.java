package com.ruoyi.web.controller.zm;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.cache.Key;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.cache.LoopByGroupCache.LOOP_NUMS_ADDR_ARR;

// 历史记录-电量计算-分区组合计算
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/calculation/zone")
public class GroupCalculationController {
    private final DevZoneCombinationMapper baseMapper;
    private final DevBaseDeviceMapper deviceMapper;
    private final DevEnergyMeterTotalMapper totalMapper;
    private final DevEnergyMeterYearMapper yearMapper;
    private final DevEnergyMeterQuarterMapper quarterMapper;
    private final DevEnergyMeterMonthMapper monthMapper;
    private final DevEnergyMeterWeekMapper weekMapper;
    private final DevEnergyMeterDayMapper dayMapper;
    private final Key key;
    private final DListUtil dListUtil;

    // 根据分区组合获取分区码，根据分区码获取分组，根据分组获取回路，根据回路获取耗电量
    @PostMapping("/total")
    public TableDataInfo<CabinetCalculationData> total(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getSourceList("total"), pageQuery);
    }

    @PostMapping("/year")
    public TableDataInfo<CabinetCalculationData> year(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getSourceList("year"), pageQuery);
    }

    @PostMapping("/quarter")
    public TableDataInfo<CabinetCalculationData> quarter(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getSourceList("quarter"), pageQuery);
    }

    @PostMapping("/month")
    public TableDataInfo<CabinetCalculationData> month(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getSourceList("month"), pageQuery);
    }

    @PostMapping("/week")
    public TableDataInfo<CabinetCalculationData> week(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getSourceList("week"), pageQuery);
    }

    @PostMapping("/day")
    public TableDataInfo<CabinetCalculationData> day(@RequestBody PageQuery pageQuery) {
        return key.getPageTable(getSourceList("day"), pageQuery);
    }

    // 分区组合列表
    @PostMapping("/list")
    public TableDataInfo<DevZoneCombinationRespVo> list(@RequestBody PageQuery pageQuery) {
        List<DevZoneCombination> source = baseMapper.selectList();
        List<DevZoneCombinationRespVo> result = new ArrayList<>();
        if (!source.isEmpty()) {
            for (int i = 0; i < source.size(); i++) {
                DevZoneCombinationRespVo vo = new DevZoneCombinationRespVo();
                vo.setOrderNo((long) i + 1);
                vo.setName(source.get(i).getName());
                vo.setZoneId(source.get(i).getZoneId());
                vo.setZoneList(source.get(i).getZoneList().split(","));
                result.add(vo);
            }
            return key.getPageTable(result, pageQuery);
        }
        return TableDataInfo.build();
    }

    // 分区的列表，提供给分区组合选择
    @GetMapping("/zoneList")
    public R<List<ZoneCombinationSelectVo>> zoneList() {
        List<ZoneCombinationSelectVo> result = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            ZoneCombinationSelectVo vo = new ZoneCombinationSelectVo();
            vo.setName("分区码：" + i);
            vo.setZoneId(i);
            vo.setZoneIdStr("" + i);
            result.add(vo);
        }
        return R.ok(result);
    }

    // 查询单个分区组合
    @GetMapping("/getOne")
    public R<DevZoneCombinationOneVo> getOne(Integer zoneId) {
        LambdaQueryWrapper<DevZoneCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevZoneCombination::getZoneId, zoneId);
        DevZoneCombination zone = baseMapper.selectOne(lqw);
        DevZoneCombinationOneVo vo = new DevZoneCombinationOneVo();
        if (zone != null) {
            vo.setZoneId(zone.getZoneId());
            vo.setName(zone.getName());
            vo.setZoneList(zone.getZoneList().split(","));
        }
        return R.ok(vo);
    }

    // 分区组合设置
    @PostMapping("/set")
    public R<?> set(@RequestBody ZoneCombinationAddVo addVo) {
        LambdaQueryWrapper<DevZoneCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevZoneCombination::getZoneId, addVo.getZoneId());
        DevZoneCombination source = baseMapper.selectOne(lqw);

        if (addVo.getName() != null && !"".equals(addVo.getName())) {
            source.setName(addVo.getName());
        }

        if (addVo.getZones().length > 0) {
            StringBuilder zoneList = new StringBuilder();
            for (int i = 0; i < addVo.getZones().length; i++) {
                if (i == addVo.getZones().length - 1) {
                    zoneList.append(addVo.getZones()[i]);
                } else {
                    zoneList.append(addVo.getZones()[i]).append(",");
                }
            }
            source.setZoneList(String.valueOf(zoneList));
        }
        LambdaUpdateWrapper<DevZoneCombination> luw = new LambdaUpdateWrapper<>();
        luw.eq(DevZoneCombination::getZoneId, addVo.getZoneId());
        return baseMapper.update(source, luw) > 0 ? R.ok("更新成功") : R.ok("更新失败");
    }

    // 新增分区组合
    @PostMapping("/add")
    public R<?> add(@RequestBody ZoneCombinationAddVo addVo) {
        LambdaQueryWrapper<DevZoneCombination> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(DevZoneCombination::getZoneId);
        List<DevZoneCombination> zoneCombinations = baseMapper.selectList(lqw);
        long id = 0;
        if (!zoneCombinations.isEmpty()) {
            id = zoneCombinations.get(0).getZoneId() + 1;
        }
        DevZoneCombination zoneCombination = new DevZoneCombination();
        zoneCombination.setZoneId(id);
        zoneCombination.setName(addVo.getName());
        StringBuilder zoneList = new StringBuilder();
        if (addVo.getZones().length > 0) {
            for (int i = 0; i < addVo.getZones().length; i++) {
                if (i == addVo.getZones().length - 1) {
                    zoneList.append(addVo.getZones()[i]);
                } else {
                    zoneList.append(addVo.getZones()[i]).append(",");
                }
            }
        }
        zoneCombination.setZoneList(String.valueOf(zoneList));
        return baseMapper.insert(zoneCombination) > 0 ? R.ok("添加成功") : R.ok("添加失败");
    }

    // 删除分区组合
    @GetMapping("/delete")
    public R<?> delete(Integer zoneId) {
        LambdaQueryWrapper<DevZoneCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevZoneCombination::getZoneId, zoneId);
        return baseMapper.delete(lqw) > 0 ? R.ok("删除成功") : R.ok("删除失败");
    }

    // 根据回路编号获取日周月季年总耗电量
    private BigDecimal getEnergy(Integer deviceId, Set<Integer> nos, String type) {
        if (nos.isEmpty()) return new BigDecimal(0);

        List<Integer> noList = new ArrayList<>(nos);
        switch (type) {
            case "total": {
                LambdaQueryWrapper<DevEnergyMeterTotal> lqw = new LambdaQueryWrapper<>();
                lqw.eq(DevEnergyMeterTotal::getDeviceNo, deviceId);
                lqw.orderByDesc(DevEnergyMeterTotal::getTimestamp);
                Long size = totalMapper.selectCount(lqw);
                List<DevEnergyMeterTotal> meterTotals;
                if (size >= 40) {
                    lqw.last("LIMIT 40");
                    meterTotals = totalMapper.selectList(lqw);
                } else return new BigDecimal(0);
                return getEnergy(noList, JSONUtil.toJsonPrettyStr(meterTotals), "total");
            }
            case "year": {
                LambdaQueryWrapper<DevEnergyMeterYear> lqw = new LambdaQueryWrapper<>();
                lqw.eq(DevEnergyMeterYear::getDeviceNo, deviceId);
                lqw.orderByDesc(DevEnergyMeterYear::getTimestamp);
                Long size = yearMapper.selectCount(lqw);
                List<DevEnergyMeterYear> meterTotals;
                if (size >= 40) {
                    lqw.last("LIMIT 40");
                    meterTotals = yearMapper.selectList(lqw);
                } else return new BigDecimal(0);
                return getEnergy(noList, JSONUtil.toJsonPrettyStr(meterTotals), "year");
            }
            case "quarter": {
                LambdaQueryWrapper<DevEnergyMeterQuarter> lqw = new LambdaQueryWrapper<>();
                lqw.eq(DevEnergyMeterQuarter::getDeviceNo, deviceId);
                lqw.orderByDesc(DevEnergyMeterQuarter::getTimestamp);
                Long size = quarterMapper.selectCount(lqw);
                List<DevEnergyMeterQuarter> meterTotals;
                if (size >= 40) {
                    lqw.last("LIMIT 40");
                    meterTotals = quarterMapper.selectList(lqw);
                } else return new BigDecimal(0);
                return getEnergy(noList, JSONUtil.toJsonPrettyStr(meterTotals), "quarter");
            }
            case "month": {
                LambdaQueryWrapper<DevEnergyMeterMonth> lqw = new LambdaQueryWrapper<>();
                lqw.eq(DevEnergyMeterMonth::getDeviceNo, deviceId);
                lqw.orderByDesc(DevEnergyMeterMonth::getTimestamp);
                Long size = monthMapper.selectCount(lqw);
                List<DevEnergyMeterMonth> meterTotals;
                if (size >= 40) {
                    lqw.last("LIMIT 40");
                    meterTotals = monthMapper.selectList(lqw);
                } else return new BigDecimal(0);
                return getEnergy(noList, JSONUtil.toJsonPrettyStr(meterTotals), "month");
            }
            case "week": {
                LambdaQueryWrapper<DevEnergyMeterWeek> lqw = new LambdaQueryWrapper<>();
                lqw.eq(DevEnergyMeterWeek::getDeviceNo, deviceId);
                lqw.orderByDesc(DevEnergyMeterWeek::getTimestamp);
                Long size = weekMapper.selectCount(lqw);
                List<DevEnergyMeterWeek> meterTotals;
                if (size >= 40) {
                    lqw.last("LIMIT 40");
                    meterTotals = weekMapper.selectList(lqw);
                } else return new BigDecimal(0);
                return getEnergy(noList, JSONUtil.toJsonPrettyStr(meterTotals), "week");
            }
            case "day": {
                LambdaQueryWrapper<DevEnergyMeterDay> lqw = new LambdaQueryWrapper<>();
                lqw.eq(DevEnergyMeterDay::getDeviceNo, deviceId);
                lqw.orderByDesc(DevEnergyMeterDay::getTimestamp);
                Long size = dayMapper.selectCount(lqw);
                List<DevEnergyMeterDay> meterTotals;
                if (size >= 40) {
                    lqw.last("LIMIT 40");
                    meterTotals = dayMapper.selectList(lqw);
                } else return new BigDecimal(0);
                return getEnergy(noList, JSONUtil.toJsonPrettyStr(meterTotals), "day");
            }
            default:
                return new BigDecimal(0);
        }
    }

    // 根据电能表集合计算电量
    public BigDecimal getEnergy(List<Integer> noList, String energyListStr, String type) {
        List<DevEnergyMeterTotal> energyList = JSONUtil.toList(energyListStr, DevEnergyMeterTotal.class);
        Set<Integer> noSet = new HashSet<>(noList);
        energyList = energyList.stream().filter(it -> noSet.contains(it.getEnergyMeterNo())).collect(Collectors.toList());
        BigDecimal result = new BigDecimal(0);
        for (DevEnergyMeterTotal devEnergyMeterTotal : energyList) {
            result = result.add(devEnergyMeterTotal.getPower());
        }
        return result;
    }

    // 根据设备获取对应分区码的分组，再根据分组获取分组下的回路，去重
    private Set<Integer> getLoops(Integer deviceId, String[] zoneId) {
        // 获取分组的分区码
        short[] zoneArr = key.getRemoteByArr(deviceId, "0xA5AE");
        // System.err.println("~~~~~~~~~~~~" + Arrays.toString(zoneArr));
        Set<Integer> loops = new HashSet<>();
        if (zoneArr != null && zoneArr.length > 0 && zoneArr[0] != -1) {
            // 填充数组默认值到指定长度
            if (zoneArr.length < 16) {
                short[] paddedArray = new short[16];
                System.arraycopy(zoneArr, 0, paddedArray, 0, zoneArr.length);
                for (int i = zoneArr.length; i < 16; i++) {
                    paddedArray[i] = -1;
                }
                zoneArr = paddedArray;
            }
            List<Integer> groups = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                // 提取出对应分区码的分组
                for (String s : zoneId) {
                    try {
                        if (Objects.equals(Integer.parseInt(s), Integer.parseInt("" + zoneArr[i]))) {
                            groups.add(i + 1);
                        }
                    } catch (Exception e) {
                        // log.error("数组下标越界~~~~~{}", Arrays.toString(zoneArr));
                    }
                }
            }
            for (Integer group : groups) {
                String noStr = key.getTelemeter(deviceId, LOOP_NUMS_ADDR_ARR[(group - 1)]) + "";
                if (!"null".equals(noStr)) {
                    for (int index = 0; index < noStr.length() / 2; index++) {
                        Integer no = Integer.valueOf(noStr.substring(index * 2, index * 2 + 2));
                        loops.add(no);
                    }
                }
            }
        }
        return loops;
    }

    // 获取分区组合数据源
    public List<CabinetCalculationData> getSourceList(String type) {
        List<Long> nos = dListUtil.Nos();
        if (nos.isEmpty()) return new ArrayList<>();
        List<DevZoneCombination> zoneCombinations = baseMapper.selectList();
        List<CabinetCalculationData> source = new ArrayList<>();
        for (DevZoneCombination z : zoneCombinations) {
            CabinetCalculationData data = new CabinetCalculationData();
            data.setId(Math.toIntExact(z.getZoneId()));
            data.setName(z.getName());
            BigDecimal total = new BigDecimal(0);
            if (z.getZoneList() != null && !z.getZoneList().isEmpty()) {
                String[] zoneIds = z.getZoneList().split(",");
                for (Long no : nos) {
                    try {
                        Set<Integer> loops = getLoops(Math.toIntExact(no), zoneIds);
                        total = total.add(getEnergy(Math.toIntExact(no), loops, type));
                    } catch (Exception ignored) { }
                }
            }
            data.setPower(String.valueOf(total.setScale(2, RoundingMode.HALF_UP)));
            data.setTime(Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            source.add(data);
        }
        return source;
    }
}
