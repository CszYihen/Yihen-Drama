package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.config.properties.MinioProperties;
import com.yihen.constant.MinioConstant;
import com.yihen.core.model.strategy.video.VideoModelFactory;
import com.yihen.core.model.strategy.video.VideoModelStrategy;
import com.yihen.core.model.strategy.video.dto.VideoTaskDTO;
import com.yihen.entity.Characters;
import com.yihen.entity.Storyboard;
import com.yihen.entity.VideoTask;
import com.yihen.enums.TaskType;
import com.yihen.http.HttpExecutor;
import com.yihen.mapper.StoryboardMapper;
import com.yihen.mapper.VideoTaskMapper;
import com.yihen.service.CharacterService;
import com.yihen.service.ModelManageService;
import com.yihen.service.StoryboardService;
import com.yihen.service.VideoTaskService;
import com.yihen.util.MinioUtil;
import com.yihen.util.UrlUtils;
import com.yihen.websocket.TaskStatusWebSocketHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class VideoTaskServiceImpl extends ServiceImpl<VideoTaskMapper, VideoTask> implements VideoTaskService {

    @Autowired
    private HttpExecutor httpExecutor;



    @Autowired
    private VideoModelFactory videoModelFactory;

    @Autowired
    private StoryboardMapper storyboardMapper;

    @Autowired
    private CharacterService characterService;



    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);  // 根据需要调整线程池大小


    @Override
    public VideoTask getTaskByTaskId(Long id) throws Exception {
        VideoTask videoTask = getById(id);
        VideoModelStrategy strategy = videoModelFactory.getStrategy(videoTask.getInstanceId());
        VideoTaskDTO videoTaskDTO = strategy.queryTask(videoTask.getTaskId(), videoTask.getInstanceId());
        videoTask.setErrorMessage(videoTaskDTO.getMessage());
        videoTask.setVideoUrl(videoTaskDTO.getVideoUrl());
        videoTask.setStatus(videoTaskDTO.getStatus());
        return videoTask;
    }

    @Override
    public List<VideoTask> getProjectVideoTasksByProjectId(Long projectId) throws Exception {
        // 获取该项目下所有任务
        LambdaQueryWrapper<VideoTask> videoTaskLambdaQueryWrapper = new LambdaQueryWrapper<VideoTask>().eq(VideoTask::getProjectId, projectId);
        List<VideoTask> videoTasks = list(videoTaskLambdaQueryWrapper);

        // 创建一个存储所有异步任务的列表
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 轮训任务（videoUrl为空即未成功）
        for (VideoTask videoTask : videoTasks) {
            if (ObjectUtils.isEmpty(videoTask.getVideoUrl())) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    // 这里的策略调用变为异步操作
                    VideoModelStrategy strategy = videoModelFactory.getStrategy(videoTask.getInstanceId());
                    try {
                        VideoTaskDTO videoTaskDTO = strategy.queryTask(videoTask.getTaskId(), videoTask.getInstanceId());
                        
                        // 保存旧状态用于判断是否需要推送
                        String oldStatus = videoTask.getStatus();
                        
                        // 更新 videoTask
                        videoTask.setVideoUrl(videoTaskDTO.getVideoUrl());
                        videoTask.setStatus(videoTaskDTO.getStatus());



                        // 根据类型更新对应数据库信息
                        if (videoTask.getTaskType().equals(TaskType.CHARACTER_VIDEO_GENERATION)) {
                            LambdaUpdateWrapper<Characters> charactersLambdaUpdateWrapper = new LambdaUpdateWrapper<Characters>().eq(Characters::getId, videoTask.getTargetId())
                                    .set(Characters::getVideoUrl, videoTask.getVideoUrl());
                            characterService.update(charactersLambdaUpdateWrapper);
                        } else if (videoTask.getTaskType().equals(TaskType.SHOT_VIDEO_GENERATION)) {
                            // TODO 更新分镜视频信息
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }, executorService);
                futures.add(future);
            }
        }

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();



        return videoTasks;
    }

    @Override
    public List<VideoTask> getUnSuccessTask(){
        LambdaQueryWrapper<VideoTask> videoTaskLambdaQueryWrapper = new LambdaQueryWrapper<VideoTask>().in(VideoTask::getStatus, "QUEUED", "RUNNING");

        return list(videoTaskLambdaQueryWrapper);
    }

    @Override
    public void updateTaskAndProperty(VideoTask videoTask) throws Exception {
        // 查询任务类型
        TaskType taskType = videoTask.getTaskType();

        // 关联的是角色资产，则更新角色
        if (taskType.equals(TaskType.CHARACTER_VIDEO_GENERATION)) {
            Characters characters = characterService.getById(videoTask.getTargetId());
            characters = saveGeneratedCharacterVideoToMinio(videoTask.getVideoUrl(), characters);
            videoTask.setVideoUrl(characters.getVideoUrl());
            characterService.updateById(characters);
        }
        else if (taskType.equals(TaskType.SHOT_VIDEO_GENERATION)) {
            // 分镜视频更新
            Storyboard storyboard = storyboardMapper.selectById(videoTask.getTargetId());
            storyboard = saveGeneratedShotVideoToMinio(videoTask.getVideoUrl(), storyboard,videoTask.getProjectId());
            videoTask.setVideoUrl(storyboard.getVideoUrl());
            storyboardMapper.updateById(storyboard);
        }



        // 更新任务
        updateById(videoTask);


    }

    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private MinioProperties minioProperties;


    // 将生成的视频保存到本地minio
    public Characters saveGeneratedCharacterVideoToMinio(String url,Characters characters) throws Exception {
        String extension = UrlUtils.extractFileExtension(url);
        if (extension == null || extension.isBlank()) {
            extension = "mp4";
        }
        // 角色图片路径 /project/{projectId}/characters/{charactersId-charactersName.xxx}
        String imgName = characters.getId() + "-" +characters.getName() + "." + extension;
        String objectName = MinioConstant.CHARACTER_IMG_PATH.formatted(characters.getProjectId(), imgName);

        // 下载
        ResponseEntity<Resource> resp = httpExecutor.downloadVideoResource(url).block();
        if (com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isEmpty(resp) || com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isEmpty(resp.getBody()) ) {
            throw new IllegalStateException("下载视频失败：响应为空");
        }
        long size = resp.getHeaders().getContentLength(); // 可能是 -1
        String contentType = resp.getHeaders().getContentType() != null
                ? resp.getHeaders().getContentType().toString()
                : guessVideoContentType(extension);

        // 4) 上传到 MinIO（流式）
        try (InputStream in = resp.getBody().getInputStream()) {
            // ✅ 推荐：minioUtil 支持 contentType
            minioUtil.uploadFile(in, size > 0 ? size : -1, MinioConstant.BUCKET_NAME, objectName, contentType);

            // ✅ 兼容：如果你只有原来的 4 参数 uploadFile(InputStream, size, bucket, objectName)
//            minioUtil.uploadFile(in, size > 0 ? (int) size : -1, MinioConstant.BUCKET_NAME, objectName);
        }

        // 回写访问地址
        String videoUrl = minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/" + objectName;

        characters.setVideoUrl(videoUrl);

        return characters;
    }


    // 将生成的分镜视频保存到本地minio
    public Storyboard saveGeneratedShotVideoToMinio(String url, Storyboard storyboard,Long projectId) throws Exception {
        String extension = UrlUtils.extractFileExtension(url);
        if (extension == null || extension.isBlank()) {
            extension = "mp4";
        }
        // 分镜图片路径 /project/{projectId}/shots/episode-{episodeId}/{shotId-shotIndex.xxx}
        String imgName = storyboard.getId() + "-" +"shot"+storyboard.getShotNumber() + "." + extension;

        String objectName = MinioConstant.SHOT_IMG_PATH.formatted(projectId,storyboard.getEpisodeId(), imgName);

        // 下载
        ResponseEntity<Resource> resp = httpExecutor.downloadVideoResource(url).block();
        if (com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isEmpty(resp) || com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isEmpty(resp.getBody()) ) {
            throw new IllegalStateException("下载视频失败：响应为空");
        }
        long size = resp.getHeaders().getContentLength(); // 可能是 -1
        String contentType = resp.getHeaders().getContentType() != null
                ? resp.getHeaders().getContentType().toString()
                : guessVideoContentType(extension);

        // 4) 上传到 MinIO（流式）
        try (InputStream in = resp.getBody().getInputStream()) {
            // ✅ 推荐：minioUtil 支持 contentType
            minioUtil.uploadFile(in, size > 0 ? size : -1, MinioConstant.BUCKET_NAME, objectName, contentType);

            // ✅ 兼容：如果你只有原来的 4 参数 uploadFile(InputStream, size, bucket, objectName)
//            minioUtil.uploadFile(in, size > 0 ? (int) size : -1, MinioConstant.BUCKET_NAME, objectName);
        }

        // 回写访问地址
        String videoUrl = minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/" + objectName;

        storyboard.setVideoUrl(videoUrl);

        return storyboard;
    }

    private String guessVideoContentType(String ext) {
        ext = ext == null ? "" : ext.toLowerCase(Locale.ROOT);
        return switch (ext) {
            case "mp4" -> "video/mp4";
            case "webm" -> "video/webm";
            case "mov" -> "video/quicktime";
            case "mkv" -> "video/x-matroska";
            default -> "application/octet-stream";
        };

    }
}
