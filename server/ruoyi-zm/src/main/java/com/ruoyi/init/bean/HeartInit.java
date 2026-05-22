package com.ruoyi.init.bean;

import cn.hutool.json.JSONUtil;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeartInit {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "Heart";
    private int deviceNo;

    public HeartInit(int deviceNo) {
        this.deviceNo = deviceNo;
    }

    public static void main(String[] args) {
        HeartInit heartInit = new HeartInit(10);
        System.out.println(JSONUtil.toJsonPrettyStr(heartInit));
    }
}
