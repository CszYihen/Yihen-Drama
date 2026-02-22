package com.yihen.controller;

import com.yihen.common.Result;
import com.yihen.controller.vo.*;
import com.yihen.entity.*;
import com.yihen.service.EpisodeService;
import com.yihen.core.model.InfoExtractTextModelService;
import com.yihen.core.model.impl.EpisodeExtractOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "章节接口", description = "章节管理")
@RestController
@RequestMapping("/api/episodes")
@Slf4j
public class EpisodeController {

    @Resource
    private EpisodeService episodeService;

    @Autowired
    private EpisodeExtractOrchestrator episodeExtractOrchestrator;

    @Resource
    private InfoExtractTextModelService infoExtractTextModelService;



    @GetMapping("/project/{projectId}")
    @Operation(summary = "获取项目的章节列表")
    public Result<List<Episode>> listByProject(@PathVariable Long projectId) {
        List<Episode> episodes = episodeService.getEpisodesByProjectId(projectId);
        return Result.success(episodes);
    }

    @GetMapping("/property/{id}")
    @Operation(summary = "获取章节中出现人物、场景信息")
    public Result<ExtractionResultVO> getProperty(@PathVariable Long id) {
        ExtractionResultVO property = episodeService.getPropertyById(id);

        return Result.success(property);
    }


    @GetMapping("/{id}")
    @Operation(summary = "获取章节详情")
    public Result<Episode> get(@PathVariable Long id) {
        Episode episode = episodeService.getEpisodeById(id);
        return Result.success(episode);
    }

    @PostMapping("/create")
    @Operation(summary = "创建章节")
    public Result<Episode> create(@RequestBody EpisodeCreateRequestVO episodeCreateRequestVO) {
        Episode episode = episodeService.createEpisode(episodeCreateRequestVO);
        return Result.success(episode);
    }


    @PostMapping("/extract")
    @Operation(summary = "提取章节关键信息")
    public Result<ExtractionResultVO> extractInfo(
            @RequestBody ExtractRequestVO request) throws Exception {

        ExtractionResultVO extractionResultVO = episodeExtractOrchestrator.extractAndSaveAssets(request);

        return Result.success(extractionResultVO);
    }

    @PostMapping("/generate-character-img")
    @Operation(summary = "生成角色图片")
    public Result<Characters> generateCharacterImage(@RequestBody CharactersRequestVO charactersRequestVO) throws Exception {
        Characters characters = episodeExtractOrchestrator.generateCharacterAndSaveAssets(charactersRequestVO);
        return Result.success(characters);
    }

    @PostMapping("/generate-character-video")
    @Operation(summary = "生成角色视频")
    public Result<VideoTask> generateCharacterVideo(@RequestBody CharactersRequestVO charactersRequestVO) throws Exception {
        VideoTask videoTask = episodeExtractOrchestrator.createCharacterVideoTaskAndSaveAssets(charactersRequestVO);
        return Result.success(videoTask);
    }

    @PostMapping("/generate-scene-img")
    @Operation(summary = "生成场景图片")
    public Result<Scene> generateSceneImage(@RequestBody SceneRequestVO sceneRequestVO) throws Exception {
        Scene scene = episodeExtractOrchestrator.generateSceneAndSaveAssets(sceneRequestVO);
        return Result.success(scene);
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "删除章节")
    public Result<Void> delete(@PathVariable Long id) {
        episodeService.deleteEpisode(id);
        return Result.success(null);
    }

    @PutMapping("/update")
    @Operation(summary = "更新章节内容")
    public Result<Void> update(@RequestBody EpisodeUpdateRequestVO episodeUpdateRequestVO) {
        Episode episode = new Episode();
        BeanUtils.copyProperties(episodeUpdateRequestVO, episode);
        episodeService.updateEpisode(episode);
        return Result.success(null);
    }


}
