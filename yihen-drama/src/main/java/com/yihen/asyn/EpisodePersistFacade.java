package com.yihen.asyn;

import com.yihen.config.properties.MinioProperties;
import com.yihen.constant.MinioConstant;
import com.yihen.constant.episode.EpisodeRedisConstant;
import com.yihen.constant.project.ProjectRedisConstant;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.entity.*;
import com.yihen.enums.EpisodeStep;
import com.yihen.http.HttpExecutor;
import com.yihen.mapper.EpisodeMapper;
import com.yihen.mapper.ProjectMapper;
import com.yihen.service.*;
import com.yihen.util.LambdaFieldUtils;
import com.yihen.util.MinioUtil;
import com.yihen.util.RedisUtils;
import com.yihen.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

@Service
public class EpisodePersistFacade {



    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ProjectMapper projectMapper;



    @Autowired
    private CharacterService characterService;

    @Autowired
    private SceneService sceneService;


    @Autowired
    private StoryboardService storyboardService;

    @Autowired
    private HttpExecutor httpExecutor;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MinioProperties minioProperties;
    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 删除章节对应资产
     */
    @Async("episodeExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void deleteRelatedContentAsync(Long episodeId) {

        // 修改项目章节数量
        Long projectId = episodeMapper.getProjectIdByEpisodeId(episodeId);
        projectMapper.reduceEpisodeCount(projectId);
        // 更新缓存章节数量
        redisUtils.hSubtract(ProjectRedisConstant.PROJECT_INFO_KEY + projectId, LambdaFieldUtils.resolveFieldName(Project::getChapterCount),1);

        // 删除对应资产： 角色+场景
        characterService.deleteCharactersByEpisodeId(episodeId);
        sceneService.deleteScenesByEpisodeId(episodeId);
        // 删除对应资产： 分镜
        storyboardService.deleteShotByEpisodeId(episodeId);

    }


    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 增加章节数量
     */
    @Async("episodeExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void addEpisodeCountAsync(Long projectId) {

        projectMapper.addEpisodeCount(projectId);
        // 更新缓存章节数量
        redisUtils.hIncrement(ProjectRedisConstant.PROJECT_INFO_KEY + projectId, LambdaFieldUtils.resolveFieldName(Project::getChapterCount),1);

    }

    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 更新生成场景
     */
    @Async("episodeExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void updateEpisodeSceneAsync(Scene scene) throws Exception {

        Scene scene_ = saveGeneratedSceneImagesToMinio(scene.getThumbnail(), scene);
        sceneService.updateById(scene_);

        // 修改章节状态
        Long episodeId = scene_.getEpisodeId();
        Episode episode = episodeMapper.selectById(episodeId);
        if (episode.getCurrentStep().getCode() < EpisodeStep.GENERATE_IMAGES.getCode()) {
            episode.setCurrentStep(EpisodeStep.GENERATE_IMAGES);
            episodeMapper.updateById(episode);
            // 更新章节缓存
            redisUtils.updateHashPartial(EpisodeRedisConstant.EPISODE_INFO_KEY + episode.getId(), episode);
        }
    }





    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 更新生成图片
     */
    @Async("episodeExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void updateEpisodeCharacterAsync(Characters characters) throws Exception {

        Characters characters_ = saveGeneratedCharacterImagesToMinio(characters.getAvatar(), characters);
        characterService.updateById(characters_);

        // 修改章节状态
        Long episodeId = characters_.getEpisodeId();
        Episode episode = episodeMapper.selectById(episodeId);
        if (episode.getCurrentStep().getCode() < EpisodeStep.GENERATE_IMAGES.getCode()) {
            episode.setCurrentStep(EpisodeStep.GENERATE_IMAGES);
            episodeMapper.updateById(episode);
            // 更新章节缓存
            redisUtils.updateHashPartial(EpisodeRedisConstant.EPISODE_INFO_KEY + episode.getId(), episode);

        }

    }


    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 保存提取的资产信息
     */
    @Async("episodeExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void updateEpisodeCurrentStepAsync( Episode episode, EpisodeStep episodeStep) throws Exception {

        // 5. 修改章节状态
        if (episode.getCurrentStep().getCode() < episodeStep.getCode()) {
            episode.setCurrentStep(episodeStep);
            episodeMapper.updateById(episode);
            // 更新章节缓存
            redisUtils.updateHashPartial(EpisodeRedisConstant.EPISODE_INFO_KEY + episode.getId(), episode);

        }


    }

    // 将生成的场景图片保存到本地minio
    public Scene saveGeneratedSceneImagesToMinio(String url,Scene scene) throws Exception {
        String extension = UrlUtils.extractFileExtension(url);
        // 场景图片路径 /project/{projectId}/scenes/{charactersId-charactersName.xxx}
        String imgName = scene.getId() + "-" +scene.getName() + "." + extension;
        String objectName = MinioConstant.SCENE_IMG_PATH.formatted(scene.getProjectId(), imgName);

        byte[] img = httpExecutor.downloadImage(url).block();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(img);
        minioUtil.uploadFile(byteArrayInputStream,
                img.length,
                MinioConstant.BUCKET_NAME, objectName);

        String thumbnail = minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/" + objectName;
        scene.setThumbnail(thumbnail);

        return scene;
    }


    // 将生成的角色图片保存到本地minio
    public Characters saveGeneratedCharacterImagesToMinio(String url,Characters characters) throws Exception {
        String extension = UrlUtils.extractFileExtension(url);
        // 角色图片路径 /project/{projectId}/characters/{charactersId-charactersName.xxx}
        String imgName = characters.getId() + "-" +characters.getName() + "." + extension;
        String objectName = MinioConstant.CHARACTER_IMG_PATH.formatted(characters.getProjectId(), imgName);

        byte[] img = httpExecutor.downloadImage(url).block();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(img);
        minioUtil.uploadFile(byteArrayInputStream,
                img.length,
                MinioConstant.BUCKET_NAME, objectName);

        String avatar = minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/" + objectName;
        characters.setAvatar(avatar);

        return characters;
    }

}
