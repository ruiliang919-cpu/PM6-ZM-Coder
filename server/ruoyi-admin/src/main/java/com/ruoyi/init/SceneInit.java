package com.ruoyi.init;

import com.ruoyi.zm.domain.DevBaseScene;
import com.ruoyi.zm.mapper.DevBaseSceneMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SceneInit {
    private static final List<DevBaseScene> INIT_LIST = Arrays.asList(
        new DevBaseScene(1L, 1, "高峰模式"),
        new DevBaseScene(2L, 2, "平峰模式"),
        new DevBaseScene(3L, 3, "清扫模式"),
        new DevBaseScene(4L, 4, "全亮模式"),
        new DevBaseScene(5L, 5, "停运模式"),
        new DevBaseScene(6L, 6, "节能模式1"),
        new DevBaseScene(7L, 7, "节能模式2"),
        new DevBaseScene(8L, 8, "场景08"),
        new DevBaseScene(9L, 9, "场景09"),
        new DevBaseScene(10L, 10, "场景10")
    );
    private final DevBaseSceneMapper devBaseSceneMapper;
    @Value("${init.sceneInit:false}")
    public boolean initFlag;

    @PostConstruct
    public void init() {
        if (initFlag) {
            devBaseSceneMapper.delete();
            devBaseSceneMapper.insertBatch(INIT_LIST);
        }
    }
}
