package com.yihen.core.model.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.yihen.asyn.EpisodePersistFacade;
import com.yihen.constant.characters.CharactersMQConstant;
import com.yihen.constant.episode.EpisodeMQConstant;
import com.yihen.controller.vo.*;
import com.yihen.core.model.strategy.video.VideoModelFactory;
import com.yihen.core.model.strategy.video.VideoModelStrategy;
import com.yihen.util.CheckUtils;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
public class EpisodeExtractOrchestrator {

    @Autowired
    private EpisodeService episodeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private InfoExtractTextModelService infoExtractService;

    @Autowired
    private EpisodePersistFacade episodePersistFacade;


    @Autowired
    @Qualifier("episodeExecutor")
    private Executor episodeExecutor;

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
        // 4. 保存对应信息
        characterService.saveBatch(extract.getCharacters().stream().filter(Characters::isNew).toList());
        sceneService.saveBatch(extract.getScenes().stream().filter(Scene::isNew).toList());

        // 创建异步任务，异步更新数据库
        episodePersistFacade.updateEpisodeCurrentStepAsync(episode,EpisodeStep.EXTRACT_INFO);

        //  发送消息 章节内容存入向量数据库
        rabbitTemplate.convertAndSend(EpisodeMQConstant.EPISODE_EXCHANGE, EpisodeMQConstant.EPISODE_VECTOR_ADD_KEY, episode);


        return extract;
    }

    // 生成人物形象图片并保存
    public Characters generateCharacterAndSaveAssets(CharactersRequestVO charactersRequestVO) throws Exception {
        Characters characters = propertyGenerateImgModelService.generateCharacter(charactersRequestVO);
        // 创建异步任务，异步更新数据库
        episodePersistFacade.updateEpisodeCharacterAsync(characters);

        return characters;
    }


    public void generateCharacterAndSaveAssetsAsync(
            List<CharactersRequestVO> requestList,
            Consumer<Characters> onSuccess,
            BiConsumer<CharactersRequestVO, Throwable> onError
    ) {


        if (ObjectUtils.isEmpty(requestList)) {
            throw new RuntimeException("批量生成人物请求不能为空");
        }
        if (requestList.size() > 20) {
            throw new RuntimeException("单次批量生成不能超过20个角色");
        }

        requestList.forEach(request ->
                CompletableFuture.runAsync(() -> {
                    try {
                        Characters characters = generateCharacterAndSaveAssets(request);
                        if (onSuccess != null) {
                            onSuccess.accept(characters);
                        }
                    } catch (Exception e) {
                        if (onError != null) {
                            onError.accept(request, e);
                        }
                    }
                }, episodeExecutor)
        );
    }

    public void generateSceneAndSaveAssetsAsync(
            List<SceneRequestVO> requestList,
            Consumer<Scene> onSuccess,
            BiConsumer<SceneRequestVO, Throwable> onError
    ) {
        if (ObjectUtils.isEmpty(requestList)) {
            throw new RuntimeException("批量生成场景请求不能为空");
        }
        if (requestList.size() > 20) {
            throw new RuntimeException("单次批量生成不能超过20个场景");
        }

        requestList.forEach(request ->
                CompletableFuture.runAsync(() -> {
                    try {
                        Scene scene = generateSceneAndSaveAssets(request);
                        if (onSuccess != null) {
                            onSuccess.accept(scene);
                        }
                    } catch (Exception e) {
                        if (onError != null) {
                            onError.accept(request, e);
                        }
                    }
                }, episodeExecutor)
        );
    }

   // 创建生成人物视频人物
    public VideoTask createCharacterVideoTaskAndSaveAssets(CharactersRequestVO charactersRequestVO) throws Exception {
        // 非空校验
        if (ObjectUtils.isEmpty(charactersRequestVO.getModelInstanceId())) {
            throw new RuntimeException("模型实例id不能为空");
        }
        if (ObjectUtils.isEmpty(charactersRequestVO.getCharacterId())) {
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
        videoTask.setTargetId(charactersRequestVO.getCharacterId());
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
