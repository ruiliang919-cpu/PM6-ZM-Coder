package com.ruoyi.mqtt03.request.base;

import com.google.gson.JsonObject;

public interface RequestHandler {
    void handle(Integer deviceNo, JsonObject payload);
}
