package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevDeviceRemoteReadBo;
import com.ruoyi.zm.domain.vo.DevDeviceRemoteReadVo;

import java.util.Collection;
import java.util.List;

/**
 * 交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应Service接口
 *
 * @author ruoyi
 * @date 2024-08-27
 */
public interface IDevDeviceRemoteReadService {

    /**
     * 查询交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应
     */
    DevDeviceRemoteReadVo queryById(Integer id);

    /**
     * 查询交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应列表
     */
    TableDataInfo<DevDeviceRemoteReadVo> queryPageList(DevDeviceRemoteReadBo bo, PageQuery pageQuery);

    /**
     * 查询交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应列表
     */
    List<DevDeviceRemoteReadVo> queryList(DevDeviceRemoteReadBo bo);

    /**
     * 新增交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应
     */
    Boolean insertByBo(DevDeviceRemoteReadBo bo);

    /**
     * 修改交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应
     */
    Boolean updateByBo(DevDeviceRemoteReadBo bo);

    /**
     * 校验并批量删除交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
