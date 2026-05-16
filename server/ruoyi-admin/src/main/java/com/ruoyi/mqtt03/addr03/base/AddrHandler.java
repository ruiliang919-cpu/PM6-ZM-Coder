package com.ruoyi.mqtt03.addr03.base;

import com.google.gson.JsonObject;

public interface AddrHandler {
    void handle(Integer deviceNo, JsonObject payload);
}
