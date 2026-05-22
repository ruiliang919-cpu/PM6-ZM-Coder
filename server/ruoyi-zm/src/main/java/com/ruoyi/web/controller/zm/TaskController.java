package com.ruoyi.web.controller.zm;

import com.ruoyi.schedule.ScheduleTask;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/task")
public class TaskController {
    private final ScheduleTask task;

    @GetMapping("/start")
    public void start(long delay, String type) {
        // switch (type) {
        //     case "queue":
        //         task.startQueueTask(delay);
        //         break;
        //     case "01":
        //         task.start01Task(delay);
        //         break;
        //     case "03":
        //         task.start03Task(delay);
        //         break;
        //     // case "63":
        //     //     task.start63Task(delay);
        //     //     break;
        //     default:
        // }
    }

    @GetMapping("/startAuto")
    public void start() {
        // task.startQueueTask(1);
        // task.Start01Task(2000);
        // task.start03Task(500);
        // task.start63Task(500);
    }

    @GetMapping("/stop")
    public void stop(String type) {
        // switch (type) {
        //     case "queue":
        //         task.stopQueueTask();
        //         break;
        //     case "01":
        //         task.stop01Task();
        //         break;
        //     case "03":
        //         task.stop03Task();
        //         break;
        //     // case "63":
        //     //     task.stop63Task();
        //     //     break;
        //     default:
        // }
    }

    @GetMapping("/stopAll")
    public void stop() {
        // task.stopQueueTask();
        // task.Stop01Task();
        // task.stop03Task();
        // task.stop63Task();
    }
}
