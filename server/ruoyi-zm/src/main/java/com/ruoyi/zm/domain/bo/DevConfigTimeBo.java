package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 机柜时间设置业务对象 dev_config_time
 *
 * @author ruoyi
 * @date 2024-08-26
 */

@Data
public class DevConfigTimeBo {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 设备通讯编号
     */
    @NotNull(message = "设备通讯编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer deviceNo;

    /**
     * 年
     */
    @NotNull(message = "年不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer year;

    /**
     * 月
     */
    @NotNull(message = "月不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer month;

    /**
     * 日
     */
    @NotNull(message = "日不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer day;

    /**
     * 时
     */
    @NotNull(message = "时不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer hour;

    /**
     * 分
     */
    @NotNull(message = "分不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer minute;

    /**
     * 秒
     */
    @NotNull(message = "秒不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer second;

    /**
     * 对时标志位 0不启用 1启用
     */
    @NotNull(message = "对时标志位 0不启用 1启用不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer timeSwitch;


}
