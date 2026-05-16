package com.ruoyi.mqtt03.request.handler;

import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.LoopByGroupCache;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.vo.UpdateLoopReqVo;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("Topic28")
@RequiredArgsConstructor
public class Topic28 implements RequestHandler {
    private final WriteController w;
    private final Key k;

    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        int id = payload.get("id").getAsInt();
        UpdateLoopReqVo u = new UpdateLoopReqVo();
        u.setDeviceId(deviceNo);
        u.setGroupId(id);
        String sourceLoopStr = (String) k.getTelemeter(deviceNo, LoopByGroupCache.LOOP_NUMS_ADDR_ARR[id - 1]);
        if (sourceLoopStr != null && !sourceLoopStr.isEmpty()) u.setLoopNo(ScaleUtil.splitIntoIntPairs(sourceLoopStr));
        w.updateLoop(u);
    }
}
