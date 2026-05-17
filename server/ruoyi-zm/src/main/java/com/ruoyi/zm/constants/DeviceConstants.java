package com.ruoyi.zm.constants;

/**
 * 设备相关常量定义
 * 
 * @author system
 */
public class DeviceConstants {

    private DeviceConstants() {
        throw new IllegalStateException("Constants class");
    }

    // ========== 在线状态 (online_status) ==========
    /** 离线 */
    public static final int ONLINE_STATUS_OFFLINE = 0;
    /** 在线 */
    public static final int ONLINE_STATUS_ONLINE = 1;

    // ========== 设备状态 (device_status) ==========
    /** 正常 */
    public static final int DEVICE_STATUS_NORMAL = 0;
    /** 故障/告警 */
    public static final int DEVICE_STATUS_FAULT = 1;

    // ========== 运行开关 (run_status) ==========
    /** 关闭 */
    public static final int RUN_STATUS_OFF = 0;
    /** 开启 */
    public static final int RUN_STATUS_ON = 1;

    // ========== 运行模式 (run_mode) ==========
    /** 自动模式 */
    public static final int RUN_MODE_AUTO = 0;
    /** 手动模式 */
    public static final int RUN_MODE_MANUAL = 1;

    // ========== 运行模式类型 (run_mode_type) ==========
    /** 普通时控 */
    public static final int MODE_TYPE_TIME_CONTROL = 1;
    /** 场景时控 */
    public static final int MODE_TYPE_SCENE_CONTROL = 2;
    /** 红外传感 */
    public static final int MODE_TYPE_INFRARED = 3;
    /** 照度传感 */
    public static final int MODE_TYPE_ILLUMINANCE = 4;

    // ========== 删除标志 (del_flag) ==========
    /** 存在 */
    public static final String DEL_FLAG_NORMAL = "0";
    /** 已删除 */
    public static final String DEL_FLAG_DELETED = "2";

    // ========== Redis 缓存 Key 前缀 ==========
    public static final String CACHE_TCP_PREFIX = "zm:create-tcp:";
    public static final String CACHE_FAULT_PREFIX = "zm:fault:";
}
