package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;



/**
 * 遥信协议视图对象 dev_protocol_01
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Data
@ExcelIgnoreUnannotated
public class DevProtocol01Vo {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址")
    private String addr;

    /**
     * 名称
     */
    @ExcelProperty(value = "名称")
    private String name;

    /**
     * 倍率
     */
    @ExcelProperty(value = "倍率")
    private Long magnification;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    private String unit;

    /**
     * 读写属性
     */
    @TableField("`read_write`")
    @ExcelProperty(value = "读写属性")
    private String readWrite;

    /**
     * 说明
     */
    @ExcelProperty(value = "说明")
    private String memo;


}
