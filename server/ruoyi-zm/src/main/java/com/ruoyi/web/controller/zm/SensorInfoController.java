package com.ruoyi.web.controller.zm;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigIlluminanceSensorBo;
import com.ruoyi.zm.domain.bo.DevConfigInfraredSensorBo;
import com.ruoyi.zm.domain.vo.DevConfigIlluminanceSensorVo;
import com.ruoyi.zm.domain.vo.DevConfigInfraredSensorVo;
import com.ruoyi.zm.service.IDevConfigIlluminanceSensorService;
import com.ruoyi.zm.service.IDevConfigInfraredSensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 机柜信息-传感器信息 控制层接口
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/sensor")
public class SensorInfoController {
    private final IDevConfigInfraredSensorService infraredService;
    private final IDevConfigIlluminanceSensorService illustrationSensorService;

    /**
     * 根据设备ID获取红外传感器列表
     */
    @GetMapping("/infraredList")
    public TableDataInfo<DevConfigInfraredSensorVo> infraredList(Integer deviceId) {
        DevConfigInfraredSensorBo bo = new DevConfigInfraredSensorBo();
        bo.setDeviceId(deviceId);
        return TableDataInfo.build(infraredService.queryList(bo));
    }

    /**
     * 根据设备ID获取照度传感器列表
     */
    @GetMapping("/illustrationList")
    public TableDataInfo<DevConfigIlluminanceSensorVo> illustrationList(Integer deviceId) {
        DevConfigIlluminanceSensorBo bo = new DevConfigIlluminanceSensorBo();
        bo.setDeviceId(deviceId);
        return TableDataInfo.build(illustrationSensorService.queryList(bo));
    }
}
