package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaCheckPermission;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.AcSwitchCache;
import com.ruoyi.cache.Key;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.flag.DeviceFlag;
import com.ruoyi.init.MqttInit;
import com.ruoyi.schedule.ScheduleTask;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBaseRegion;
import com.ruoyi.zm.domain.bo.DevBaseDeviceBo;
import com.ruoyi.zm.domain.bo.DevBaseDeviceReqBo;
import com.ruoyi.zm.domain.vo.DevBaseDeviceVo;
import com.ruoyi.zm.domain.vo.DeviceOtherMsgRespVo;
import com.ruoyi.zm.domain.vo.PageWithIdReqVo;
import com.ruoyi.zm.domain.vo.TimeControlAcRespVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBaseRegionMapper;
import com.ruoyi.zm.service.IDevBaseDeviceService;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 机柜
 *
 * @author mophi
 * @date 2024-07-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/baseDevice")
public class DevBaseDeviceController extends BaseController {

    private final IDevBaseDeviceService iDevBaseDeviceService;
    private final DevBaseDeviceMapper baseMapper;
    private final DevBaseRegionMapper regionMapper;
    private final AcSwitchCache acSwitchCache;
    private final Key key;

    /**
     * 查询机柜列表
     */
    @SaCheckPermission("zm:baseDevice:list")
    @PostMapping("/list")
    public TableDataInfo<DevBaseDeviceVo> list(DevBaseDeviceBo bo, @RequestBody PageQuery pageQuery) throws ModbusTransportException {
        return iDevBaseDeviceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出机柜列表
     */
    @SaCheckPermission("zm:baseDevice:export")
    // @Log(title = "机柜", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevBaseDeviceBo bo, HttpServletResponse response) {
        List<DevBaseDeviceVo> list = iDevBaseDeviceService.queryList(bo);
        ExcelUtil.exportExcel(list, "机柜", DevBaseDeviceVo.class, response);
    }

    /**
     * 获取机柜详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zm:baseDevice:query")
    @GetMapping("/{id}")
    public R<DevBaseDeviceVo> getInfo(@NotNull(message = "主键不能为空")
                                      @PathVariable Long id) {
        DevBaseDeviceVo devBaseDeviceVo = iDevBaseDeviceService.queryById(id);
        devBaseDeviceVo.setDeviceNo(Long.valueOf(devBaseDeviceVo.getDeviceId()));
        return R.ok(devBaseDeviceVo);
    }

    /**
     * 初始化机柜
     */
    @GetMapping("/init")
    public R<Void> init(Integer deviceId) {
        // cabinetInitUtil.initCabinetData(deviceId);
        return R.ok("初始化指令已下发");
    }

    /**
     * 机柜设置-其他设置-机柜信息与通讯参数
     */
    @GetMapping("/otherMsg")
    public R<DeviceOtherMsgRespVo> otherMsg(Integer deviceId) {
        LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseDevice::getDeviceNo, deviceId);
        DevBaseDevice devBaseDevice = baseMapper.selectOne(lqw);
        DeviceOtherMsgRespVo vo = new DeviceOtherMsgRespVo();
        vo.setName(devBaseDevice.getDeviceName());
        vo.setDeviceId(Math.toIntExact(devBaseDevice.getId()));
        vo.setIp(devBaseDevice.getIp());
        vo.setHost(devBaseDevice.getDeviceId());
        vo.setZoneId(Math.toIntExact(devBaseDevice.getRegionId()));
        LambdaQueryWrapper<DevBaseRegion> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(DevBaseRegion::getId, devBaseDevice.getRegionId());
        DevBaseRegion region = regionMapper.selectOne(lqw1);
        vo.setArea(region.getName());
        return R.ok(vo);
    }

    /**
     * 机柜设置-交流开关 列表
     */
    @PostMapping("/acSwitchControlList")
    public TableDataInfo<TimeControlAcRespVo> acSwitchList(@RequestBody PageWithIdReqVo reqVo) {
        TableDataInfo<TimeControlAcRespVo> d = acSwitchCache.acSwitchList(reqVo);
        if (d != null) return d;

        List<TimeControlAcRespVo> result = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            TimeControlAcRespVo vo = new TimeControlAcRespVo();
            vo.setDeviceId(reqVo.getDeviceId());
            vo.setNo(i + 1);
            vo.setStatus(1);
            vo.setEnabled(1);
            vo.setStime("00:00");
            vo.setEtime("00:00");
            result.add(vo);
        }
        return key.getPageTable(result, reqVo.getPageNum(), reqVo.getPageSize());
    }
}
