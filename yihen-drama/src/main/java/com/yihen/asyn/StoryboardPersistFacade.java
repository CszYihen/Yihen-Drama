package com.yihen.asyn;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihen.config.properties.MinioProperties;
import com.yihen.constant.episode.EpisodeRedisConstant;
import com.yihen.constant.MinioConstant;
import com.yihen.entity.*;
import com.yihen.enums.EpisodeStep;
import com.yihen.enums.TaskType;
import com.yihen.http.HttpExecutor;
import com.yihen.mapper.EpisodeMapper;
import com.yihen.mapper.StoryboardMapper;
import com.yihen.mapper.VideoTaskMapper;
import com.yihen.service.StoryBoardCharacterService;
import com.yihen.service.StoryBoardSceneService;
import com.yihen.util.MinioUtil;
import com.yihen.util.RedisUtils;
import com.yihen.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoryboardPersistFacade {
    @Autowired
    private HttpExecutor httpExecutor;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private VideoTaskMapper videoTaskMapper;

    @Autowired
    private StoryboardMapper storyboardMapper; // 你当前这个 service 或 mapper
    @Autowired
    private StoryBoardCharacterService storyBoardCharacterService;
    @Autowired
    private StoryBoardSceneService storyBoardSceneService;

    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 保存生成的分镜
     */
    @Async("storyboardExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void persistAsync(Long episodeId, Long projectId, List<Storyboard> storyboards) {

        // 1) 更新章节状态（建议你明确逻辑：生成分镜后应该进入哪个 step）

        Episode episode = episodeMapper.selectById(episodeId);
        if (episode != null && episode.getCurrentStep() != null
                && episode.getCurrentStep().getCode().equals(EpisodeStep.GENERATE_IMAGES.getCode())) {
            episode.setCurrentStep(EpisodeStep.GENERATE_STORYBOARD);

            episodeMapper.updateById(episode);
            // 更新缓存
            redisUtils.updateHashPartial(EpisodeRedisConstant.EPISODE_INFO_KEY + episode.getId(),episode);

        }

        // 2) 保存分镜
        // ⚠️ 如果你后面要用 storyboard.id 建关联，必须确保插入后 id 可用
        // 最稳妥做法：逐条 insert（数量通常不大：10~50），性能完全够用
        storyboards.forEach(storyboard -> {
            storyboard.setEpisodeId(episodeId);
            storyboardMapper.insert(storyboard);
        });

        // 3) 批量构建关系表数据（避免 N+1）
        List<StoryBoardCharacter> scRelations =
                storyboards.stream()
                        .flatMap(sb -> {
                            List<Characters> cs = Optional.ofNullable(sb.getCharacters()).orElse(List.of());
                            return cs.stream()
                                    .filter(c -> c.getId() != null)
                                    .map(c -> {
                                        StoryBoardCharacter r = new StoryBoardCharacter();
                                        r.setStoryboardId(sb.getId());
                                        r.setEpisodeId(episodeId);
                                        r.setProjectId(projectId);
                                        r.setCharacterId(c.getId());
                                        return r;
                                    });
                        })
                        .collect(Collectors.toList());

        List<StoryBoardScene> ssRelations =
                storyboards.stream()
                        .flatMap(sb -> {
                            List<Scene> ss = Optional.ofNullable(sb.getScenes()).orElse(List.of());
                            return ss.stream()
                                    .filter(s -> s.getId() != null)
                                    .map(s -> {
                                        StoryBoardScene r = new StoryBoardScene();
                                        r.setStoryboardId(sb.getId());
                                        r.setEpisodeId(episodeId);
                                        r.setProjectId(projectId);
                                        r.setSceneId(s.getId());
                                        return r;
                                    });
                        })
                        .collect(Collectors.toList());

        // 4) 批量保存关系
        if (!scRelations.isEmpty()) {
            storyBoardCharacterService.saveBatch(scRelations, 500);
        }
        if (!ssRelations.isEmpty()) {
            storyBoardSceneService.saveBatch(ssRelations, 500);
        }
    }


    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 保存分镜 生图提示词
     */
    @Async("storyboardExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void updatePromptAsync(Storyboard storyboard) {
        // 修改章节状态
        Episode episode = episodeMapper.selectById(storyboard.getEpisodeId());
        if (episode.getCurrentStep().getCode().equals(EpisodeStep.GENERATE_STORYBOARD.getCode())) {
            episode.setCurrentStep(EpisodeStep.GENERATE_VIDEO);
            episodeMapper.updateById(episode);
            // 更新缓存
            redisUtils.updateHashPartial(EpisodeRedisConstant.EPISODE_INFO_KEY + episode.getId(),episode);

        }
        // 数据库分镜更新
        storyboardMapper.updateById(storyboard);
    }


    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 更新分镜 首帧图
     */
    @Async("storyboardExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void updateFirstFrameAsync(Storyboard storyboard,Long projectId) throws Exception {
        // 持久化存储到Minio
        String url = storyboard.getThumbnail();
        String extension = UrlUtils.extractFileExtension(url);
        // 场景图片路径 /project/{projectId}/shots/episode-{episodeId}/{shotId-shotIndex.xxx}
        String imgName = storyboard.getId() + "-"+"shot"+storyboard.getShotNumber() + "." + extension;
        String objectName = MinioConstant.SHOT_IMG_PATH.formatted(projectId, storyboard.getEpisodeId(), imgName);

        byte[] img = httpExecutor.downloadImage(url).block();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(img);
        minioUtil.uploadFile(byteArrayInputStream,
                img.length,
                MinioConstant.BUCKET_NAME, objectName);

        String thumbnail = minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/" + objectName;
        storyboard.setThumbnail(thumbnail);

        // 修改章节状态
        Episode episode = episodeMapper.selectById(storyboard.getEpisodeId());
        if (episode.getCurrentStep().getCode().equals(EpisodeStep.GENERATE_STORYBOARD.getCode())) {
            episode.setCurrentStep(EpisodeStep.GENERATE_VIDEO);
            episodeMapper.updateById(episode);
        }
        // 数据库分镜更新
        storyboardMapper.updateById(storyboard);
    }


    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 更新分镜 更新分镜关联数据
     */
    @Async("storyboardExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void updateShotAssociatedInfoAsync(Storyboard storyboard) {
        // 1. 先删除所有对应的 storyboard_character
        LambdaQueryWrapper<StoryBoardCharacter> storyBoardCharacterLambdaQueryWrapper = new LambdaQueryWrapper<StoryBoardCharacter>().eq(StoryBoardCharacter::getStoryboardId, storyboard.getId());
        storyBoardCharacterService.remove(storyBoardCharacterLambdaQueryWrapper);

        // 2. 添加新的 storyboard_character
        List<Characters> characters = storyboard.getCharacters();
        for (Characters character : characters) {
            StoryBoardCharacter storyBoardCharacter = new StoryBoardCharacter();
            storyBoardCharacter.setStoryboardId(storyboard.getId());
            storyBoardCharacter.setCharacterId(character.getId());
            storyBoardCharacter.setEpisodeId(storyboard.getEpisodeId());
            storyBoardCharacter.setProjectId(character.getProjectId());
            storyBoardCharacterService.save(storyBoardCharacter);
        }

        // 3. 先删除所有对应的 storyboard_scene
        LambdaQueryWrapper<StoryBoardScene> storyBoardSceneLambdaQueryWrapper = new LambdaQueryWrapper<StoryBoardScene>().eq(StoryBoardScene::getStoryboardId, storyboard.getId());
        storyBoardSceneService.remove(storyBoardSceneLambdaQueryWrapper);

        // 4. 添加新的 storyboard_scene
        List<Scene> scenes = storyboard.getScenes();
        for (Scene scene : scenes) {
            StoryBoardScene storyBoardScene = new StoryBoardScene();
            storyBoardScene.setStoryboardId(storyboard.getId());
            storyBoardScene.setSceneId(scene.getId());
            storyBoardScene.setEpisodeId(storyboard.getEpisodeId());
            storyBoardScene.setProjectId(scene.getProjectId());
            storyBoardSceneService.save(storyBoardScene);
        }

        storyboardMapper.updateById(storyboard);
    }

    public void deleteShotAssociatedInfoAsync(Long shotId) {
        // 删除对应 storyboard_scene
        LambdaQueryWrapper<StoryBoardScene> storyBoardSceneLambdaQueryWrapper = new LambdaQueryWrapper<StoryBoardScene>().eq(StoryBoardScene::getStoryboardId, shotId);
        storyBoardSceneService.remove(storyBoardSceneLambdaQueryWrapper);

        // 删除对应 storyboard_character
        LambdaQueryWrapper<StoryBoardCharacter> storyBoardCharacterLambdaQueryWrapper = new LambdaQueryWrapper<StoryBoardCharacter>().eq(StoryBoardCharacter::getStoryboardId, shotId);
        storyBoardCharacterService.remove(storyBoardCharacterLambdaQueryWrapper);

        // 删除对应的 视频生成任务
        LambdaQueryWrapper<VideoTask> lambdaQueryWrapper = new LambdaQueryWrapper<VideoTask>()
                .eq(VideoTask::getTaskType, TaskType.SHOT_VIDEO_GENERATION)
                .eq(VideoTask::getTargetId, shotId);
        videoTaskMapper.delete(lambdaQueryWrapper);
    }
}
