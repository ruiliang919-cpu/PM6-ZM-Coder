package com.ruoyi.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseScene;
import com.ruoyi.zm.domain.DevConfigTimeControlScene;
import com.ruoyi.zm.domain.vo.ControlEnabledRespVo;
import com.ruoyi.zm.domain.vo.DevConfigTimeControlSceneRespVo;
import com.ruoyi.zm.domain.vo.TimeControlReqVo;
import com.ruoyi.zm.mapper.DevBaseSceneMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class SceneControlCache {
    public static final String[] SCENE_PARAMS_ADDR_ARR = new String[]{"0XAAEE", "0XAB2E", "0XAB6E"};
    private final Key key;
    private final DevBaseSceneMapper sceneMapper;
    private final SceneCache sceneCache;

    // 根据设备ID与场景时控ID获取场景时控模式是否启用
    public R<ControlEnabledRespVo> getSceneEnabled(Integer deviceId, Integer controlId) {
        try {
            String addr = "0XAAEEEnabled";
            Map<String, Integer> m = (Map<String, Integer>) key.getRemote(deviceId, addr);
            ControlEnabledRespVo vo = new ControlEnabledRespVo();
            vo.setDeviceId(deviceId);
            vo.setControlId(controlId);
            vo.setEnabled(Objects.equals(m.getOrDefault("data3", 0), controlId));
            return R.ok(vo);
        } catch (Exception e) {
            //            log.error("SceneControlCache → getSceneEnabled", e);
        }
        return null;
    }

    // 查询场景模式列表（包含参数引用）
    public TableDataInfo<DevConfigTimeControlSceneRespVo> getSceneList(TimeControlReqVo vo) {
        try {
            List<DevConfigTimeControlSceneRespVo> source = new ArrayList<>();
            List<DevConfigTimeControlScene> sceneParamsList = (List<DevConfigTimeControlScene>) key.getRemote(vo.getDeviceId(), SCENE_PARAMS_ADDR_ARR[vo.getControlId() - 1]);
            // System.err.println("sceneParamsList：" + sceneParamsList);
            for (int i = 0; i < sceneParamsList.size(); i++) {
                DevConfigTimeControlSceneRespVo respVo = new DevConfigTimeControlSceneRespVo();
                respVo.setId(i + 1);
                respVo.setDeviceId(vo.getDeviceId());
                respVo.setTimeControlId(vo.getControlId());
                respVo.setTimeFrameId(sceneParamsList.get(i).getTimeFrameId());
                respVo.setEnabledStatus(sceneParamsList.get(i).getEnabledStatus());
                respVo.setSceneSelect(sceneParamsList.get(i).getSceneSelect());
                respVo.setSceneName(sceneParamsList.get(i).getSceneSelect() == 0 ? "未选中" : getSceneName(sceneParamsList.get(i).getSceneSelect(), vo.getDeviceId()));
                respVo.setStime(sceneParamsList.get(i).getStime());
                respVo.setEtime(sceneParamsList.get(i).getEtime());
                source.add(respVo);
            }
            return key.getPageTable(source, 1, 10);
        } catch (Exception e) {
            // log.error("SceneControlCache → getSceneList", e);
        }
        return null;
    }

    // 根据场景ID查询场景名称
    public String getSceneName(Integer id, Integer deviceId) {
        List<String> sceneNamesList = sceneCache.getSceneNamesList(deviceId);
        String name = sceneNamesList.get(id - 1);
        if (name != null && !("").equals(name)) {
            return name;
        }
        LambdaQueryWrapper<DevBaseScene> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseScene::getSceneId, id);
        DevBaseScene scene = sceneMapper.selectOne(lqw);
        if (scene != null && !("").equals(scene.getName())) {
            return scene.getName();
        }
        return "";
    }
}
