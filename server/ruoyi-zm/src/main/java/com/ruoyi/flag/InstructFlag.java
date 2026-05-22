package com.ruoyi.flag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InstructFlag {
    public static final String TOTAL_POINT = "update-the-total-points:";
    public static final String WRITE = "write-update-flag:";
    public static final String READ06 = "read-await-flag:";
    public static final String READ03 = "read-await-flag:";
    private static final Map<String, Boolean> map = new ConcurrentHashMap<>();

    public static boolean Flag(String type, int slaveId) {
        return map.getOrDefault(type + slaveId, true);
    }

    public static void SetFlag(String type, int slaveId, boolean flag) {
        map.put(type + slaveId, flag);
    }
}
