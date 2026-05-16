package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 * @author ruoyi
 */
@EnableScheduling
@SpringBootApplication
public class RuoYiApplication {
    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        new SpringApplication(RuoYiApplication.class).run(args);
    }
}
