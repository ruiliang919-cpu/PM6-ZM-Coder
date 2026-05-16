package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 直流回路设定值
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DccSetting {
    // 回路编号
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    // 表示 亮度设定值/亮度反馈值
    private Integer value;
    // 表示 输出电压/输出电流/内部温度（摄氏度）/ 实时功率
    private Long longValue;
    // 表示 开关设定/开关反馈
    private String status;
}
