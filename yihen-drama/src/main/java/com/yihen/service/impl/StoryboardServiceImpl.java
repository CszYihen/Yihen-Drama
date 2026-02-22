package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.asyn.StoryboardPersistFacade;
import com.yihen.controller.vo.ImageModelRequestVO;
import com.yihen.controller.vo.TextModelRequestVO;
import com.yihen.controller.vo.VideoModelRequestVO;
import com.yihen.core.model.FirstFrameGenerateTextModelService;
import com.yihen.core.model.ShotGenerateTextModelService;
import com.yihen.core.model.ShotVideoGenerateTextModelService;
import com.yihen.core.model.strategy.image.ImageModelFactory;
import com.yihen.core.model.strategy.image.ImageModelStrategy;
import com.yihen.core.model.strategy.video.VideoModelFactory;
import com.yihen.core.model.strategy.video.VideoModelStrategy;
import com.yihen.entity.*;
import com.yihen.enums.SceneCode;
import com.yihen.enums.TaskType;
import com.yihen.mapper.EpisodeMapper;
import com.yihen.mapper.StoryboardMapper;
import com.yihen.service.*;
import kotlin.jvm.internal.Lambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class StoryboardServiceImpl extends ServiceImpl<StoryboardMapper, Storyboard> implements StoryboardService {


    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private StoryboardPersistFacade storyboardPersistFacade;

    @Autowired
    private StoryBoardCharacterService storyBoardCharacterService;

    @Autowired
    private StoryBoardSceneService storyBoardSceneService;

    @Autowired
    private ShotGenerateTextModelService shotGenerateTextModelService;

    @Autowired
    private CharacterService characterService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private VideoTaskService videoTaskService;

    @Autowired
    private FirstFrameGenerateTextModelService firstFrameGenerateTextModelService;

    @Autowired
    private ShotVideoGenerateTextModelService shotVideoGenerateTextModelService;

    @Autowired
    private ImageModelFactory imageModelFactory;

    @Autowired
    private VideoModelFactory videoModelFactory;

    @Autowired
    private StoryboardMapper storyboardMapper;


    @Override
    public List<Storyboard> getStoryboardsByEpisodeId(Long episodeId) {

        // 1️⃣ 查询分镜主体
        List<Storyboard> storyboards = list(
                new LambdaQueryWrapper<Storyboard>()
                        .eq(Storyboard::getEpisodeId, episodeId)
                        .orderByAsc(Storyboard::getOrderIndex)
        );

        if (storyboards.isEmpty()) {
            return storyboards;
        }

        // storyboardId 集合
        List<Long> storyboardIds = storyboards.stream()
                .map(Storyboard::getId)
                .collect(Collectors.toList());

        // 2️⃣ 查询 分镜-角色 关系
        List<StoryBoardCharacter> scRelations =
                storyBoardCharacterService.list(
                        new LambdaQueryWrapper<StoryBoardCharacter>()
                                .eq(StoryBoardCharacter::getEpisodeId, episodeId)
                                .in(StoryBoardCharacter::getStoryboardId, storyboardIds)
                );

        // 3️⃣ 查询 分镜-场景 关系
        List<StoryBoardScene> ssRelations =
                storyBoardSceneService.list(
                        new LambdaQueryWrapper<StoryBoardScene>()
                                .eq(StoryBoardScene::getEpisodeId, episodeId)
                                .in(StoryBoardScene::getStoryboardId, storyboardIds)
                );

        // 4️⃣ 批量查角色 / 场景资产
        Map<Long, Characters> characterMap;
        if (!scRelations.isEmpty()) {
            List<Long> characterIds = scRelations.stream()
                    .map(StoryBoardCharacter::getCharacterId)
                    .distinct()
                    .collect(Collectors.toList());

            characterMap = characterService.listByIds(characterIds).stream()
                    .collect(Collectors.toMap(
                            Characters::getId,
                            Function.identity()
                    ));
        } else {
            characterMap = Map.of();
        }

        Map<Long, Scene> sceneMap;
        if (!ssRelations.isEmpty()) {
            List<Long> sceneIds = ssRelations.stream()
                    .map(StoryBoardScene::getSceneId)
                    .distinct()
                    .collect(Collectors.toList());

            sceneMap = sceneService.listByIds(sceneIds).stream()
                    .collect(Collectors.toMap(
                            Scene::getId,
                            Function.identity()
                    ));
        } else {
            sceneMap = Map.of();
        }

        // 5️⃣ 关系分组（storyboardId -> List）
        Map<Long, List<StoryBoardCharacter>> scGroup =
                scRelations.stream()
                        .collect(Collectors.groupingBy(
                                StoryBoardCharacter::getStoryboardId
                        ));

        Map<Long, List<StoryBoardScene>> ssGroup =
                ssRelations.stream()
                        .collect(Collectors.groupingBy(
                                StoryBoardScene::getStoryboardId
                        ));

        // 6️⃣ 回填到 storyboard
        storyboards.forEach(sb -> {

            List<Characters> characters =
                    Optional.ofNullable(scGroup.get(sb.getId()))
                            .orElse(List.of())
                            .stream()
                            .map(rel -> characterMap.get(rel.getCharacterId()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

            sb.setCharacters(characters);

            List<Scene> scenes =
                    Optional.ofNullable(ssGroup.get(sb.getId()))
                            .orElse(List.of())
                            .stream()
                            .map(rel -> sceneMap.get(rel.getSceneId()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

            sb.setScenes(scenes);
        });

        return storyboards;
    }

    @Override
    public Storyboard getStoryboardsById(Long id) {
        // 查询分镜
        Storyboard storyboard = getById(id);

        // 2️⃣ 查询 分镜-角色 关系
        List<StoryBoardCharacter> scRelations =
                storyBoardCharacterService.list(
                        new LambdaQueryWrapper<StoryBoardCharacter>()
                                .eq(StoryBoardCharacter::getEpisodeId, storyboard.getEpisodeId())
                                .in(StoryBoardCharacter::getStoryboardId,id)
                );

        // 3️⃣ 查询 分镜-场景 关系
        List<StoryBoardScene> ssRelations =
                storyBoardSceneService.list(
                        new LambdaQueryWrapper<StoryBoardScene>()
                                .eq(StoryBoardScene::getEpisodeId, storyboard.getEpisodeId())
                                .in(StoryBoardScene::getStoryboardId, id)
                );

        // 4️⃣ 批量查角色
        List<Characters> characters;
        if (!scRelations.isEmpty()) {
            List<Long> characterIds = scRelations.stream()
                    .map(StoryBoardCharacter::getCharacterId)
                    .distinct()
                    .collect(Collectors.toList());

            characters = characterService.listByIds(characterIds);
        } else {
            characters = List.of();
        }

        // 回填到分镜
        storyboard.setCharacters(characters);

        List<Scene> scenes;
        if (!ssRelations.isEmpty()) {
            List<Long> sceneIds = ssRelations.stream()
                    .map(StoryBoardScene::getSceneId)
                    .distinct()
                    .collect(Collectors.toList());

            scenes = sceneService.listByIds(sceneIds);
        } else {
            scenes = List.of();
        }

        // 回填到分镜
        storyboard.setScenes(scenes);

        return storyboard;
    }


    @Override
    public List<Storyboard> generate(Long episodeId,Long projectId , Long modelId) throws Exception {
        // 1. 获取章节内容
        String content = episodeMapper.getContentByEpisodeId(episodeId);
        TextModelRequestVO textModelRequestVO = new TextModelRequestVO();
        textModelRequestVO.setModelId(modelId);
        textModelRequestVO.setText(content);
        textModelRequestVO.setSceneCode(SceneCode.STORYBOARD_GEN);
        textModelRequestVO.setProjectId(projectId);

        List<Storyboard> storyboards = shotGenerateTextModelService.extract(textModelRequestVO);

        // 异步保存
        storyboardPersistFacade.persistAsync(episodeId,projectId,storyboards);

        return storyboards;
    }

    @Override
    public Storyboard generateFirstFramePrompt(Long id,Long projectId , Long modelId) throws Exception {
        // 1. 获取分镜对象

        Storyboard storyboard = getStoryboardsById(id);
        TextModelRequestVO textModelRequestVO = new TextModelRequestVO();
        textModelRequestVO.setSceneCode(SceneCode.FIRST_FRAME_GEN);
        textModelRequestVO.setEpisodeId(storyboard.getEpisodeId());
        textModelRequestVO.setModelId(modelId);
        textModelRequestVO.setProjectId(projectId);
        textModelRequestVO.setObject(storyboard);
        String response = firstFrameGenerateTextModelService.extract(textModelRequestVO);


        storyboard.setImagePrompt(response);

        // 创建异步任务，异步更新数据库
        storyboardPersistFacade.updatePromptAsync(storyboard);

        return storyboard;
    }

    @Override
    public Storyboard generateFirstFrame(Long shotId, Long projectId, Long modelInstanceId) throws Exception {
        // 获取对应的策略模型
        ImageModelStrategy strategy = imageModelFactory.getStrategy(modelInstanceId);

        // 获取分镜
        Storyboard storyboard = getStoryboardsById(shotId);

        ImageModelRequestVO imageModelRequestVO = new ImageModelRequestVO();
        imageModelRequestVO.setModelInstanceId(modelInstanceId);
        imageModelRequestVO.setObject(storyboard);

        String byTextAndImage = strategy.createByTextAndImage(imageModelRequestVO);

        storyboard.setThumbnail(byTextAndImage);

        //  更新数据
        storyboardPersistFacade.updateFirstFrameAsync(storyboard,projectId);
        return storyboard;
    }

    @Override
    public Storyboard generateShotVideoPrompt(Long shotId, Long projectId, Long modelInstanceId) throws Exception {
        // 1. 获取分镜对象

        Storyboard storyboard = getStoryboardsById(shotId);
        TextModelRequestVO textModelRequestVO = new TextModelRequestVO();
        textModelRequestVO.setSceneCode(SceneCode.VIDEO_GEN);
        textModelRequestVO.setEpisodeId(storyboard.getEpisodeId());
        textModelRequestVO.setModelId(modelInstanceId);
        textModelRequestVO.setProjectId(projectId);
        textModelRequestVO.setObject(storyboard);
        String response = shotVideoGenerateTextModelService.extract(textModelRequestVO);


        storyboard.setVideoPrompt(response);

        // 创建异步任务，异步更新数据库
        storyboardPersistFacade.updatePromptAsync(storyboard);

        return storyboard;
    }

    @Override
    public VideoTask createShotVideoTask(Long shotId, Long projectId, Long modelInstanceId, Map<String,Object> params) throws Exception {
        // 非空校验
        if (ObjectUtils.isEmpty(modelInstanceId)) {

            throw new RuntimeException("模型实例id不能为空");
        }
        if (ObjectUtils.isEmpty(shotId)) {

            throw new RuntimeException("分镜id不能为空");
        }
        if (ObjectUtils.isEmpty(projectId)) {
            throw new RuntimeException("项目id不能为空");
        }

        Storyboard storyboard = getStoryboardsById(shotId);
        VideoModelRequestVO videoModelRequestVO = new VideoModelRequestVO();
        videoModelRequestVO.setProjectId(projectId);
        videoModelRequestVO.setObject(storyboard);
        videoModelRequestVO.setModelInstanceId(modelInstanceId);
        videoModelRequestVO.setParams(params);

        VideoModelStrategy strategy = videoModelFactory.getStrategy(modelInstanceId);
        String taskId = strategy.createShotVideoTask(videoModelRequestVO);

        // TODO 如何异步保存？
        // 创建任务
        VideoTask videoTask = new VideoTask();
        videoTask.setProjectId(videoModelRequestVO.getProjectId());
        videoTask.setTaskId(taskId);
        videoTask.setInstanceId(videoModelRequestVO.getModelInstanceId());
        videoTask.setTaskType(TaskType.SHOT_VIDEO_GENERATION);
        videoTask.setTargetId(shotId);
        videoTask.setNextPollAt(Date.from(Instant.now()));
        videoTask.setPollCount(0);
        videoTask.setStatus("QUEUED");


        // 5. 添加任务信息到数据库
        videoTaskService.save(videoTask);

        return videoTask;

    }

    @Override
    public Storyboard updateFirstFramePrompt(Long shotId, Long projectId, String text) {
        Storyboard storyboard = getStoryboardsById(shotId);
        storyboard.setImagePrompt(text);
        // 创建异步任务，异步更新数据库
        storyboardPersistFacade.updatePromptAsync(storyboard);
        return storyboard;
    }

    @Override
    public void deleteShot(Long shotId) {
        // 删除分镜
        removeById(shotId);

        // 删除分镜关联的内容
        storyboardPersistFacade.deleteShotAssociatedInfoAsync(shotId);

    }

    @Override
    public Storyboard updateStoryboard(Storyboard storyboard) {
        // 只能修改关联角色、场景和分镜描述

        // 关联场景不能为空
        if (ObjectUtils.isEmpty(storyboard.getScenes())) {
            throw new RuntimeException("关联场景不能为空");
        }

        // 创建异步任务，异步更新数据库
        storyboardPersistFacade.updateShotAssociatedInfoAsync(storyboard);

        return storyboard;
    }

    @Override
    public void deleteShotByEpisodeId(Long episodeId) {
        // 获取章节下所有的分镜Id
        List<Long> shotIds = storyboardMapper.getShotIdByEpisodeId(episodeId);
        shotIds.forEach(this::deleteShot);
    }
}
