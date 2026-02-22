package com.yihen.core.model.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.yihen.asyn.EpisodePersistFacade;
import com.yihen.controller.vo.*;
import com.yihen.core.model.strategy.video.VideoModelFactory;
import com.yihen.core.model.strategy.video.VideoModelStrategy;
import com.yihen.util.UrlUtils;
import com.yihen.config.properties.MinioProperties;
import com.yihen.constant.MinioConstant;
import com.yihen.core.model.PropertyGenerateImgModelService;
import com.yihen.entity.*;
import com.yihen.enums.EpisodeStep;
import com.yihen.enums.SceneCode;
import com.yihen.http.HttpExecutor;
import com.yihen.service.*;
import com.yihen.core.model.InfoExtractTextModelService;
import com.yihen.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EpisodeExtractOrchestrator {

    @Autowired
    private EpisodeService episodeService;



    @Autowired
    private InfoExtractTextModelService infoExtractService;

    @Autowired
    private EpisodePersistFacade episodePersistFacade;


    private static final ExecutorService EXECUTORSERVICE = Executors.newFixedThreadPool(5);

    @Autowired
    private CharacterService characterService;

    @Autowired
    private VideoTaskService videoTaskService;

    @Autowired
    private PropertyGenerateImgModelService propertyGenerateImgModelService;



    @Autowired
    private SceneService sceneService;


    @Autowired
    private VideoModelFactory videoModelFactory;

    // 提取资产信息并保存
    public ExtractionResultVO extractAndSaveAssets(ExtractRequestVO request) throws Exception {
        // 1. 根据id获取章节信息
        Episode episode = episodeService.getById(request.getEpisodeId());

        if (ObjectUtils.isEmpty(episode)) {
            throw new RuntimeException("章节不存在");
        }

        // 2. 构建提取参数
        TextModelRequestVO textModelRequestVO = new TextModelRequestVO();
        textModelRequestVO.setModelId(request.getModelId());
        textModelRequestVO.setSceneCode(SceneCode.INFO_EXTRACT);
        textModelRequestVO.setText(episode.getContent());
        textModelRequestVO.setEpisodeId(episode.getId());
        textModelRequestVO.setProjectId(episode.getProjectId());

        // 3. 提取信息
        ExtractionResultVO extract = infoExtractService.extract(textModelRequestVO);

        extract.getCharacters().forEach(c->{
            c.setEpisodeId(episode.getId());
            c.setProjectId(episode.getProjectId());
        });
        extract.getScenes().forEach(s->{
            s.setEpisodeId(episode.getId());
            s.setProjectId(episode.getProjectId());
        });

        // 创建异步任务，异步更新数据库
        CompletableFuture.runAsync(() -> {
            // 过滤已经出现的角色和场景

            // 4. 保存对应信息
            characterService.saveBatch(extract.getCharacters().stream().filter(Characters::isNew).toList());
            sceneService.saveBatch(extract.getScenes().stream().filter(Scene::isNew).toList());
            // 5. 修改章节状态
            episode.setCurrentStep(EpisodeStep.GENERATE_IMAGES);
            episodeService.updateById(episode);
        }, EXECUTORSERVICE);

        return extract;
    }

    // 生成人物形象图片并保存
    public Characters generateCharacterAndSaveAssets(CharactersRequestVO charactersRequestVO) throws Exception {
        Characters characters = propertyGenerateImgModelService.generateCharacter(charactersRequestVO);
        // 创建异步任务，异步更新数据库
        episodePersistFacade.updateEpisodeCharacterAsync(characters);

        return characters;
    }

   // 创建生成人物视频人物
    public VideoTask createCharacterVideoTaskAndSaveAssets(CharactersRequestVO charactersRequestVO) throws Exception {
        // 非空校验
        if (ObjectUtils.isEmpty(charactersRequestVO.getModelInstanceId())) {
            throw new RuntimeException("模型实例id不能为空");
        }
        if (ObjectUtils.isEmpty(charactersRequestVO.getCharcterId())) {
            throw new RuntimeException("角色id不能为空");
        }
        if (ObjectUtils.isEmpty(charactersRequestVO.getProjectId())) {
            throw new RuntimeException("项目id不能为空");
        }

        VideoModelStrategy strategy = videoModelFactory.getStrategy(charactersRequestVO.getModelInstanceId());
        String taskId = strategy.create(charactersRequestVO);

        // TODO 如何异步保存？
        // 创建任务
        VideoTask videoTask = new VideoTask();
        videoTask.setProjectId(charactersRequestVO.getProjectId());
        videoTask.setTaskId(taskId);
        videoTask.setInstanceId(charactersRequestVO.getModelInstanceId());
        videoTask.setTargetId(charactersRequestVO.getCharcterId());
        videoTask.setNextPollAt(Date.from(Instant.now()));
        videoTask.setPollCount(0);
        videoTask.setStatus("QUEUED");




        // 5. 添加任务信息到数据库
        videoTaskService.save(videoTask);

        return videoTask;

    }


    // 生成场景图片并保存
    public Scene generateSceneAndSaveAssets(SceneRequestVO sceneRequestVO) throws Exception {
        Scene scene = propertyGenerateImgModelService.generateScene(sceneRequestVO);
        // 创建异步任务，异步更新数据库
        episodePersistFacade.updateEpisodeSceneAsync(scene);

        return scene;
    }








}
