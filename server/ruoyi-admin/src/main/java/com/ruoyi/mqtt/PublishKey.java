package com.ruoyi.mqtt;

/**
 * 通讯中断后：恢复通讯需要下发的主题：
 * 1.对时设置         主题：/zm/{ip}/write01
 * 3.添加回路         主题：/zm/{ip}/write03
 * 4.删除回路         主题：/zm/{ip}/write04
 * 5.分组命名         主题：/zm/{ip}/write05
 * 6.场景命名         主题：/zm/{ip}/write06
 * 7.分区编号         主题：/zm/{ip}/write07
 * 8.普通时控         主题：/zm/{ip}/write08
 * 9.场景时控         主题：/zm/{ip}/write09
 * 10.红外模式       主题：/zm/{ip}/write10
 * 11.照度模式       主题：/zm/{ip}/write11
 * 12.交流开关       主题：/zm/{ip}/write12
 * 13.模式选择       主题：/zm/{ip}/write13
 * 20.照度外控值     主题：/zm/{ip}/write20
 * 21.场景设置     主题：/zm/{ip}/write21
 * 25.照度模式     主题：/zm/{ip}/write25
 * 26.红外模式     主题：/zm/{ip}/write26
 */
public enum PublishKey {
    对时设置("write01"),
    模块输出设置("write02"),
    添加回路("write03"),
    删除回路("write04"),
    分组命名("write05"),
    场景命名("write06"),
    分区编号("write07"),
    普通时控("write08"),
    场景时控("write09"),
    红外模式("write10"),
    照度模式("write11"),
    交流开关("write12"),
    模式选择("write13"),
    系统总开关("write14"),
    工作模式("write15"),
    回路总开关("write16"),
    整流模块开关机("write17"),
    分组控制总开关("write18"),
    场景控制("write19"),
    照度外控值("write20"),
    场景设置("write21"),
    系统控制其他开关开关0148("write22"),
    设置更新标志位("write23"),
    事件记录("write24"),
    照度模式选择("write25"),
    红外模式选择("write26"),
    交流开关控制("write27"),
    保存回路("write28"),
    获取心跳和版本号("write29");

    private final String code;

    PublishKey(String code) {
        this.code = code;
    }

    public String getTopic(String lastIp) {
        return "/zm/" + lastIp + "/" + code;
    }
}
