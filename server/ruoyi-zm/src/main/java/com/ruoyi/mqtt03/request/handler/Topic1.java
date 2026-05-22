package com.ruoyi.mqtt03.request.handler;

import cn.hutool.json.JSONObject;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("Topic1")
@RequiredArgsConstructor
public class Topic1 implements RequestHandler {
    private final WriteController w;

    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        w.timeSaveNow(deviceNo);
    }
}
