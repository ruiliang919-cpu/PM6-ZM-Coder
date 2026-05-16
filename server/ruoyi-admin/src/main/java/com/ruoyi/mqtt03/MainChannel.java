package com.ruoyi.mqtt03;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.mqtt03.request.RequestHandlerFactory;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainChannel {
    private final AddrHandlerFactory addrHandlerFactory;
    private final RequestHandlerFactory requestHandlerFactory;

    public void handleHoldingRegister(Integer deviceNo, String payload) {
        JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
        String addr = json.get("addr").getAsString().substring(2);
        AddrHandler handler = addrHandlerFactory.getHandler(addr);
        if (handler != null) handler.handle(deviceNo, json);
    }

    public void handleRequest(Integer deviceNo, String payload) {
        JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
        int topic = json.get("writeTopic").getAsInt();
        RequestHandler handler = requestHandlerFactory.getHandler(topic);
        if (handler != null) handler.handle(deviceNo, json);
    }
}
