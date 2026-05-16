package com.ruoyi.mqtt03.request.handler;

import com.google.gson.JsonObject;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("Topic1")
@RequiredArgsConstructor
public class Topic1 implements RequestHandler {
    private final WriteController w;

    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        w.timeSaveNow(deviceNo);
    }
}
