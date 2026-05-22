package com.ruoyi.utils;

import lombok.Getter;

@Getter
public enum LockUtil {
    LOCK01("lock-:01:"),
    LOCK03("lock-:03:"),
    LOCK_03("lock-:code-03:"),
    LOCK06("lock-:06:"),
    LOCK_06("lock-:code-06:"),
    LOCK_WRITE("lock-:write:");

    private final String lockName;

    // 枚举的构造函数
    LockUtil(String lockName) {
        this.lockName = lockName;
    }

}
