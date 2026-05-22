package com.ruoyi.mqttwrite.scene;

import com.ruoyi.zm.domain.vo.SceneControlTable;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneValue {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "0XAAEE";
    private int controlId;
    private boolean enabled;
    private List<SceneControlTable> data;
}
