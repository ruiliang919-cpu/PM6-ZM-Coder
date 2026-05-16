package com.ruoyi.zm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "回路分组列表 Response VO")
@Data
@ToString(callSuper = true)
public class WebDccLoopGruopReqVO {
    // 设备ID
    private Long slaveId;
    // 分组ID
    private Long groupId;
}
