package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 交流信息业务对象 dev_status_ac
 *
 * @author ruoyi
 * @date 2024-09-19
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevStatusAcBo extends BaseEntity {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备id
     */
    @NotNull(message = "设备id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deviceId;

    /**
     * 电路数：1路；2路
     */
    @NotNull(message = "电路数：1路；2路不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long circuitNum;

    /**
     * AB线电压
     */
    @NotNull(message = "AB线电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal uab;

    /**
     * BC线电压
     */
    @NotNull(message = "BC线电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal ubc;

    /**
     * CA线电压
     */
    @NotNull(message = "CA线电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal uac;

    /**
     * A相电流
     */
    @NotNull(message = "A相电流不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal ia;

    /**
     * B相电流
     */
    @NotNull(message = "B相电流不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal ib;

    /**
     * C相电流
     */
    @NotNull(message = "C相电流不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal ic;


}
