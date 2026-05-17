package com.ruoyi.zm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 耗电量历史数据归档定时任务
 * 每天凌晨2点执行，将超过1年的数据迁移到归档表
 */
@Component
public class PowerDataArchiveTask {

    private static final Logger log = LoggerFactory.getLogger(PowerDataArchiveTask.class);

    /** 归档阈值：1年（秒） */
    private static final long ARCHIVE_THRESHOLD_SECONDS = 365L * 24 * 60 * 60;

    /** 每批次处理记录数 */
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 每天凌晨2点执行数据归档
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void archiveOldData() {
        long thresholdTimestamp = System.currentTimeMillis() / 1000 - ARCHIVE_THRESHOLD_SECONDS;
        log.info("开始归档历史耗电量数据, 阈值时间戳(秒): {}", thresholdTimestamp);

        try {
            int totalArchived = 0;

            while (true) {
                // 1. 先查出待归档的 ID 列表
                List<Long> ids = jdbcTemplate.queryForList(
                    "SELECT id FROM dev_base_power WHERE timestamp < ? ORDER BY id LIMIT ?",
                    Long.class, thresholdTimestamp, BATCH_SIZE);

                if (ids.isEmpty()) break;

                // 2. 构建 IN 子句
                String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
                Object[] idArray = ids.toArray();

                // 3. 基于同一批 ID 插入归档表
                jdbcTemplate.update(
                    "INSERT INTO dev_base_power_archive (id, device_id, type, value, timestamp) " +
                    "SELECT id, device_id, type, value, timestamp FROM dev_base_power WHERE id IN (" + placeholders + ")",
                    idArray);

                // 4. 删除同一批 ID
                jdbcTemplate.update(
                    "DELETE FROM dev_base_power WHERE id IN (" + placeholders + ")",
                    idArray);

                totalArchived += ids.size();
            }

            log.info("数据归档完成, 共归档 {} 条记录", totalArchived);
        } catch (Exception e) {
            log.error("数据归档失败", e);
            throw e;
        }
    }
}
