package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeControlReqVo {
    private Integer deviceId;
    private Integer controlId;
    private Integer pageSize;
    private Integer pageNum;
}
