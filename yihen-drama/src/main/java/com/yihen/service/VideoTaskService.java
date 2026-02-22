package com.yihen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.VideoTask;

import java.util.List;
import java.util.Optional;

public interface VideoTaskService extends IService<VideoTask> {

    // 查询任务信息
    VideoTask getTaskByTaskId(Long id) throws Exception;

    List<VideoTask> getProjectVideoTasksByProjectId(Long projectId) throws Exception;

    // 查询为完成的任务
    List<VideoTask> getUnSuccessTask() ;

    // 更新任务及其关联的资产
    void updateTaskAndProperty(VideoTask videoTask) throws Exception;
}
