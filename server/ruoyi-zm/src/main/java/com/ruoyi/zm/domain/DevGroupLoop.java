package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 分组回路编号对象 dev_group_loop
 *
 * @author Lion Li
 * @date 2024-08-20
 */
@Data
@TableName("dev_group_loop")
public class DevGroupLoop {

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
     * 分组ID
     */
    private Long groupId;

    /**
     * 分组回路编号
     */
    private String loopNo;


}
