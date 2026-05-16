package com.ruoyi.cache;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigScene;
import com.ruoyi.zm.domain.vo.DevBaseSceneSelectListVo;
import com.ruoyi.zm.domain.vo.SceneParamsListReqVo;
import com.ruoyi.zm.domain.vo.SceneParamsListRespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SceneCache {
    public static final String[] SCENE_PARAMS_ADDR_ARR = new String[]{
        "0xAFC2", "0xAFE3", "0xB004", "0xB025", "0xB046",
        "0xB067", "0xB088", "0xB0A9", "0xB0CA", "0xB0EB"};
    public static final String[] SCENE_NAME_ADDR_ARR = new String[]{
        "0xA483", "0xA48D", "0xA497", "0xA4A1", "0xA4AB",
        "0xA4B5", "0xA4BF", "0xA4C9", "0xA4D3", "0xA4DD"};
    public static final String[] SCENE_NAME_ARR = {
        "系统设置-场景设置-场景命名01_01",
        "系统设置-场景设置-场景命名02_01",
        "系统设置-场景设置-场景命名03_01",
        "系统设置-场景设置-场景命名04_01",
        "系统设置-场景设置-场景命名05_01",
        "系统设置-场景设置-场景命名06_01",
        "系统设置-场景设置-场景命名07_01",
        "系统设置-场景设置-场景命名08_01",
        "系统设置-场景设置-场景命名09_01",
        "系统设置-场景设置-场景命名10_01"
    };
    public static final String[] ADDR_SCENE = {
        "0XAAEE",
        "0XAB2E",
        "0XAB6E"
    };
    private final Key key;
    private final LoopByGroupCache loopByGroupCache;
    private final StringRedisTemplate stringRedisTemplate;


    // 场景设置-场景参数
    public TableDataInfo<SceneParamsListRespVo> getSceneParamsList(SceneParamsListReqVo reqVo) {
        try {
            List<SceneParamsListRespVo> source = new ArrayList<>();
            List<DevConfigScene> sceneParams = (List<DevConfigScene>) key.getRemote(reqVo.getDeviceId(), SCENE_PARAMS_ADDR_ARR[reqVo.getSceneId() - 1]);
            // System.out.println("sceneParams：" + sceneParams);
            List<String> groupNames = loopByGroupCache.getGroupNames(reqVo.getDeviceId());
            for (int i = 0; i < 16; i++) {
                SceneParamsListRespVo respVo = new SceneParamsListRespVo();
                respVo.setGroupId(i + 1);
                try {
                    respVo.setGroupName(groupNames.get(i));
                } catch (Exception e) {
                    respVo.setGroupName("分组" + (i + 1));
                }
                try {
                    respVo.setSelectStatus(sceneParams.get(i).getSelectStatus());
                    respVo.setLux(sceneParams.get(i).getLux());
                    respVo.setBtnStatus(sceneParams.get(i).getBtnStatus());
                } catch (Exception e) {
                    respVo.setSelectStatus(0);
                    respVo.setLux(0L);
                    respVo.setBtnStatus(0);
                }
                source.add(respVo);
            }
            return key.getPageTable(source, reqVo.getPageNum(), 16);
        } catch (Exception e) {
            log.error("SceneCache → getSceneParamsList", e);
        }
        return null;
    }

    // 设备ID获取场景名称列表
    public List<String> getSceneNamesList(Integer deviceId) {
        List<String> sceneNames = new ArrayList<>();
        String ip = key.getCreateTCP(deviceId).getIp();
        for (int i = 0; i < SCENE_NAME_ADDR_ARR.length; i++) {
            String name = stringRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + ip + ":" + deviceId + ":" + SCENE_NAME_ADDR_ARR[i]);
            if (name == null || name.isEmpty()) {
                sceneNames.add("场景" + (i + 1));
            } else {
                if (name.startsWith("\"")) name = name.substring(1, name.length() - 1);
                sceneNames.add(name);
            }
        }
        return sceneNames;
    }

    // 获取场景设置 ID+名称
    public R<List<DevBaseSceneSelectListVo>> getSceneSelectList(Integer deviceId) {
        List<DevBaseSceneSelectListVo> result = new ArrayList<>();
        List<String> sceneNamesList = getSceneNamesList(deviceId);
        for (int i = 0; i < 10; i++) {
            DevBaseSceneSelectListVo vo = new DevBaseSceneSelectListVo();
            vo.setId(i + 1);
            vo.setName(sceneNamesList.get(i));
            result.add(vo);
        }
        return R.ok(result);
    }
}
