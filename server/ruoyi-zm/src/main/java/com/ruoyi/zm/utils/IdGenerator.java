package com.ruoyi.zm.utils;

import java.util.Random;
import java.util.UUID;

public class IdGenerator {
    private final static Random random = new Random();

    public static long UUIDId() {
        int uuidHashCode = UUID.randomUUID().toString().replaceAll("-", "").hashCode();
        long uniqueId = uuidHashCode < 0 ? -uuidHashCode : uuidHashCode;
        String idStr = String.valueOf(uniqueId);
        StringBuilder paddedId = new StringBuilder(idStr);
        while (paddedId.length() < 15) {
            paddedId.append(random.nextInt(10));
        }
        uniqueId = Long.parseLong(paddedId.toString());
        return uniqueId;
    }
}
