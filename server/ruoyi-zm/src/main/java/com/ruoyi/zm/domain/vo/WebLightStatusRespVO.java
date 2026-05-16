package com.ruoyi.zm.domain.vo;

import com.ruoyi.zm.domain.DevStatusAcLoop;
import com.ruoyi.zm.domain.DevStatusDccLoop;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Schema(description = "照明状态列表 Response VO")
@Data
@ToString(callSuper = true)
public class WebLightStatusRespVO {

    private Long id;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备通讯编号
     */
    private Long deviceNo;

    /**
     * 设备所在区域
     */
    private String regionName;

    /**
     * 直流模块个数
     */
    private Integer dcModuleNum;

    /**
     * 直流开灯数
     */
    private Long dcTurnOnNum;
    /**
     * 直流关灯数
     */
    private Long dcTurnOffNum;

    /**
     * 直流状态列表
     */
    private List<DevStatusDccLoop>  dcList;

    /**
     *交流模块回路数
     */
    private Integer acModuleNum;
    /**
     *交流开灯数
     */
    private Long acTurnOnNum;
    /**
     *交流关灯数
     */
    private Long acTurnOffNum;

    /**
     * 交流状态列表
     */
    private List<DevStatusAcLoop>  acList;
}
