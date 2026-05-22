package com.ruoyi.mqttwrite.simple;

import com.ruoyi.zm.domain.vo.SimpleControlTable;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleValue {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "0XA80A";
    private int controlId;
    private boolean enabled;
    private List<SimpleControlTable> data1;
    private Integer[] data2;

    public static Integer[] selectArr(Integer[] arr) {
        List<Integer> list = Arrays.asList(arr);
        Integer[] r = new Integer[16];
        IntStream.range(1, 17).forEach(i -> {
           if (list.contains(i)) r[i - 1] = 1;
           else r[i - 1] = 0;
        });
        return r;
    }
}
