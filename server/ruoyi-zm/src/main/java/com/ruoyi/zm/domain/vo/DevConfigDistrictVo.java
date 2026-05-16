package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;



/**
 * 分区配置视图对象 dev_config_district
 *
 * @author ruoyi
 * @date 2024-08-26
 */
@Data
@ExcelIgnoreUnannotated
public class DevConfigDistrictVo {

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
     * 分区编号ID（实际表示分组ID）分区编号 1 表示 第一分组
     */
    @ExcelProperty(value = "分区编号ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "实=际表示分组ID")
    private Long districtId;

    /**
     * 分区地址 即分组所在的区域地址 若为 5 表示 分区编号所在的地区为 5，若分区编号为 1 ，则表示第一分组所在的区域为 5
     */
    @ExcelProperty(value = "分区地址 即分组所在的区域地址 若为 5 表示 分区编号所在的地区为 5，若分区编号为 1 ，则表示第一分组所在的区域为 5")
    private Integer addr;

    /**
     * 名称（不显示，因为分区即是分组，分组已有名称，所以分区不需要名称）
     */
    @ExcelProperty(value = "名称", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "不=显示，因为分区即是分组，分组已有名称，所以分区不需要名称")
    private String name;


}
