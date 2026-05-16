package com.ruoyi.zm.domain.vo;

import lombok.Data;

@Data
public class DevConfigTimeControlSceneRespVo {

    private Integer id;

    /**
     * 设备编号
     */

    private Integer deviceId;

    /**
     * 时控编号
     */

    private Integer timeControlId;

    /**
     * 时段信息编号
     */

    private Integer timeFrameId;

    /**
     * 使能（启用状态）；0-使能（启用）/1-禁止
     */

    private Integer enabledStatus;

    /**
     * 设置“1”则选择场景01：1-10对应场景1-10
     */

    private Integer sceneSelect;

    /**
     * 场景选择对应的场景名称
     */
    private String sceneName;

    /**
     * 开始时间 ASCII字符串长度6个字节，示例：”12:00“
     */

    private String stime;

    /**
     * 结束时间 ASCII字符串长度6个字节，示例：”12:00“
     */

    private String etime;
}
