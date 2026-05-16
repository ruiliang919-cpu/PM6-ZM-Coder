package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchReqVo {
    private Integer slaveId;
    private Integer pageSize;
    private Integer pageNum;
}
