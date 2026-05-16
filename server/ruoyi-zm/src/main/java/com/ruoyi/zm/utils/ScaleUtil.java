package com.ruoyi.zm.utils;

import cn.hutool.core.util.ArrayUtil;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

// 进制工具类
public class ScaleUtil {
    public static int from16To10(String str) {
        String hexWithoutPrefix = str.substring(2);
        return Integer.parseInt(hexWithoutPrefix, 16);
    }

    public static int from16To10Min1(String str) {
        return from16To10(str);
    }

    public static String toAscII(short[] data) {
        if (data == null) {
            return "";
        }
        int lastNonZeroIndex = data.length - 1;
        for (int i = data.length - 1; i >= 0; i--) {
            if (data[i] != 0x0000) {
                lastNonZeroIndex = i;
                break;
            }
        }
        short[] trimmedRegisters = Arrays.copyOfRange(data, 0, lastNonZeroIndex + 1);
        byte[] bytes = new byte[trimmedRegisters.length * 2];
        for (int i = 0; i < trimmedRegisters.length; i++) {
            bytes[i * 2] = (byte) ((trimmedRegisters[i] >> 8) & 0xFF);
            bytes[i * 2 + 1] = (byte) (trimmedRegisters[i] & 0xFF);
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Charset gbkCharset = Charset.forName("GBK");
        CharBuffer charBuffer = gbkCharset.decode(buffer);
        return charBuffer.toString().trim();
    }

    // 二进制字符串转数组
    public static int[] toIntArray(String binaryString) {

        int[] binaryArray = new int[binaryString.length()];

        for (int i = 0; i < binaryString.length(); i++) {
            char c = binaryString.charAt(i);
            if (c == '1') {
                binaryArray[i] = 1;
            } else {
                binaryArray[i] = 0;
            }
        }

        binaryArray = ArrayUtil.reverse(binaryArray);

        return binaryArray;
    }

    // 构建分组+分组回路的编号字符串
    public static String combineIDs(String groupID, Integer[] loopIDs) {
        StringBuilder sb = new StringBuilder();
        sb.append(groupID); // 先添加分组编号
        for (Integer id : loopIDs) {
            if (id < 10) {
                sb.append("0").append(id);
            } else {
                sb.append(id);
            }
        }

        // 获取当前字符串长度
        int currentLength = sb.length();
        // 计算还需要多少位才能达到82位
        int paddingLength = 82 - currentLength;
        // 如果不足82位，则填充0
        if (paddingLength > 0) {
            while (paddingLength-- > 0) {
                sb.append('0');
            }
        }
        return sb.toString();
    }


    public static int convertGroupsToRegisterValue(Integer[] selectedGroups) {
        int result = 0;
        for (int group : selectedGroups) {
            if (group >= 1 && group <= 16) {
                // 分组编号从1开始，对应的位是从0开始的
                result |= (1 << (group - 1));
            }
        }
        return result;
    }

    public static int convertGroupsToRegisterValueInt(int[] selectedGroups) {
        int result = 0;
        for (int group : selectedGroups) {
            if (group >= 1 && group <= 16) {
                // 分组编号从1开始，对应的位是从0开始的
                result |= (1 << (group - 1));
            } else {
                throw new IllegalArgumentException("Group number must be between 1 and 16.");
            }
        }
        return result;
    }

    public static int[] gbkToArr(String input) {
        Charset gbkCharset = Charset.forName("GBK");
        byte[] bytes = input.getBytes(gbkCharset);
        if (bytes.length % 2 != 0) {
            bytes = padByteArrayToEvenLength(bytes);
        }
        int shortsCount = bytes.length / 2;
        int[] shorts = new int[shortsCount];
        for (int i = 0; i < shortsCount; i++) {
            shorts[i] = ((bytes[i * 2] & 0xFF) << 8) | (bytes[i * 2 + 1] & 0xFF);
        }

        return shorts;
    }

    public static Integer[] gbkToIntegerArr(String input) {
        Charset gbkCharset = Charset.forName("GBK");
        byte[] bytes = input.getBytes(gbkCharset);
        if (bytes.length % 2 != 0) {
            bytes = padByteArrayToEvenLength(bytes);
        }
        Integer shortsCount = bytes.length / 2;
        Integer[] shorts = new Integer[shortsCount];
        for (int i = 0; i < shortsCount; i++) {
            shorts[i] = ((bytes[i * 2] & 0xFF) << 8) | (bytes[i * 2 + 1] & 0xFF);
        }

        return shorts;
    }

    private static byte[] padByteArrayToEvenLength(byte[] originalBytes) {
        byte[] paddedBytes = new byte[originalBytes.length + 1];
        System.arraycopy(originalBytes, 0, paddedBytes, 0, originalBytes.length);
        paddedBytes[paddedBytes.length - 1] = 0;
        return paddedBytes;
    }


    // 十六进制+十进制再转回十六进制
    public static String hexAddInt(String hexStr, int num) {
        int hexNumber = Integer.parseInt(hexStr.substring(2), 16);
        return String.format("0x%04X", hexNumber + num);
    }

    // 计算CRC校验值
    public static int crcCheck(String[] text) {
        byte[] data = new byte[text.length];
        for (int i = 0; i < text.length; i++) {
            data[i] = (byte) Integer.parseInt(text[i], 16);
        }
        int crcValue = 0xFFFF;
        for (byte datum : data) {
            crcValue ^= (datum & 0xFF);
            for (int j = 0; j < 8; j++) {
                if ((crcValue & 0x0001) != 0) {
                    crcValue = (crcValue >> 1) ^ 0xA001;
                } else {
                    crcValue = (crcValue >> 1);
                }
            }
        }
        crcValue = ((crcValue & 0xFF00) >>> 8) | ((crcValue & 0x00FF) << 8);
        return crcValue;
    }

    public static int crcCheck(String input) {
        String[] text = splitIntoPairs(input);
        return crcCheck(text);
    }

    // 将字符串两两分割成一组
    public static String[] splitIntoPairs(String input) {
        int size = input.length() / 2;
        String[] result = new String[size];
        for (int i = 0; i < input.length(); i += 2) {
            result[i / 2] = input.substring(i, i + 2);
        }
        return result;
    }

    public static Integer[] splitIntoIntPairs(String input) {
        int size = input.length() / 2;
        Integer[] result = new Integer[size];
        for (int i = 0; i < input.length(); i += 2) {
            result[i / 2] = Integer.parseInt(input.substring(i, i + 2));
        }
        return result;
    }

    // 十进制转十六进制字符串 10 → 0A
    public static String to4s(Integer number) {
        return String.format("%2s", Integer.toHexString(number).toUpperCase()).replace(' ', '0');
    }

    // 将二进制字符串转换为十六进制字符串 长度超出Long类型可用
    public static String binaryToHex(String binary, int size) {
        BigInteger decimal = new BigInteger(binary, 2);
        String upperCase = decimal.toString(16).toUpperCase();
        return String.format("%" + size + "s", upperCase).replace(' ', '0');
    }

    // 十六进制字符串转二进制字符串再转字符串数组
    public static String[] HexToBinaryArr(String hex) {
        String binaryString = Integer.toBinaryString(Integer.parseInt(hex, 16));
        binaryString = String.format("%16s", binaryString).replace(' ', '0');
        return binaryString.split("");
    }


}
