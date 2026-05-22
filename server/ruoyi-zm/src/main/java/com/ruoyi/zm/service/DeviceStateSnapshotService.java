package com.ruoyi.zm.service;

/**
 * Redis 设备状态快照服务 — 定期备份关键 Redis 状态到 MySQL，启动时恢复
 */
public interface DeviceStateSnapshotService {

    /** 保存指定 key 模式的所有 Redis 值到 DB 快照 */
    void snapshotPattern(String keyPattern);

    /** 从一个 key 模式恢复 Redis 数据 */
    void restorePattern(String keyPattern);

    /** 执行全部关键 key 的快照 */
    void snapshotAll();

    /** 从 DB 恢复全部关键 Redis 数据（仅在 Redis 为空时） */
    void restoreAllIfNeeded();
}
