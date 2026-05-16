package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 普通时控启用对象 dev_base_simple_control
 *
 * @author ruoyi
 * @date 2024-09-09
 */
@Data

@TableName("dev_base_simple_control")
public class DevBaseSimpleControl {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Integer id;
    /**
     * 设备ID
     */
    private Integer deviceId;
    /**
     * 普通时控ID
     */
    private Integer controlId;
    /**
     * 是否启用 0不启用 1已启用
     */
    private Integer enabled;

}
