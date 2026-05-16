package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaIgnore;
import com.ruoyi.utils.device.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Executor;

// http://localhost:8081/method/time/status
// http://localhost:8081/method/time/thread
@SaIgnore
@RestController
@RequestMapping("/method/time")
@RequiredArgsConstructor
public class MethodTimeController {
    private final TimeUtil timeUtil;
    private final Executor mqttTaskExecutor;

    @GetMapping("/status")
    public void status(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        List<TimeUtil.Stats> dataList = timeUtil.getAddress();
        try (PrintWriter writer = response.getWriter()) {
            writer.write("<html><head><title>最近十次数据</title>");
            writer.write("<style>table {border-collapse: collapse;} th, td {border: 1px solid black; padding: 8px;}</style>");
            writer.write("</head><body>");
            writer.write("<h2>最近十次数据</h2>");
            writer.write("<table>");
            writer.write("<tr>");
            writer.write("<th>地址</th>");
            writer.write("<th>最长执行时间（ms）</th>");
            writer.write("<th>最短执行时间（ms）</th>");
            writer.write("<th>平均执行时间（ms）</th>");
            writer.write("</tr>");
            for (TimeUtil.Stats stats : dataList) {
                writer.write("<tr>");
                writer.write("<td>" + stats.getAddr() + "</td>");
                writer.write("<td>" + stats.getMax() + "</td>");
                writer.write("<td>" + stats.getMin() + "</td>");
                writer.write("<td>" + stats.getAvg() + "</td>");
                writer.write("</tr>");
            }
            writer.write("</table>");
            writer.write("</body></html>");
        }
    }

    @GetMapping("/thread")
    public void thread(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            Executor e = mqttTaskExecutor;
            if (e instanceof ThreadPoolTaskExecutor) {
                ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) e;
                int activeThreads = executor.getActiveCount();
                int poolSize = executor.getPoolSize();
                int queueSize = executor.getThreadPoolExecutor().getQueue().size();
                long completedTasks = executor.getThreadPoolExecutor().getCompletedTaskCount();
                writer.write("<html><head><title>任务</title>");
                writer.write("<style>table {border-collapse: collapse;} th, td {border: 1px solid black; padding: 8px;}</style>");
                writer.write("</head><body>");
                writer.write("<h2>线程池监控</h2>");
                writer.write("<table>");
                writer.write("<tr>");
                writer.write("<th>线程池大小</th>");
                writer.write("<th>活跃线程数</th>");
                writer.write("<th>队列中任务数</th>");
                writer.write("<th>已完成任务数</th>");
                writer.write("</tr>");
                writer.write("<tr>");
                writer.write("<td>" + poolSize + "</td>");
                writer.write("<td>" + activeThreads + "</td>");
                writer.write("<td>" + queueSize + "</td>");
                writer.write("<td>" + completedTasks + "</td>");
                writer.write("</tr>");
                writer.write("</table>");
                writer.write("</body></html>");
            }
        }
    }
}
