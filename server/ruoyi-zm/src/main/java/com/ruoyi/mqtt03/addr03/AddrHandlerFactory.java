package com.ruoyi.mqtt03.addr03;

import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AddrHandlerFactory {
    private final Map<String, AddrHandler> handlers;

    public AddrHandler getHandler(String addr) {
        return handlers.get("Addr" + addr + "Handler");
    }

    public static String getKey(String ip, String addr, Integer deviceNo) {
        return "zm:queue:zm:cache:3:" + ip + ":" + deviceNo + ":" + addr;
    }
}
