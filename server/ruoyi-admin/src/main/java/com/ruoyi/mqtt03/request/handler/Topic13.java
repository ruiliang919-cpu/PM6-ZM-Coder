package com.ruoyi.mqtt03.request.handler;

import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("Topic13")
@RequiredArgsConstructor
public class Topic13 implements RequestHandler {
    private final WriteController w;
    private final Key k;

    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        w.moduleSetting(deviceNo, (Addr0XB715.Data) k.getRemote(deviceNo, "0XB715"));
    }
}
