package com.ruoyi.web.controller.zm;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.Key;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevFaultRecord;
import com.ruoyi.zm.domain.bo.DevFaultRecordBo;
import com.ruoyi.zm.domain.vo.DevFaultRecordExcelVo;
import com.ruoyi.zm.domain.vo.DevFaultRecordVo;
import com.ruoyi.zm.domain.vo.RecordFaultReqVo;
import com.ruoyi.zm.domain.vo.RecordOneReqVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevFaultRecordMapper;
import com.ruoyi.zm.service.IDevFaultRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ruoyi.common.core.domain.R.SUCCESS;
import static com.ruoyi.common.core.domain.R.restResult;

// 历史事件与告警控制层
@Slf4j
@RestController
@RequestMapping("/zm/record")
@RequiredArgsConstructor
public class RecordFaultController {
    private final IDevFaultRecordService faultRecordService;
    private final DevBaseDeviceMapper deviceMapper;
    private final DevFaultRecordMapper faultRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Key key;

    private Map<Long, String> cachedNameMap;
    private long nameMapCacheTime;

    private synchronized Map<Long, String> NameMap() {
        long now = System.currentTimeMillis();
        if (cachedNameMap != null && (now - nameMapCacheTime) < 60_000) {
            return cachedNameMap;
        }
        cachedNameMap = deviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>()
                .select(DevBaseDevice::getDeviceNo, DevBaseDevice::getDeviceName))
            .stream().collect(Collectors.toMap(DevBaseDevice::getDeviceNo,
                DevBaseDevice::getDeviceName, (o1, o2) -> o2));
        nameMapCacheTime = now;
        return cachedNameMap;
    }

    @Scheduled(fixedDelay = 5000)
    public void allFaults() {
        // 从缓存中取出数据
        // 使用SCAN替代KEYS，避免阻塞Redis
        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keyset = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions()
                .match("zm:fault:*")
                .count(100)
                .build();
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    keyset.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            }
            return keyset;
        });
        List<DevFaultRecordVo> source = new ArrayList<>();
        Map<Long, String> nameMap = NameMap();
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                DevFaultRecordVo record = (DevFaultRecordVo) redisTemplate.opsForValue().get(key);
                if (record != null) {
                    try {
                        record.setMessage(this.key.get01Name(record.getMessage()));
                        record.setName(nameMap.get(Long.valueOf(record.getDeviceId())));
                    } catch (Exception ignored) {
                        record.setMessage(record.getMessage());
                    }
                    source.add(record);
                }
            }
            source.sort(Comparator.comparingLong(DevFaultRecordVo::getStime).reversed());
        }
        redisTemplate.delete("zm:news:allFaults");
        for (DevFaultRecordVo f : source)
            redisTemplate.opsForList().rightPush("zm:news:allFaults", f);
        redisTemplate.opsForValue().set("zm:news:allFaults:size", source.size());
    }

    /**
     * 查询当前告警-告警信息列表
     */
    @PostMapping("/getFaultSeven")
    public TableDataInfo<?> getFaultSeven(@RequestBody RecordFaultReqVo vo) {
        try {
            int start = (vo.getPageNum() - 1) * vo.getPageSize();
            int size = (Integer) redisTemplate.opsForValue().get("zm:news:allFaults:size");
            int end = Math.min(start + vo.getPageSize(), size);
            List<java.lang.Object> sourceList = redisTemplate.opsForList().range("zm:news:allFaults", start, end);
            List<DevFaultRecordVo> source = null;
            if (sourceList != null) {
                source = sourceList.stream().map(s -> (DevFaultRecordVo) s).collect(Collectors.toList());
                // 过滤事件名称，过滤设备名称，过滤事件发生时间
                source = source.stream()
                    .filter(record -> (StringUtils.isBlank(vo.getEventName()) || record.getMessage().contains(vo.getEventName())))
                    .filter(record -> (StringUtils.isBlank(vo.getDeviceName())) || record.getName().contains(vo.getDeviceName()))
                    .filter(record -> (ObjectUtils.isEmpty(vo.getStartTime())) || record.getStime() >= vo.getStartTime()).collect(Collectors.toList());
                for (DevFaultRecordVo faultRecord : source) {
                    start++;
                    faultRecord.setId(start);
                }
            }
            TableDataInfo<DevFaultRecordVo> result = TableDataInfo.build(source);
            result.setTotal(size);
            return result;
        } catch (Exception e) {
            log.error("e: ", e);
        }
        return TableDataInfo.build();
    }

    /**
     * 直流机柜-机柜记录-事件记录列表（显示近七天数据）
     */
    @PostMapping("/getRecordOne")
    public TableDataInfo<?> getRecordSeven(@RequestBody RecordOneReqVo vo) {
        TableDataInfo<DevFaultRecordVo> source = faultRecordService.queryPageList(new DevFaultRecordBo() {{
            setDeviceId(vo.getDeviceId());
            setShowType(1);
            setStime(vo.getStartTime());
            setType(2);
        }}, vo.getPageSize(), vo.getPageNum());
        DevBaseDevice d = deviceMapper.selectOne(new LambdaQueryWrapper<DevBaseDevice>().select(DevBaseDevice::getDeviceName).eq(DevBaseDevice::getDeviceNo, vo.getDeviceId()));
        String name;
        if (d != null && d.getDeviceName() != null && !d.getDeviceName().isEmpty()) name = d.getDeviceName();
        else name = "未登记机柜";
        AtomicInteger no = new AtomicInteger((vo.getPageNum() - 1) * vo.getPageSize());
        no.getAndIncrement();
        if (!source.getRows().isEmpty()) source.getRows().forEach(item -> {
            item.setId(no.getAndIncrement());
            item.setName(name);
            item.setStime(item.getStime() * 1000L);
            item.setMessage(key.get01Name(item.getMessage()));
        });
        return source;
    }

    /**
     * 查询历史记录-历史事件列表（即七天的事件记录）
     */
    @PostMapping("/getRecordSeven")
    public TableDataInfo<?> getRecordSeven(@RequestBody RecordFaultReqVo vo) {
        Map<Long, String> nameMap = NameMap();
        TableDataInfo<DevFaultRecordVo> source = faultRecordService.queryPageList(new DevFaultRecordBo() {{
            if (StrUtil.isNotBlank(vo.getDeviceName())) {
                DevBaseDevice device = deviceMapper.selectOne(new LambdaQueryWrapper<DevBaseDevice>().select(DevBaseDevice::getDeviceNo).eq(DevBaseDevice::getDeviceName, vo.getDeviceName()));
                setDeviceId(Math.toIntExact(device.getDeviceNo()));
            }
            setShowType(1);
            setMessage(vo.getEventName());
            setType(2);
        }}, vo.getPageSize(), vo.getPageNum());
        AtomicInteger no = new AtomicInteger((vo.getPageNum() - 1) * vo.getPageSize());
        no.getAndIncrement();
        if (!source.getRows().isEmpty()) source.getRows().forEach(item -> {
            item.setId(no.getAndIncrement());
            item.setName(nameMap.getOrDefault(Long.valueOf(item.getDeviceId()), "未登记机柜"));
            item.setStime(item.getStime() * 1000L);
            item.setMessage(key.get01Name(item.getMessage()));
        });
        return source;
    }

    /**
     * 查询历史记录-历史记录列表（即所有的告警信息）
     */
    @PostMapping("/getFaultAll")
    public TableDataInfo<?> getFaultAll(@RequestBody RecordFaultReqVo vo) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageSize(vo.getPageSize());
        pageQuery.setPageNum(vo.getPageNum());
        DevFaultRecordBo bo = new DevFaultRecordBo();
        bo.setType(1);
        if (vo.getDeviceName() != null) {
            LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DevBaseDevice::getDeviceName, vo.getDeviceName());
            DevBaseDevice device = deviceMapper.selectOne(lqw);
            bo.setDeviceId(Math.toIntExact(device.getDeviceNo()));
        }
        bo.setMessage(vo.getEventName());
        TableDataInfo<DevFaultRecordVo> source = faultRecordService.queryPageList(bo, pageQuery);
        AtomicInteger no = new AtomicInteger((vo.getPageNum() - 1) * vo.getPageSize());
        no.getAndIncrement();
        Map<Long, String> nameMap = NameMap();
        if (!source.getRows().isEmpty()) {
            source.getRows().forEach(item -> {
                item.setId(no.getAndIncrement());
                item.setName(nameMap.getOrDefault(Long.valueOf(item.getDeviceId()), "未登记机柜"));
                item.setStime(item.getStime() * 1000L);
                try {
                    item.setMessage(key.get01Name(item.getMessage()));
                } catch (Exception ignored) {
                    item.setMessage(item.getMessage());
                }
            });
        }
        return source;
    }

    /**
     * 查询告警信息 最新一条（需要一直调用接口）
     */
    @GetMapping("/getNews")
    public R<?> getNews() {
        String msg = (String) redisTemplate.opsForValue().get("zm:news:one");
        if (msg != null && !msg.isEmpty()) return restResult(msg, SUCCESS, "操作成功");
        else return R.ok();
    }

    @Scheduled(fixedDelay = 3000)
    public void news() {
        List<Object> range = redisTemplate.opsForList().range("zm:news:allFaults", 0, 0);
        if (range != null && !range.isEmpty()) {
            DevFaultRecordVo news = (DevFaultRecordVo) range.get(0);
            String head;
            String name = "";
            try {
                DevBaseDevice newsDevice = deviceMapper.selectById(news.getDeviceId());
                name = newsDevice != null ? newsDevice.getDeviceName() : "未知设备";
                head = key.get01Name(news.getMessage());
            } catch (Exception e) {
                head = news.getMessage();
            }
            redisTemplate.opsForValue().set("zm:news:one", head + " 设备：" + name);
        } else redisTemplate.opsForValue().set("zm:news:one", "");
    }

    /**
     * 历史事件导出
     */
    @PostMapping("/exportRecord")
    public void exportRecord(DevFaultRecordBo bo, HttpServletResponse response) {
        // bo.setType(2);
        LambdaQueryWrapper<DevFaultRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevFaultRecord::getType, 2);
        List<DevFaultRecordVo> source = faultRecordMapper.selectVoList(lqw);
        List<DevFaultRecordExcelVo> result = new ArrayList<>();
        ZoneId zoneId = ZoneId.systemDefault();
        AtomicInteger id = new AtomicInteger(1);
        Map<Long, String> nameMap = NameMap();
        if (source != null && !source.isEmpty()) {
            source.forEach(item -> {
                DevFaultRecordExcelVo vo = new DevFaultRecordExcelVo();
                vo.setId(id.getAndIncrement());
                vo.setName(nameMap.getOrDefault(Long.valueOf(item.getDeviceId()), "未登记机柜"));
                vo.setMessage(item.getMessage());
                vo.setStime(Instant.ofEpochSecond(item.getStime()).atZone(zoneId).toLocalDateTime().format(timeFormatter));
                vo.setMessage(key.get01Name(item.getMessage()));
                result.add(vo);
            });
        }
        ExcelUtil.exportExcel(result, "历史事件", DevFaultRecordExcelVo.class, response);
    }

    /**
     * 历史告警导出
     */
    @PostMapping("/exportFault")
    public void exportFault(DevFaultRecordBo bo, HttpServletResponse response) {
        bo.setType(1);
        List<DevFaultRecordVo> source = faultRecordService.queryList(bo);
        List<DevFaultRecordExcelVo> result = new ArrayList<>();
        ZoneId zoneId = ZoneId.systemDefault();
        Map<Long, String> nameMap = NameMap();
        if (source != null && !source.isEmpty()) {
            source.forEach(item -> {
                DevFaultRecordExcelVo vo = new DevFaultRecordExcelVo();
                vo.setId(item.getId());
                item.setName(nameMap.getOrDefault(Long.valueOf(item.getDeviceId()), "未登记机柜"));
                vo.setMessage(item.getMessage());
                vo.setStime(Instant.ofEpochSecond(item.getStime()).atZone(zoneId).toLocalDateTime().format(timeFormatter));
                vo.setMessage(key.get01Name(item.getMessage()));
                result.add(vo);
            });
        }
        ExcelUtil.exportExcel(result, "历史告警", DevFaultRecordExcelVo.class, response);
    }
}
