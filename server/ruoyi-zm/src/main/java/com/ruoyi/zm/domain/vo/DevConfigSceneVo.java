package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;



/**
 * 场景设置视图对象 dev_config_scene
 *
 * @author ruoyi
 * @date 2024-09-03
 */
@Data
@ExcelIgnoreUnannotated
public class DevConfigSceneVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Long id;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private Long deviceId;

    /**
     * 场景编号
     */
    @ExcelProperty(value = "场景编号")
    private Long sceneId;

    /**
     * 分组编号
     */
    @ExcelProperty(value = "分组编号")
    private Long groupId;

    /**
     * 亮度，亮度100
     */
    @ExcelProperty(value = "亮度，亮度100")
    private Long lux;

    /**
     * 开关类型；0-开/1-关
     */
    @ExcelProperty(value = "开关类型；0关；1开")
    private Integer btnStatus;

    /**
     * 分组信息 是否选中 0不选中 1选中
     */
    @ExcelProperty(value = "分组信息 是否选中 0不选中 1选中")
    private Integer selectStatus;


}
