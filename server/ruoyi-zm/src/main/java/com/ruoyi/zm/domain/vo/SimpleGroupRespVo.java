package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleGroupRespVo {
    private Integer id;
    private Integer groupId;
    private Integer selectStatus;
    private String groupName;
}
