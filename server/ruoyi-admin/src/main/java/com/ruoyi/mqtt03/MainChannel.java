package com.ruoyi.mqtt03;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
        try {
            JSONObject json = JSONUtil.parseObj(payload);
            String addr = json.getStr("addr").substring(2);
            AddrHandler handler = addrHandlerFactory.getHandler(addr);
            if (handler != null) handler.handle(deviceNo, json);
        } catch (Exception e) {
            log.error("handleHoldingRegister error, deviceNo={}", deviceNo, e);
        }
    }

    public void handleRequest(Integer deviceNo, String payload) {
        try {
            JSONObject json = JSONUtil.parseObj(payload);
            int topic = json.getInt("writeTopic");
            RequestHandler handler = requestHandlerFactory.getHandler(topic);
            if (handler != null) handler.handle(deviceNo, json);
        } catch (Exception e) {
            log.error("handleRequest error, deviceNo={}", deviceNo, e);
        }
    }
}
