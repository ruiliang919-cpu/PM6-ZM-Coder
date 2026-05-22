package com.ruoyi.mqttwrite.scene;

import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneParams {
    private Long msgId = IdGenerator.UUIDId();
    private List<D> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class D {
        private int sceneId;
        private Integer[] groupIds;
        private Integer[] luxArr;
        private Boolean[] switchArr;
    }

    public static Boolean[] getB(Integer[] arr) {
        Boolean[] b = new Boolean[arr.length];
        for (int i = 0; i < arr.length; i++)
            b[i] = arr[i] == 1;
        return b;
    }

    public static Integer[] getG(Integer[] arr) {
        int[] b = new int[16];
        Integer[] c = new Integer[16];
        for (int i = 0; i < arr.length; i++) b[i] = arr[i];
        for (int i = 0; i < 16; i++) c[i] = b[i];
        return c;
    }
}
