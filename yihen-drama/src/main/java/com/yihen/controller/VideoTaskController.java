package com.yihen.controller;

import com.yihen.common.Result;
import com.yihen.entity.VideoTask;
import com.yihen.service.VideoTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "视频任务接口", description = "视频生成任务管理")
@RestController
@RequestMapping("/api/video-tasks")
@Slf4j
public class VideoTaskController {

    @Resource
    private VideoTaskService videoTaskService;


    @Operation(summary = "获取视频任务详情")
    @GetMapping("/{id}")
    public Result<VideoTask> get(@Parameter(description = "视频任务ID",example = "1") @PathVariable Long id) throws Exception {
        VideoTask videoTask = videoTaskService.getTaskByTaskId(id);
        return Result.success(videoTask);
    }

    @Operation(summary = "获取项目视频任务列表")
    @GetMapping("/project/{projectId}")
    public Result<List<VideoTask>> getProjectVideoTasks(@Parameter(description = "项目ID",example = "1") @PathVariable Long projectId) throws Exception {
        List<VideoTask> videoTasks = videoTaskService.getProjectVideoTasksByProjectId(projectId);
        return Result.success(videoTasks);
    }

}
