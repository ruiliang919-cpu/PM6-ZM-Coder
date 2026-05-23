package com.ruoyi.flag;

public class DeviceFlag {
    public static final String NAME = "nameUpdateFlag";
    public static final String LIST = "listUpdateFlag";
    public static final String NO = "noUpdateFlag";
    public static final String IP = "ipUpdateFlag";
    private static volatile boolean nameUpdateFlag = true;
    private static volatile boolean listUpdateFlag = true;
    private static volatile boolean noUpdateFlag = true;
    private static volatile boolean ipUpdateFlag = true;

    public static boolean Flag(String type) {
        switch (type) {
            case NAME:
                return nameUpdateFlag;
            case LIST:
                return listUpdateFlag;
            case IP:
                return ipUpdateFlag;
            case NO:
                return noUpdateFlag;
            default:
                return false;
        }
    }

    public synchronized static void SetFlag(String type, boolean flag) {
        switch (type) {
            case NAME:
                nameUpdateFlag = flag;
                break;
            case LIST:
                listUpdateFlag = flag;
                break;
            case IP:
                ipUpdateFlag = flag;
                break;
            case NO:
                noUpdateFlag = flag;
                break;
            default:
                break;
        }
    }
}
