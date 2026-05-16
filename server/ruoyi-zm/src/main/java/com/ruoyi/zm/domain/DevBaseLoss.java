package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 总功率对象 dev_base_loss
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dev_base_loss")
public class DevBaseLoss implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 设备ID
     */
    private Integer deviceId;
    /**
     * 总功率
     */
    private BigDecimal loss;

}
