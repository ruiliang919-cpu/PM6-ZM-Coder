package com.ruoyi.web.controller.zm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.SceneCache;
import com.ruoyi.cache.SceneControlCache;
import com.ruoyi.cache.SimpleControlCache;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBaseScene;
import com.ruoyi.zm.domain.DevConfigTimeControl;
import com.ruoyi.zm.domain.bo.DevConfigTimeControlAcBo;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBaseSceneMapper;
import com.ruoyi.zm.mapper.DevConfigTimeControlMapper;
import com.ruoyi.zm.service.IDevConfigTimeControlAcService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// 时控模式控制层
@RestController
@RequestMapping("/zm/timeControl")
@RequiredArgsConstructor
public class TimeControlController {
    private final IDevConfigTimeControlAcService timeControlAcService;
    private final DevBaseDeviceMapper deviceMapper;
    private final DevBaseSceneMapper sceneMapper;
    private final SimpleControlCache simpleControlCache;
    private final SceneControlCache sceneControlCache;
    private final Key key;
    private final SceneCache sceneCache;


    // 查询普通模式列表 根据设备ID
    @GetMapping("/simpleList")
    public R<?> getSimpleList(Integer deviceId, Integer controlId) {
        R<List<DevConfigTimeControlRespVo>> simpleList = simpleControlCache.getSimpleList(deviceId, controlId);
        if (simpleList != null) return simpleList;
        List<DevConfigTimeControlRespVo> result = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            DevConfigTimeControlRespVo respVo = new DevConfigTimeControlRespVo();
            respVo.setControlId(controlId);
            respVo.setFrameId(i + 1);
            respVo.setLux(0);
            respVo.setSwitchStatus(0);
            respVo.setEnabledStatus(0);
            respVo.setStime("00:00");
            respVo.setEtime("00:00");
            result.add(respVo);
        }
        return R.ok(result);
    }

    // 查询交流开关模式列表
    @PostMapping("/acSwitchList")
    public TableDataInfo<?> getacSwitchList(@RequestBody TimeControlReqVo vo) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(vo.getPageNum());
        pageQuery.setPageSize(vo.getPageSize());
        DevConfigTimeControlAcBo bo = new DevConfigTimeControlAcBo();
        bo.setDeviceId(vo.getDeviceId());
        return timeControlAcService.queryPageList(bo, pageQuery);
    }

    // 查询场景模式列表（包含参数引用）
    @PostMapping("/sceneList")
    public TableDataInfo<DevConfigTimeControlSceneRespVo> getSceneList(@RequestBody TimeControlReqVo vo) {
        TableDataInfo<DevConfigTimeControlSceneRespVo> sceneList = sceneControlCache.getSceneList(vo);
        if (sceneList != null) return sceneList;

        List<DevConfigTimeControlSceneRespVo> result = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            DevConfigTimeControlSceneRespVo respVo = new DevConfigTimeControlSceneRespVo();
            respVo.setId(i + 1);
            respVo.setDeviceId(vo.getDeviceId());
            respVo.setTimeControlId(vo.getControlId());
            respVo.setTimeFrameId(i + 1);
            respVo.setEnabledStatus(0);
            respVo.setStime("00:00");
            respVo.setEtime("00:00");
            respVo.setSceneSelect(0);
            respVo.setSceneName("未选中");
            result.add(respVo);
        }
        return key.getPageTable(result, 1, 100);
    }

    private final DevConfigTimeControlMapper devConfigTimeControlMapper;

    // 获取普通时控模式的下拉框
    @GetMapping("/simpleListSelect")
    public R<?> getSimpleListSelect(Integer deviceId) {
        List<TimeControlSelectRespVo> result = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            TimeControlSelectRespVo vo = new TimeControlSelectRespVo();
            vo.setId(i + 1);
            vo.setName("普通时控0" + (i + 1));
            result.add(vo);
        }
        return R.ok(result);
    }

    // 获取场景时控模式的下拉框
    @GetMapping("/sceneListSelect")
    public R<?> getSceneListSelect(Integer deviceId) {
        List<TimeControlSelectRespVo> result = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            TimeControlSelectRespVo vo = new TimeControlSelectRespVo();
            vo.setId(i + 1);
            vo.setName("场景时控0" + (i + 1));
            result.add(vo);
        }
        return R.ok(result);
    }

    // 获取参数引用的机柜列表（普通+场景）
    @GetMapping("/getCabinetList")
    public R<?> getCabinetList() {
        LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(DevBaseDevice::getDeviceNo);
        List<DevBaseDevice> source = deviceMapper.selectList();
        List<DeviceSelectListRespVo> result = new LinkedList<>();
        source.forEach(item -> {
            DeviceSelectListRespVo vo = new DeviceSelectListRespVo();
            vo.setId(Math.toIntExact(item.getDeviceNo()));
            vo.setName(item.getDeviceName());
            result.add(vo);
        });
        return R.ok(result);
    }

    // 普通时控参数引用点击确定后得到的时序表数据
    // 传入设备ID与时控ID
    @GetMapping("/simpleTimeScale")
    public R<?> simpleTimeScale(Integer deviceId, Integer controlId) {
        R<List<DevConfigTimeControlRespVo>> simpleList = simpleControlCache.getSimpleList(deviceId, controlId);
        if (simpleList != null) return simpleList;
        LambdaQueryWrapper<DevConfigTimeControl> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevConfigTimeControl::getDeviceId, deviceId);
        lqw.eq(DevConfigTimeControl::getTimeControlId, controlId);
        lqw.orderByAsc(DevConfigTimeControl::getTimeFrameId);
        List<DevConfigTimeControl> source = devConfigTimeControlMapper.selectList(lqw);
        if (source.isEmpty()) {
            return R.ok("该模式下暂无数据", null);
        }
        LinkedList<DevConfigTimeControlRespVo> result = new LinkedList<>();
        source.forEach(item -> {
            DevConfigTimeControlRespVo vo = new DevConfigTimeControlRespVo();
            vo.setControlId(item.getTimeControlId());
            vo.setFrameId(item.getTimeFrameId());
            vo.setLux(item.getLux());
            vo.setSwitchStatus(item.getSwitchStatus());
            vo.setEnabledStatus(item.getEnabledStatus());
            vo.setStime(item.getStime());
            vo.setEtime(item.getEtime());
            result.add(vo);
        });
        return R.ok(result);
    }

    // 机柜设置-时控模式-普通时控模式参数-分组选择
    @PostMapping("/simpleGroupList")
    public TableDataInfo<?> simpleGroupList(@RequestBody TimeControlReqVo vo) {
        TableDataInfo<?> tableDataInfo = simpleControlCache.simpleGroupList(vo);
        if (tableDataInfo != null) return tableDataInfo;

        List<SimpleGroupRespVo> result = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            SimpleGroupRespVo respVo = new SimpleGroupRespVo();
            respVo.setId(i + 1);
            respVo.setGroupId(i + 1);
            respVo.setGroupName("分组" + (i + 1));
            respVo.setSelectStatus(0);
            result.add(respVo);
        }
        return key.getPageTable(result, 1, 16);

    }


    // 根据设备ID与普通时控ID获取普通时控模式是否启用
    @GetMapping("/getSimpleEnabled")
    public R<ControlEnabledRespVo> getSimpleEnabled(Integer deviceId, Integer controlId) {
        R<ControlEnabledRespVo> simpleEnabled = simpleControlCache.getSimpleEnabled(deviceId, controlId);
        if (simpleEnabled != null) return simpleEnabled;
        ControlEnabledRespVo result = new ControlEnabledRespVo();
        result.setDeviceId(deviceId);
        result.setControlId(controlId);
        result.setEnabled(false);
        return R.ok(result);
    }

    // 根据设备ID与场景时控ID获取场景时控模式是否启用
    @GetMapping("/getSceneEnabled")
    public R<ControlEnabledRespVo> getSceneEnabled(Integer deviceId, Integer controlId) {
        R<ControlEnabledRespVo> sceneEnabled = sceneControlCache.getSceneEnabled(deviceId, controlId);
        if (sceneEnabled != null) return sceneEnabled;
        ControlEnabledRespVo result = new ControlEnabledRespVo();
        result.setDeviceId(deviceId);
        result.setControlId(controlId);
        result.setEnabled(false);
        return R.ok(result);
    }

    // 查询场景列表，不分页
    @GetMapping("/getSceneList")
    public R<List<BaseSceneRespVo>> getSceneList() {
        List<DevBaseScene> devBaseScenes = sceneMapper.selectList();
        List<BaseSceneRespVo> result = new LinkedList<>();
        if (devBaseScenes != null && !devBaseScenes.isEmpty()) {
            devBaseScenes.forEach(item -> {
                BaseSceneRespVo vo = new BaseSceneRespVo();
                vo.setSceneId(item.getSceneId());
                vo.setName(item.getName());
                result.add(vo);
            });
        }
        return R.ok(result);
    }

    // 查询场景列表，不分页
    @GetMapping("/getModuleSceneList")
    public R<List<BaseSceneRespVo>> getModuleSceneList(Integer deviceId) {
        List<String> sceneNamesList = sceneCache.getSceneNamesList(deviceId);
        List<DevBaseScene> devBaseScenes = sceneMapper.selectList();
        List<BaseSceneRespVo> result = new LinkedList<>();
        if (sceneNamesList != null && !sceneNamesList.isEmpty()) {
            for (int i = -1; i < 10; i++) {
                BaseSceneRespVo vo = new BaseSceneRespVo();
                vo.setSceneId(i + 1);
                if (i == -1) {
                    vo.setName("未选中");
                } else {
                    try {
                        vo.setName(sceneNamesList.get(i));
                    } catch (Exception e) {
                        try {
                            vo.setName(devBaseScenes.get(i).getName());
                        } catch (Exception f) {
                            vo.setName("场景" + (i + 1));
                        }
                    }
                }
                result.add(vo);
            }
        }
        return R.ok(result);
    }
}
