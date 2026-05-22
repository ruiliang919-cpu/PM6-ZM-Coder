package com.ruoyi.web.controller.device;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.flag.DeviceFlag;
import com.ruoyi.init.MqttInit;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.bo.DevBaseDeviceReqBo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.service.IDevBaseDeviceService;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class DeviceInfoController extends BaseController {
    private final IDevBaseDeviceService iDevBaseDeviceService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DevBaseDeviceMapper baseMapper;
    private final MqttInit mqttInit;

    // 新增机柜
    @RepeatSubmit()
    @PostMapping("/zm/baseDevice/add")
    public R<Void> add(@RequestBody DevBaseDeviceReqBo bo) throws ModbusTransportException {
        Matcher matcher = Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$").matcher(bo.getIp());
        if (matcher.matches()) {
            String[] parts = bo.getIp().split("\\.");
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return R.fail("输入 IP 格式不规范");
                }
            }
        } else {
            return R.fail("输入 IP 格式不规范");
        }

        DevBaseDevice device = BeanUtil.toBean(bo, DevBaseDevice.class);
        device.setDeviceNo(Long.valueOf(bo.getIp().split("\\.")[3]));
        DevBaseDevice temp = baseMapper.selectOne(new LambdaQueryWrapper<DevBaseDevice>().select(DevBaseDevice::getId).orderByDesc(DevBaseDevice::getId).last("LIMIT 1"));
        if (temp != null && temp.getId() != null) {
            device.setId(temp.getId() + 1);
        } else device.setId(1L);

        int insert = baseMapper.insert(device);
        DeviceFlag.SetFlag(DeviceFlag.LIST, true);
        DeviceFlag.SetFlag(DeviceFlag.NAME, true);
        DeviceFlag.SetFlag(DeviceFlag.NO, true);
        DeviceFlag.SetFlag(DeviceFlag.IP, true);
        redisTemplate.delete("zm:create-tcp:" + device.getDeviceNo());
        redisTemplate.delete("zm:dev_base_devices:list");
        mqttInit.checkHeart(Math.toIntExact(device.getDeviceNo()));
        return insert > 0 ? R.ok("添加成功") : R.fail("添加失败");
    }

    // 修改机柜
    @PostMapping("zm/basic/editCabinet")
    public R<Void> editCabinet(@RequestBody DevBaseDeviceReqBo bo) {
        try {
            DevBaseDevice ipTemp = baseMapper.selectOne(new LambdaQueryWrapper<DevBaseDevice>()
                .select(DevBaseDevice::getId)
                .eq(DevBaseDevice::getIp, bo.getIp())
                .ne(DevBaseDevice::getId, bo.getId()));
            if (ipTemp != null && ipTemp.getId() != null) {
                return R.warn("IP地址不能重复，修改失败");
            }
            DevBaseDevice device = BeanUtil.toBean(bo, DevBaseDevice.class);
            baseMapper.updateById(device);
            DeviceFlag.SetFlag(DeviceFlag.LIST, true);
            DeviceFlag.SetFlag(DeviceFlag.NAME, true);
            DeviceFlag.SetFlag(DeviceFlag.NO, true);
            DeviceFlag.SetFlag(DeviceFlag.IP, true);
            Long deviceNo = baseMapper.selectById(bo.getId()).getDeviceNo();
            redisTemplate.delete("zm:create-tcp:" + deviceNo);
            redisTemplate.delete("zm:dev_base_devices:list");
            mqttInit.checkHeart(Math.toIntExact(deviceNo));
            return R.ok("修改成功");
        } catch (Exception e) {
            return R.fail("修改失败");
        }
    }

    // 删除机柜
    @DeleteMapping("/remove/{ids}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long ids) {
        Boolean b = iDevBaseDeviceService.deleteWithValidByIds(ids, true);
        DeviceFlag.SetFlag(DeviceFlag.LIST, true);
        DeviceFlag.SetFlag(DeviceFlag.NAME, true);
        DeviceFlag.SetFlag(DeviceFlag.NO, true);
        DeviceFlag.SetFlag(DeviceFlag.IP, true);
        return toAjax(b);
    }


}
