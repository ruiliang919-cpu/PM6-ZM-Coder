package com.ruoyi.mqtt03.addr03.handler.addr0x0000;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0x0000 {
    private String ip;
    private String addr;
    private String[] data;
}
