package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class Protocol01ExcelImportVo {
    /**
     * 地址
     */
    @ExcelProperty(value = "位地址 (十六进制)")
    private String addr;

    /**
     * 名称
     */
    @ExcelProperty(value = "名称")
    private String name;

    /**
     * 倍率
     */
    @ExcelProperty(value = "值解析")
    private Long magnification;

    /**
     * 说明
     */
    @ExcelProperty(value = "说明")
    private String memo;
}
