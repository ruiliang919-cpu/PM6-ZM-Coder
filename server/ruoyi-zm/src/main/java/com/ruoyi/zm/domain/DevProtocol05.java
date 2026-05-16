package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 遥控协议对象 dev_protocol_05
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Data
// @EqualsAndHashCode(callSuper = true)
@TableName("dev_protocol_05")
// public class DevProtocol05 extends BaseEntity {
public class DevProtocol05 implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 地址
     */
    private String addr;
    /**
     * 名称
     */
    private String name;
    /**
     * 倍率
     */
    private Long magnification;
    /**
     * 单位
     */
    private String unit;
    /**
     * 读写属性
     */
    @TableField("`read_write`")
    private String readWrite;
    /**
     * 说明
     */
    private String memo;

}
