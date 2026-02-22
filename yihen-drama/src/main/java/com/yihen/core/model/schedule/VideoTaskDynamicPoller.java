package com.yihen.core.model.schedule;

import com.yihen.entity.VideoTask;
import com.yihen.service.VideoTaskService;
import com.yihen.websocket.TaskStatusWebSocketHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class VideoTaskDynamicPoller {

    private final ThreadPoolTaskScheduler scheduler;
    private final VideoTaskService videoTaskService;

    @Autowired
    private TaskStatusWebSocketHandler taskStatusWebSocketHandler;

    // 可调参数
    private final long minDelayMs = 5_000;     // 有活时快一点
    private final long maxDelayMs = 120_000;   // 空闲时最多 2 分钟
    private final long baseDelayMs = 10_000;   // 默认间隔

    private volatile long currentDelayMs = baseDelayMs;
    private int emptyStreak = 0;

    public VideoTaskDynamicPoller(ThreadPoolTaskScheduler scheduler,
                                  VideoTaskService videoTaskService) {
        this.scheduler = scheduler;
        this.videoTaskService = videoTaskService;
    }

    @PostConstruct
    public void start() {
        // 启动后 2 秒跑第一次
        scheduleNext(2_000);
        log.info("动态轮询器已启动");
    }

    private void scheduleNext(long delayMs) {
        scheduler.schedule(this::tickSafe, new Date(System.currentTimeMillis() + delayMs));
    }

    private void tickSafe() {
        try {
            tick();
        } catch (Exception e) {
            // 这里千万别 throw 出去，否则本次执行结束后可能不再安排下一次
            log.error("轮询异常", e);

            // 异常时适当放慢一点，避免一直打第三方
            currentDelayMs = Math.min(maxDelayMs, Math.max(currentDelayMs, 30_000));
        } finally {
            scheduleNext(currentDelayMs);
        }
    }

    private void tick() throws Exception {
        Instant now = Instant.now();

        List<VideoTask> jobs = videoTaskService.getUnSuccessTask();

        if (ObjectUtils.isEmpty(jobs)) {
            emptyStreak++;
            currentDelayMs = calcDelayWhenEmpty(emptyStreak);
            log.info("本次无任务，emptyStreak={}，下次轮询间隔={}ms", emptyStreak, currentDelayMs);
            return;
        }

        // 有任务：立刻加快
        emptyStreak = 0;
        currentDelayMs = minDelayMs;
        log.info("本次待轮询任务数={}，下次轮询间隔={}ms", jobs.size(), currentDelayMs);

        for (VideoTask job : jobs) {
            try {
                pollOne(job, now);
            } catch (Exception e) {
                // 不要 throw，避免整个 tick 中断 + 影响调度
                log.error("轮询单个任务失败 jobId={}", job.getId(), e);
            }
        }
    }

    private long calcDelayWhenEmpty(int emptyStreak) {
        // 指数退避：10s, 20s, 40s, 80s, 120s(封顶)...
        long delay = baseDelayMs * (1L << Math.min(emptyStreak - 1, 4)); // 最多放大 16 倍
        return Math.min(maxDelayMs, delay);
    }

    private void pollOne(VideoTask job, Instant now) throws Exception {
        // 旧状态
        String oldStatus = job.getStatus();

        // 3) 查第三方状态
        VideoTask videoTask = videoTaskService.getTaskByTaskId(job.getId());

        // 4) 根据状态写库 + 推送
        if (videoTask.getStatus().toUpperCase().contains("SUCCESS") || videoTask.getStatus().toUpperCase().contains("SUCCEEDED")) {
            // 根据状态写库
            videoTaskService.updateTaskAndProperty(videoTask);
            //  推送消息
            taskStatusWebSocketHandler.sendInfo(videoTask.getProjectId(), videoTask);
            return;
        }

        if (!oldStatus.equals(videoTask.getStatus())) { // 状态改变则更新状态
            videoTaskService.updateById(videoTask);
            taskStatusWebSocketHandler.sendInfo(videoTask.getProjectId(), videoTask);
        }

    }
}
