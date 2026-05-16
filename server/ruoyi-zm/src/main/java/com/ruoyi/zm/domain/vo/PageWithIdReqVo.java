package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageWithIdReqVo {
    private Integer deviceId;
    private Integer pageSize;
    private Integer pageNum;
}
