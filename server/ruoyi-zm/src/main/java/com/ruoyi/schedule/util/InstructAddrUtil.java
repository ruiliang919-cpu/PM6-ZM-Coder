package com.ruoyi.schedule.util;

import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.service.IDevProtocol05Service;
import com.ruoyi.zm.service.IDevProtocol06Service;
import com.ruoyi.zm.service.telecommand.impl.Protocol01Service;
import com.ruoyi.zm.service.telemetering.impl.Protocol03Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstructAddrUtil {
    private final Protocol01Service protocol01Service;
    private final Protocol03Service protocol03Service;
    private final IDevProtocol05Service protocol05Service;
    private final IDevProtocol06Service protocol06Service;
    // private Map<String, String> map01 = null;
    private Map<String, String> map03 = null;
    private Map<String, String> map05 = null;
    private Map<String, String> map06 = null;
    private Map<String, String> map01R = null;
    public static final String MAP03 = "map03";
    public static final String MAP05 = "map05";
    public static final String MAP06 = "map06";
    // public static final String MAP01 = "map01";

    public String NameByAddr(String addr) {
        if (map01R == null)
            map01R = protocol01Service.get01List().stream().filter(p -> !p.getName().equals("备用"))
                .collect(Collectors.toMap(a -> a.getAddr().toUpperCase(), DevProtocol01::getName));
        return map01R.getOrDefault(addr.toUpperCase(), addr);
    }

    public String AddrByName(String type, String addrName) {
        switch (type) {
            case MAP03:
                if (map03 == null)
                    map03 = protocol03Service.get03List().stream().filter(p -> !p.getName().equals("备用"))
                        .collect(Collectors.toMap(DevProtocol03::getName, DevProtocol03::getAddr));
                return map03.getOrDefault(addrName, "<None>");
            case MAP05:
                if (map05 == null)
                    map05 = protocol05Service.get05List().stream().filter(p -> !p.getName().equals("备用"))
                        .collect(Collectors.toMap(DevProtocol05::getName, DevProtocol05::getAddr));
                return map05.getOrDefault(addrName, "<None>");
            case MAP06:
                if (map06 == null)
                    map06 = protocol06Service.get06List().stream().filter(p -> !p.getName().equals("备用"))
                        .collect(Collectors.toMap(DevProtocol06::getName, DevProtocol06::getAddr));
                return map06.getOrDefault(addrName, "<None>");
        }
        return "<None>";
    }

    public List<String> AddrByContains(String type, String... addrNames) {
        List<String> list = new ArrayList<>();
        switch (type) {
            case MAP03:
                if (map03 == null)
                    map03 = protocol03Service.get03List().stream().filter(p -> !p.getName().equals("备用"))
                        .collect(Collectors.toMap(DevProtocol03::getName, DevProtocol03::getAddr));
                map03.forEach((k, v) -> {
                    if (Arrays.stream(addrNames).anyMatch(a -> a.contains(k))) list.add(v);
                });
                return list;
            case MAP05:
                if (map05 == null)
                    map05 = protocol05Service.get05List().stream().filter(p -> !p.getName().equals("备用"))
                        .collect(Collectors.toMap(DevProtocol05::getName, DevProtocol05::getAddr));
                map05.forEach((k, v) -> {
                    if (Arrays.stream(addrNames).anyMatch(a -> a.contains(k))) list.add(v);
                });
                return list;
            case MAP06:
                if (map06 == null)
                    map06 = protocol06Service.get06List().stream().filter(p -> !p.getName().equals("备用"))
                        .collect(Collectors.toMap(DevProtocol06::getName, DevProtocol06::getAddr));
                map06.forEach((k, v) -> {
                    if (Arrays.stream(addrNames).anyMatch(a -> a.contains(k))) list.add(v);
                });
                return list;
        }
        return list;
    }

    // 可能有 BUG
    public static List<DevInstruct> sortAndLink(List<DevInstruct> instructs) {
        List<DevInstruct> sorted = instructs.stream()
            .sorted(Comparator.comparing(DevInstruct::getTimestamp))
            .collect(Collectors.toList());
        Map<String, DevInstruct> addrMap = sorted.stream()
            .collect(Collectors.toMap(DevInstruct::getAddr, i -> i));

        Set<DevInstruct> linked = new HashSet<>();
        List<DevInstruct> result = new ArrayList<>();
        for (DevInstruct current : sorted) {
            if (!linked.contains(current)) {
                result.add(current);
                linked.add(current);
                DevInstruct next = addrMap.get(current.getNextAddr());
                while (next != null && !linked.contains(next)) {
                    result.add(next);
                    linked.add(next);
                    next = addrMap.get(next.getNextAddr());
                }
            }
        }

        for (DevInstruct current : sorted)
            if (!linked.contains(current)) result.add(current);
        return result;
    }
}
