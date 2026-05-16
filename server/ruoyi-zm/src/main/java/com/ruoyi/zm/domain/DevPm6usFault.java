package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * PM6US开关故障记录对象 dev_pm6us_fault
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data

@TableName("dev_pm6us_fault")
public class DevPm6usFault implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备编号
     */
    private Long deviceId;

    /**
     * PM6US开关故障编号
     */
    private Long switchNo;

    /**
     * 故障判定 0：正常 1：故障
     */
    private Long faultDecide;


}
