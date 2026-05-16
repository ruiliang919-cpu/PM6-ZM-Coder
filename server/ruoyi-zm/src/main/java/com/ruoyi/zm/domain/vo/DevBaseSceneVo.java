package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;



/**
 * 场景视图对象 dev_base_scene
 *
 * @author mophi
 * @date 2024-08-26
 */
@Data
@ExcelIgnoreUnannotated
public class DevBaseSceneVo {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Long id;

    /**
     * 场景编号
     */
    @ExcelProperty(value = "场景编号")
    private Integer sceneId;

    /**
     * 场景名称
     */
    @ExcelProperty(value = "场景名称")
    private String name;

    // /**
    //  * 排序字段
    //  */
    // private Integer orderNo;

    private Boolean enabled;
}
