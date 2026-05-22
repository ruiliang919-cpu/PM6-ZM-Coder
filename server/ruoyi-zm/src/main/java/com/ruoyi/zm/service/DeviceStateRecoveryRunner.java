package com.ruoyi.zm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动后检查 Redis 状态，如有需要从 DB 恢复
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceStateRecoveryRunner implements ApplicationRunner {

    private final DeviceStateSnapshotService snapshotService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            snapshotService.restoreAllIfNeeded();
        } catch (Exception e) {
            log.error("Device state recovery failed on startup", e);
        }
    }
}
