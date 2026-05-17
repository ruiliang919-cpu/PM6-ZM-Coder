package com.ruoyi.zm.constants;

/**
 * Modbus 协议地址常量
 * 基于 PM6-ZM 协议 v3.8.2
 */
public class ProtocolAddress {
    private ProtocolAddress() {}

    // ===== 遥信 (Protocol 01) =====

    /** 遥信总数组长度 */
    public static final int COIL_TOTAL_LENGTH = 845;

    /** 遥信第一段起始地址 */
    public static final int COIL_SECTION_1_START = 0;
    /** 遥信第一段长度 */
    public static final int COIL_SECTION_1_LENGTH = 87;

    /** 遥信第二段起始地址 */
    public static final int COIL_SECTION_2_START = 256;
    /** 遥信第二段长度 */
    public static final int COIL_SECTION_2_LENGTH = 107;

    /** 遥信第三段起始地址 */
    public static final int COIL_SECTION_3_START = 512;
    /** 遥信第三段长度 */
    public static final int COIL_SECTION_3_LENGTH = 40;

    /** 遥信第四段起始地址 */
    public static final int COIL_SECTION_4_START = 640;
    /** 遥信第四段长度 */
    public static final int COIL_SECTION_4_LENGTH = 40;

    /** 遥信第五段起始地址 */
    public static final int COIL_SECTION_5_START = 768;
    /** 遥信第五段长度 */
    public static final int COIL_SECTION_5_LENGTH = 40;

    /** 遥信第六段起始地址 */
    public static final int COIL_SECTION_6_START = 896;
    /** 遥信第六段长度 */
    public static final int COIL_SECTION_6_LENGTH = 40;

    /** 遥信第七段起始地址 */
    public static final int COIL_SECTION_7_START = 959;
    /** 遥信第七段长度 */
    public static final int COIL_SECTION_7_LENGTH = 40;

    /** 遥信第八段起始地址 */
    public static final int COIL_SECTION_8_START = 1022;
    /** 遥信第八段长度 */
    public static final int COIL_SECTION_8_LENGTH = 40;

    /** 遥信第九段起始地址 */
    public static final int COIL_SECTION_9_START = 1152;
    /** 遥信第九段长度 */
    public static final int COIL_SECTION_9_LENGTH = 40;

    /** 遥信第十段起始地址 */
    public static final int COIL_SECTION_10_START = 1280;
    /** 遥信第十段长度 */
    public static final int COIL_SECTION_10_LENGTH = 40;

    /** 遥信第十一段起始地址 */
    public static final int COIL_SECTION_11_START = 1408;
    /** 遥信第十一段长度 */
    public static final int COIL_SECTION_11_LENGTH = 8;

    /** 遥信第十二段起始地址 */
    public static final int COIL_SECTION_12_START = 1472;
    /** 遥信第十二段长度 */
    public static final int COIL_SECTION_12_LENGTH = 8;

    /** 遥信第十三段起始地址 */
    public static final int COIL_SECTION_13_START = 1536;
    /** 遥信第十三段长度 */
    public static final int COIL_SECTION_13_LENGTH = 100;

    /** 遥信第十四段起始地址 */
    public static final int COIL_SECTION_14_START = 1792;
    /** 遥信第十四段长度 */
    public static final int COIL_SECTION_14_LENGTH = 64;

    /** 遥信第十五段起始地址 */
    public static final int COIL_SECTION_15_START = 2048;
    /** 遥信第十五段长度 */
    public static final int COIL_SECTION_15_LENGTH = 64;

    /** 遥信第十六段起始地址 */
    public static final int COIL_SECTION_16_START = 2525;
    /** 遥信第十六段长度 */
    public static final int COIL_SECTION_16_LENGTH = 64;

    /** 遥信第十七段起始地址 */
    public static final int COIL_SECTION_17_START = 2816;
    /** 遥信第十七段长度 */
    public static final int COIL_SECTION_17_LENGTH = 5;

    /** 遥信第十八段起始地址 */
    public static final int COIL_SECTION_18_START = 2870;
    /** 遥信第十八段长度 */
    public static final int COIL_SECTION_18_LENGTH = 18;

    /** 心跳检测线圈起始地址 */
    public static final int HEARTBEAT_COIL_START = 0;
    /** 心跳检测线圈数量 */
    public static final int HEARTBEAT_COIL_COUNT = 1;

    // ===== 遥测 (Protocol 03) =====

    /** 馈线支路数量 */
    public static final int FEEDER_BRANCH_COUNT = 64;
    /** 每个馈线支路名称占用的寄存器数量 */
    public static final int FEEDER_BRANCH_NAME_REGISTERS = 10;

    /** 主监控版本占用的寄存器数量 */
    public static final int MASTER_MONITOR_VERSION_REGISTERS = 10;

    /** 照度传感器外控通道占用的寄存器数量 */
    public static final int ILLUMINANCE_SENSOR_REGISTERS = 10;

    /** 回路分组数量上限 */
    public static final int MAX_PACKET_COUNT = 16;
    /** 每个分组回路编号占用的寄存器数量 */
    public static final int PACKET_LOOP_NUMBER_REGISTERS = 40;

    /** 分组选择 ASCII 字符串字节长度 */
    public static final int PACKET_SELECTION_ASCII_BYTES = 32;
    /** 分组回路编号 ASCII 字符串字节长度 */
    public static final int PACKET_LOOP_ASCII_BYTES = 80;

    // ===== 数组/寄存器大小 =====

    /** 分组回路编号字符串总长度 */
    public static final int PACKET_LOOP_STRING_TOTAL_LENGTH = 82;

    /** CRC 初始值 */
    public static final int CRC_INIT_VALUE = 0xFFFF;
    /** CRC 多项式 */
    public static final int CRC_POLYNOMIAL = 0xA001;
    /** 寄存器值转二进制字符串的固定长度 */
    public static final int REGISTER_BINARY_STRING_LENGTH = 16;
}
