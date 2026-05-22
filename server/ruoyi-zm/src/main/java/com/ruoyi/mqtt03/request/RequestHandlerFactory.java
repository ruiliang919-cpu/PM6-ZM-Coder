package com.ruoyi.mqtt03.request;

import com.ruoyi.mqtt03.request.base.RequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequestHandlerFactory {
    private final Map<String, RequestHandler> handlers;

    public RequestHandler getHandler(int topic) {
        return handlers.get("Topic" + topic);
    }
}
