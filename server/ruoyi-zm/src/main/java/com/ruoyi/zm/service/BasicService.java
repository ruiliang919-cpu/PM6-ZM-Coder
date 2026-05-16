package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.zm.domain.bo.BaseDeviceReqBo;
import com.ruoyi.zm.domain.vo.BaseDeviceResp;
import com.ruoyi.zm.domain.vo.ControlPartitionResp;
import com.ruoyi.zm.domain.vo.DevBaseSceneVo;

import java.util.List;

/**
 * 基本资料业务层接口
 */
public interface BasicService {
    List<ControlPartitionResp> getControlPartitionList();

    List<DevBaseSceneVo> getSceneList();

    List<BaseDeviceResp> getCabinetList(PageQuery pageQuery);

    boolean addCabinet(BaseDeviceReqBo bo);

}
