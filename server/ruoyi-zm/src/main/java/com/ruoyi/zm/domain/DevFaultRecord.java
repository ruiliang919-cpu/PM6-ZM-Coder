package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警记录对象 dev_fault_record
 *
 * @author ruoyi
 * @date 2024-09-04
 */
@Data

@TableName("dev_fault_record")
public class DevFaultRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Integer id;
    /**
     * 设备编号 主机号
     */
    private Integer deviceId;
    /**
     * 告警/事件信息
     */
    private String message;
    /**
     * 开始时间
     */
    private Long stime;
    /**
     * 是否显示 0不显示 1显示
     */
    private Integer showType;
    /**
     * 类型 1告警记录 2事件记录
     */
    private Integer type;

}
