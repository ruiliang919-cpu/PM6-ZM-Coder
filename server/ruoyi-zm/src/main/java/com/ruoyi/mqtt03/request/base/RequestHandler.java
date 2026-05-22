package com.ruoyi.mqtt03.request.base;

import cn.hutool.json.JSONObject;

public interface RequestHandler {
    void handle(Integer deviceNo, JSONObject payload);
}
