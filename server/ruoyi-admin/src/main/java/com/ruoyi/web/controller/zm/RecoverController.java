package com.ruoyi.web.controller.zm;


import com.ruoyi.common.core.domain.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;

// 断线重连备份
@RestController
@RequestMapping("/recover")
public class RecoverController {

    private final AsyncRestTemplate restTemplate = new AsyncRestTemplate();

    @Value("${recover.host:http://127.0.0.1:4444/}")
    private String host;

    @Value("${recovery.password:}")
    private String password;

    @GetMapping("/password")
    public R<Boolean> password(String password) {
        return R.ok(this.password.equals(password));
    }

    @GetMapping("/issued")
    public R<?> issued() {
        restTemplate.getForEntity(host + "recover/issued", String.class);
        return R.ok("迁移设备数据指令已下发");
    }

    @GetMapping("/read")
    public R<?> read() {
        restTemplate.getForEntity(host + "recover/read", String.class);
        return R.ok("读取设备最新数据指令已下发");
    }
}
