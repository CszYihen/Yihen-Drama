package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.asyn.EpisodePersistFacade;
import com.yihen.controller.vo.EpisodeCreateRequestVO;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.entity.Characters;
import com.yihen.entity.Episode;
import com.yihen.entity.Scene;
import com.yihen.enums.EpisodeStep;
import com.yihen.mapper.EpisodeMapper;
import com.yihen.service.*;
import com.yihen.util.CheckUtils;
import jakarta.servlet.annotation.WebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service("episodeServiceImpl")
public class EpisodeServiceImpl extends ServiceImpl<EpisodeMapper, Episode> implements EpisodeService {





    @Autowired
    private EpisodeMapper episodeMapper;


    @Autowired
    private CharacterService characterService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private EpisodePersistFacade episodePersistFacade;

    @Override
    public List<Episode> getEpisodesByProjectId(Long projectId) {
        return episodeMapper.getEpisodesByProjectId(projectId);
    }



    @Override
    public Episode createEpisode(EpisodeCreateRequestVO episodeCreateRequestVO) {
        // 合法性校验
        CheckUtils.validateRequeried(episodeCreateRequestVO::getName,"章节名称不可为空");
        CheckUtils.validateRequeried(episodeCreateRequestVO::getProjectId,"项目ID不可为空");
        CheckUtils.validateRequeried(episodeCreateRequestVO::getChapterNumber,"章节序号不可为空");


        // 实体构建
        Episode episode = new Episode();
        episode.setProjectId(episodeCreateRequestVO.getProjectId());
        episode.setChapterNumber(episodeCreateRequestVO.getChapterNumber());
        episode.setName(episodeCreateRequestVO.getName());
        episode.setContent(episodeCreateRequestVO.getContent());
        episode.setCurrentStep(EpisodeStep.INPUT_CONTENT);

        // 保存
        save(episode);

        // 修改项目章节数量
        episodePersistFacade.addEpisodeCountAsync(episode.getProjectId());

        return episode;
    }

    @Override
    public List<Long> getEpisodeIdsByProjectId(Long projectId) {
        return episodeMapper.getEpisodeIdByProjectId(projectId);
    }


    @Override
    public ExtractionResultVO getPropertyById(Long id) {
        LambdaQueryWrapper<Characters> charactersLambdaQueryWrapper = new LambdaQueryWrapper<Characters>().eq(Characters::getEpisodeId, id);
        List<Characters> characters = characterService.list(charactersLambdaQueryWrapper);

        LambdaQueryWrapper<Scene> sceneLambdaQueryWrapper = new LambdaQueryWrapper<Scene>().eq(Scene::getEpisodeId, id);
        List<Scene> scenes = sceneService.list(sceneLambdaQueryWrapper);

        ExtractionResultVO extractionResultVO = new ExtractionResultVO();
        extractionResultVO.setCharacters(characters);
        extractionResultVO.setScenes(scenes);
        return extractionResultVO;
    }

    @Override
    public void deleteEpisode(Long id) {

        removeById(id);
        // 删除章节关联内容
        episodePersistFacade.deleteRelatedContentAsync(id);


    }

    @Override
    public Long getProjectIdByEpisodeId(Long episodeId) {
        return episodeMapper.getProjectIdByEpisodeId(episodeId);
    }

    @Override
    public String getEpisodeContentById(Long episodeId) {
        String content = episodeMapper.getContentByEpisodeId(episodeId);
        return content;
    }

    @Override
    public Episode getEpisodeById(Long id) {
        return getById(id);
    }

    @Override
    public void updateEpisode(Episode episode) {
        updateById(episode);
    }

}
