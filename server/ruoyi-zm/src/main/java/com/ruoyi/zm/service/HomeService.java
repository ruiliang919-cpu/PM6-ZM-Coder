package com.ruoyi.zm.service;

import com.ruoyi.zm.domain.vo.PowerRespVo;

import java.util.List;

// 首页业务层接口
public interface HomeService {
    // 获取每一个机柜的总耗电量
    List<PowerRespVo> getAllPower();

    // 获取每一个机柜的日耗电量
    List<PowerRespVo> getAllPowerByDay();
}
