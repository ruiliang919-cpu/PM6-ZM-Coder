package com.ruoyi.mqttwrite.inf;

import com.ruoyi.zm.domain.vo.InfraredParamsTable;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfValue {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "0XAE7A";
    private boolean enabled;
    private List<InfraredParamsTable> data;
    private Integer[] data1;

    public static Integer[] selectArr(Integer[] arr) {
        List<Integer> list = Arrays.asList(arr);
        Integer[] r = new Integer[16];
        IntStream.range(1, 17).forEach(i -> {
            if (list.contains(i)) r[i - 1] = 1;
            else r[i - 1] = 0;
        });
        return r;
    }

    public static int[] selectIntArr(int[] arr) {
        List<Integer> r = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 1) r.add(i + 1);
        }
        int[] ints = new int[r.size()];
        for (int i = 0; i < ints.length; i++) ints[i] = r.get(i);
        return ints;
    }

    public static Integer[] selectIntegerArr(Integer[] arr) {
        List<Integer> r = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 1) r.add(i + 1);
        }
        return r.toArray(new Integer[0]);
    }
}
