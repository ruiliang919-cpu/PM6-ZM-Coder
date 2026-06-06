package com.ruoyi.modbus;

import com.ruoyi.cache.DeviceUtilCache;
import com.ruoyi.cache.Key;
import com.ruoyi.modbus.util.Register;
import com.ruoyi.netty.handler.RtuHandler;
import com.ruoyi.netty.handler.RtuWriteUtil;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class Slave {
    private final RtuHandler rtuHandler;
    private final RtuWriteUtil rtuWriteUtil;
    private final TaskExecutor taskExecutor;
    private final Key key;
    private final DeviceUtilCache deviceUtilCache;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public Slave(RtuHandler rtuHandler, RtuWriteUtil rtuWriteUtil,
                 @Qualifier("threadPoolTaskExecutor") TaskExecutor taskExecutor,
                 Key key, DeviceUtilCache deviceUtilCache,
                 RedisTemplate<String, Object> redisTemplate) {
        this.rtuHandler = rtuHandler;
        this.rtuWriteUtil = rtuWriteUtil;
        this.taskExecutor = taskExecutor;
        this.key = key;
        this.deviceUtilCache = deviceUtilCache;
        this.redisTemplate = redisTemplate;
    }
    @Value("${init.slave:false}")
    public boolean flag;
    @Value("${slave.port:1504}")
    private int port;

    @PostConstruct
    public void run() {
        if (flag) {
            log.info("Starting modbus slave server thread...");
            taskExecutor.execute(this::createSalve);
        }
    }

    private void createSalve() {
        final ModbusSlaveSet salve = new TcpSlave(port, false);
        salve.addProcessImage(new Register()
            .setRtuHandler(rtuHandler)
            .setRtuWriteUtil(rtuWriteUtil)
            .setRedisTemplate(redisTemplate)
            .setDeviceUtilCache(deviceUtilCache)
            .setKey(key)
            .setStart(0)
            .getImg(1));
//         log.info("<~~~~~~~~ Modbus Slave Server Run Start Success! In {}. ~~~~~~~~>", port);
        try {
            salve.start();
        } catch (Exception e) {
            log.error("创建从站失败", e);
        }
    }
}
