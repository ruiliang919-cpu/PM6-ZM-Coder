package com.ruoyi.web.controller.zm;

import com.ruoyi.cache.Key;
import com.ruoyi.cache.SceneCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.zm.domain.DevBaseScene;
import com.ruoyi.zm.domain.vo.SceneNameVo;
import com.ruoyi.zm.mapper.DevBaseSceneMapper;
import com.ruoyi.zm.service.IDevBaseSceneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/scene")
public class SceneController {

    private final IDevBaseSceneService iDevBaseSceneService;
    private final DevBaseSceneMapper sceneMapper;
    private final SceneCache sceneCache;

    // 获取场景名称列表s
    @GetMapping("/getSceneNames")
    public R<List<SceneNameVo>> getSceneNames() {
        List<DevBaseScene> scenes = sceneMapper.selectList();
        List<SceneNameVo> result = new ArrayList<>();
        AtomicInteger id = new AtomicInteger();
        scenes
            .stream()
            .filter(Key.distinctByKey(DevBaseScene::getName))
            .forEach(item -> {
                SceneNameVo vo = new SceneNameVo();
                vo.setId(id.getAndIncrement());
                vo.setName(item.getName());
                result.add(vo);
            });
        return R.ok(result);
    }


}
