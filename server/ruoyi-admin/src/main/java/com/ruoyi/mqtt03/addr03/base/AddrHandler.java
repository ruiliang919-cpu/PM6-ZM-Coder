package com.ruoyi.mqtt03.addr03.base;

import cn.hutool.json.JSONObject;

public interface AddrHandler {
    void handle(Integer deviceNo, JSONObject payload);
}
