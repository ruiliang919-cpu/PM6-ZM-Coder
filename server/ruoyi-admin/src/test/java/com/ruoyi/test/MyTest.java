package com.ruoyi.test;

import com.ruoyi.netty.handler.RtuWriteUtil;
import com.ruoyi.web.controller.zm.SensorModuleController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyTest {
    @Autowired
    private RtuWriteUtil rtuWriteUtil;
    @Autowired
    private SensorModuleController sensorModuleController;
    @Test
    public void test1(){
        // rtuWriteUtil.zoneControl((short) 1, 21,23);
        // sensorModuleController.getIllLux(44,1);
    }
}
