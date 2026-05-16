package com.ruoyi.send;

import cn.hutool.json.JSONObject;
import com.ruoyi.schedule.util.InstructAddrUtil;
import com.ruoyi.schedule.util.WriteSaveUtil;
import com.ruoyi.zm.domain.DevInstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

@Slf4j
@Service
@RequiredArgsConstructor
public class WriteSendService {
    private final WriteSaveUtil saveUtil;
    private final InstructAddrUtil sendUtil;

    // data 必须是除 String 类型外的任意类型的数组！！！！！！！！！！！！！！！！！！
    public String writeToAddr(Integer deviceId, Integer code,
                                           String addrName, Integer addrNum, Object data) {
        String addr = "";
        DevInstruct instruct = new DevInstruct();
        instruct.setSalveId(deviceId);
        instruct.setCode(code);
        if (code == 5) addr = sendUtil.AddrByName(InstructAddrUtil.MAP05, addrName);
        else if (code == 6) addr = sendUtil.AddrByName(InstructAddrUtil.MAP06, addrName);
        instruct.setAddr(addr);
        instruct.setAddrNum(addrNum);
        instruct.setWriteValue(new JSONObject().set("arr", data).toString());
        saveUtil.SaveSimple(instruct);
        return addr;
    }

    public String writeToAddr(Integer deviceId, Integer code,
                                           String addrName, Integer addrNum, Object data, String nextArr) {
        String addr = "";
        DevInstruct instruct = new DevInstruct();
        instruct.setSalveId(deviceId);
        instruct.setCode(code);
        if (code == 5) addr = sendUtil.AddrByName(InstructAddrUtil.MAP05, addrName);
        else if (code == 6) addr = sendUtil.AddrByName(InstructAddrUtil.MAP06, addrName);
        instruct.setAddr(addr);
        instruct.setAddrNum(addrNum);
        instruct.setWriteValue(new JSONObject().set("arr", data).toString());
        instruct.setNextAddr(nextArr);
        saveUtil.SaveSimple(instruct);
        return addr;
    }

    public static short[] stringToShortArray(String input) {
        // 使用GBK编码将字符串转换为字节数组
        Charset gbkCharset = Charset.forName("GBK");
        byte[] bytes = input.getBytes(gbkCharset);

        if (bytes.length != 20) {
            bytes = padByteArrayToEvenLength(bytes);
        }

        // 计算短整型数组的大小
        int shortsCount = 10;

        // 创建短整型数组
        short[] shorts = new short[shortsCount];

        // 将字节数组转换为短整型数组
        for (int i = 0; i < shortsCount; i++) {
            try {
                shorts[i] = (short) (((bytes[i * 2] & 0xFF) << 8) | (bytes[i * 2 + 1] & 0xFF));
            } catch (Exception e) {
                shorts[i] = 0;
            }
        }

        return shorts;
    }

    private static byte[] padByteArrayToEvenLength(byte[] originalBytes) {
        // 创建一个新的字节数组，长度为原始数组长度+1
        byte[] paddedBytes = new byte[originalBytes.length + 1];
        // 将原始字节数组复制到新数组中
        System.arraycopy(originalBytes, 0, paddedBytes, 0, originalBytes.length);
        // 最后一位填充为0
        paddedBytes[paddedBytes.length - 1] = 0;
        return paddedBytes;
    }
}
