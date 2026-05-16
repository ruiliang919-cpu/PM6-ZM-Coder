package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dtflys.forest.utils.StringUtils;
import com.ruoyi.cache.DeviceCache;
import com.ruoyi.cache.Key;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.flag.DeviceFlag;
import com.ruoyi.init.MqttInit;
import com.ruoyi.schedule.ScheduleTask;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBaseScene;
import com.ruoyi.zm.domain.DevFaultRecord;
import com.ruoyi.zm.domain.DevWriteInstruct;
import com.ruoyi.zm.domain.bo.DevBaseDeviceReqBo;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.mapper.*;
import com.ruoyi.zm.service.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

// 基本资料接口
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/basic")
public class BasicInformationController {
    private final BasicService basicService;
    private final DevBaseDeviceMapper deviceMapper;
    private final DevBaseSceneMapper baseSceneMapper;
    private final DevWriteInstructMapper devWriteInstructMapper;
    private final DevFaultRecordMapper faultRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, short[]> shortArrayRedisTemplate;
    private final RedisTemplate<String, boolean[]> booleanArrayRedisTemplate;
    private final DevBaseRegionMapper regionMapper;
    private final Key key;
    private final ScheduleTask scheduleTask;
    private final DListUtil dListUtil;
    private final MqttInit mqttInit;

    // 获取控制分区列表
    @RequestMapping("/getControlPartitionList")
    public R<List<ControlPartitionResp>> getControlPartitionList() {
        return R.ok(basicService.getControlPartitionList());
    }

    // 获取场景列表
    @RequestMapping("/getSceneList")
    public TableDataInfo<DevBaseSceneVo> getSceneList(@RequestBody DevBaseScenePageVo reqVo) {
        List<DevBaseScene> sceneList = baseSceneMapper.selectList();
        if (StringUtils.isNotBlank(reqVo.getName())) {
            sceneList = sceneList.stream().parallel().filter(item -> item.getName().contains(reqVo.getName())).collect(Collectors.toList());
        }
        List<DevBaseSceneVo> result = new ArrayList<>();
        if (sceneList != null && !sceneList.isEmpty()) {
            sceneList.forEach(item -> {
                DevBaseSceneVo vo = new DevBaseSceneVo();
                vo.setId(item.getId());
                vo.setSceneId(item.getSceneId());
                vo.setName(item.getName());
                result.add(vo);
            });
        }
        return key.getPageTable(result, reqVo.getPageNum(), reqVo.getPageSize());
    }

    // 获取场景列表 不分页
    @GetMapping("/getSceneList")
    public R<List<DevBaseSceneVo>> getSceneList() {
        List<DevBaseScene> sceneList = baseSceneMapper.selectList();
        List<DevBaseSceneVo> result = new ArrayList<>();
        int sceneSelect = 0;
        try {
            sceneSelect = Integer.parseInt((String) redisTemplate.opsForValue().get("zm:global:scene:select"));
        } catch (Exception ignored) {

        }
        int finalSceneSelect = sceneSelect;
        sceneList.forEach(item -> {
            DevBaseSceneVo vo = new DevBaseSceneVo();
            vo.setId(item.getId());
            vo.setSceneId(item.getSceneId());
            vo.setName(item.getName());
            vo.setEnabled(finalSceneSelect == vo.getId());
            result.add(vo);
        });
        return R.ok(result);
    }

    // 获取机柜信息 用于基本资料展示
    @SaIgnore
    @PostMapping("/getCabinetList")
    public R<TableDataInfo<BaseDeviceResp>> getCabinetList(@RequestBody PageQuery pageQuery) {
        DeviceCache cache = new DeviceCache(redisTemplate, shortArrayRedisTemplate, booleanArrayRedisTemplate,
            deviceMapper, dListUtil, regionMapper, key);
        List<BaseDeviceResp> result = basicService.getCabinetList(pageQuery);
        int id = 0;
        for (BaseDeviceResp row : result) {
            id++;
            row.setOrderNo(id);
            try {
                boolean typeB = this.key.getTelecommand(Math.toIntExact(row.getDeviceNo()))[817];
                row.setType(typeB ? 1 : 0);
            } catch (Exception ignored) {
                row.setType(0);
            }
            try {
                row.setRunMode(this.key.getTelecommand(Math.toIntExact(row.getDeviceNo()))[164] ? 1 : 0);
            } catch (Exception ignored) {
                row.setRunMode(0);
            }
            try {
                HashMap<String, Integer> map = cache.getDcDimNum(row.getDeviceNo());
                row.setDcModuleNum(map.get("dcModuleNum") == null ? 0 : map.get("dcModuleNum"));
                row.setDimmerNum(map.get("dimmerNum") == null ? 0 : map.get("dimmerNum"));
            } catch (Exception ignored) {
                row.setDcModuleNum(0);
                row.setDimmerNum(0);
            }
        }
        return R.ok(key.getPageTable(result, pageQuery));
    }

    /**
     * 获取报文列表
     */
    @PostMapping("/getInstructs")
    public TableDataInfo<DevWriteInstructVo> getInstructs(@RequestBody PageQuery q) {
        IPage<DevWriteInstructVo> p = devWriteInstructMapper
            .selectVoPage(q.build(), new LambdaQueryWrapper<DevWriteInstruct>()
                .orderByDesc(DevWriteInstruct::getTimestamp));
        return TableDataInfo.build(p);
    }

    // 告警记录，事件记录，报文记录
    @PostConstruct
    @Scheduled(fixedDelay = 88000)
    public void clearRecord() {
        long s = LocalDateTime.now().toLocalDate().minusDays(5).atStartOfDay().atZone(UTC).toEpochSecond();
        devWriteInstructMapper.delete(new LambdaQueryWrapper<DevWriteInstruct>().le(DevWriteInstruct::getTimestamp, s * 1000));
        faultRecordMapper.delete(new LambdaQueryWrapper<DevFaultRecord>().le(DevFaultRecord::getStime, s));
    }
}
