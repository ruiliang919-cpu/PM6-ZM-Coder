package com.ruoyi.mqttwrite.ill;

import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IllOutValue {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "0XB710";
    private D data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class D {
        private int no;
        private int value;
    }

    public static IllOutValue build(int no, int value) {
        IllOutValue v = new IllOutValue();
        IllOutValue.D d = new IllOutValue.D();
        d.setNo(no);
        d.setValue(value);
        v.setData(d);
        return v;
    }
}
