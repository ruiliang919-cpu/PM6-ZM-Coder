package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serializable;



/**
 * 场景模式-时控配置视图对象 dev_config_time_control_scene
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data
public class DevConfigTimeControlSceneVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Integer id;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private Integer deviceId;

    /**
     * 时控编号
     */
    @ExcelProperty(value = "时控编号")
    private Integer timeControlId;

    /**
     * 时段信息编号
     */
    @ExcelProperty(value = "时段信息编号")
    private Integer timeFrameId;

    /**
     * 使能（启用状态）；0-使能（启用）/1-禁止
     */
    @ExcelProperty(value = "使能", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "启=用状态")
    private Integer enabledStatus;

    /**
     * 设置“1”则选择场景01：1-10对应场景1-10
     */
    @ExcelProperty(value = "设置“1”则选择场景01：1-10对应场景1-10")
    private Integer sceneSelect;

    /**
     * 开始时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    @ExcelProperty(value = "开始时间 ASCII字符串长度6个字节，示例：”12:00“")
    private String stime;

    /**
     * 结束时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    @ExcelProperty(value = "结束时间 ASCII字符串长度6个字节，示例：”12:00“")
    private String etime;

    private Integer enabled;
}
