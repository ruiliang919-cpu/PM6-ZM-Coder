package com.ruoyi.cache;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.zm.domain.Leakage;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeakageCache {
    private static final String[] BRANCH_NAMES = {"0x0600", "0x060A", "0x0614", "0x061E", "0x0628", "0x0632", "0x063C", "0x0646", "0x0650", "0x065A", "0x0664", "0x066E", "0x0678", "0x0682", "0x068C", "0x0696", "0x06A0", "0x06AA", "0x06B4", "0x06BE", "0x06C8", "0x06D2", "0x06DC", "0x06E6", "0x06F0", "0x06FA", "0x0704", "0x070E", "0x0718", "0x0722", "0x072C", "0x0736", "0x0740", "0x074A", "0x0754", "0x075E", "0x0768", "0x0772", "0x077C", "0x0786", "0x0790", "0x079A", "0x07A4", "0x07AE", "0x07B8", "0x07C2", "0x07CC", "0x07D6", "0x07E0", "0x07EA", "0x07F4", "0x07FE", "0x0808", "0x0812", "0x081C", "0x0826", "0x0830", "0x083A", "0x0844", "0x084E", "0x0858", "0x0862", "0x086C", "0x0876"};
    private final Key key;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final List<String> nameList = new ArrayList<>();

    // 直流机柜-机柜信息-传感器页面列表
    @Transactional
    public TableDataInfo<Leakage> table(int deviceId, int num, int size) throws ModbusTransportException {
        List<String> leakageCurrents = new ArrayList<>();
        int Num = 0;
        try {
            Num = Integer.parseInt(key.getTelemeter(deviceId, "0x2740") + "");
            // log.info("Num is：{}", Num);
        } catch (Exception ignored) {
        }
        // 馈线支路名称
//        List<String> names = (List<String>) key.multiGetTelemeter(deviceId, BRANCH_NAMES);

        String ip = key.getCreateTCP(deviceId).getIp();
        List<String> names = (List<String>) redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(ip, "0x0600", deviceId));
        if (names == null || names.isEmpty()) names = nameList();

        // 漏电流
        try {
            List<BigDecimal> currentList = key.getTelemeterBigDecimalList(deviceId, "0x2700");
            if (!currentList.isEmpty())
                for (int index = 0; index < Num; index++)
                    leakageCurrents.add(currentList.get(index) + "mA");
            else
                for (int index = 0; index < Num; index++)
                    leakageCurrents.add("0.0mA");
        } catch (Exception e) {
            for (int index = 0; index < Num; index++)
                leakageCurrents.add("0.0mA");
        }

        names = names.subList(0, Math.min(Num, names.size()));
        leakageCurrents = leakageCurrents.subList(0, Math.min(Num, leakageCurrents.size()));

        List<Leakage> result = new ArrayList<>();
        for (int index = 0; index < Num; index++) {
            Leakage obj = new Leakage();
            obj.setName(names.get(index));
            obj.setCurrent(leakageCurrents.get(index));
            result.add(obj);
        }

        return key.getPageTable(result, num, size);
    }

    public static List<String> nameList() {
        if (!nameList.isEmpty()) return nameList;
        for (int i = 0; i < 64; i++) nameList.add("馈电支路" + (i + 1));
        return nameList;
    }
}
