package com.yihen.controller;

import com.yihen.common.Result;
import com.yihen.controller.vo.CharactersRequestVO;
import com.yihen.controller.vo.FirstFrameRequestVO;
import com.yihen.controller.vo.StoryboardRequestVO;
import com.yihen.controller.vo.UpdateFirstFramePromptRequestVO;
import com.yihen.entity.Storyboard;
import com.yihen.entity.VideoTask;
import com.yihen.service.StoryboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分镜接口", description = "分镜管理")
@RestController
@RequestMapping("/api/storyboard")
@Slf4j
public class StoryboardController {

    @Resource
    private StoryboardService storyboardService;

    @GetMapping("/{episodeId}")
    @Operation(summary = "获取分镜列表")
    public Result<List<Storyboard>> get(@PathVariable Long episodeId) {
        List<Storyboard> storyboards = storyboardService.getStoryboardsByEpisodeId(episodeId);
        return Result.success(storyboards);
    }

    @PutMapping
    @Operation(summary = "保存分镜")
    public Result<List<Storyboard>> save(@PathVariable Long episodeId, @RequestBody List<Storyboard> storyboards) {
        storyboards.forEach(sb -> sb.setEpisodeId(episodeId));
        storyboardService.saveOrUpdateBatch(storyboards);
        return Result.success(storyboards);
    }

    @PostMapping("/generate")
    @Operation(summary = "生成分镜")
    public Result<List<Storyboard>> generate(@RequestBody StoryboardRequestVO storyboardRequestVO) throws Exception {
        List<Storyboard> storyboards = storyboardService.generate(storyboardRequestVO.getEpisodeId(),storyboardRequestVO.getProjectId(),storyboardRequestVO.getModelId());
        return Result.success(storyboards);
    }

    @PostMapping("/get/{shotId}")
    @Operation(summary = "获取单个分镜信息")
    public Result<Storyboard> generate(@PathVariable("shotId") Long shotId) throws Exception {
        Storyboard storyboards = storyboardService.getStoryboardsById(shotId);
        return Result.success(storyboards);
    }

    @PutMapping("/update")
    @Operation(summary = "修改单个分镜信息")
    public Result<Storyboard> generate(@RequestBody Storyboard storyboard) throws Exception {
        Storyboard storyboards = storyboardService.updateStoryboard(storyboard);
        return Result.success(storyboards);
    }

    @DeleteMapping("/delete/{shotId}")
    @Operation(summary = "删除分镜")
    public Result<Void> deleteShot(@PathVariable("shotId") Long shotId) throws Exception {
        storyboardService.deleteShot(shotId);
        return Result.success("删除成功");
    }

    @PostMapping("/first-frame-prompt")
    @Operation(summary = "生成首帧提示词")
    public Result<Storyboard> generateFirstFramePrompt(@RequestBody FirstFrameRequestVO frameRequestVO) throws Exception {
        Storyboard storyboard = storyboardService.generateFirstFramePrompt(frameRequestVO.getShotId(),frameRequestVO.getProjectId(), frameRequestVO.getModelInstanceId());
        return Result.success(storyboard);
    }

    @PutMapping("/update/first-frame-prompt")
    @Operation(summary = "修改首帧提示词")
    public Result<Storyboard> updateFirstFramePrompt(@RequestBody UpdateFirstFramePromptRequestVO updateFirstFramePromptRequestVO) throws Exception {
        Storyboard storyboard = storyboardService.updateFirstFramePrompt(updateFirstFramePromptRequestVO.getShotId(),updateFirstFramePromptRequestVO.getProjectId(), updateFirstFramePromptRequestVO.getText());
        return Result.success(storyboard);
    }

    @PostMapping("/first-frame")
    @Operation(summary = "生成首帧图")
    public Result<Storyboard> generateFirstFrame(@RequestBody FirstFrameRequestVO frameRequestVO) throws Exception {
        Storyboard storyboard = storyboardService.generateFirstFrame(frameRequestVO.getShotId(),frameRequestVO.getProjectId(), frameRequestVO.getModelInstanceId());
        return Result.success(storyboard);
    }

    @PostMapping("/shot-video-prompt")
    @Operation(summary = "生成分镜视频提示词")
    public Result<Storyboard> generateShotVideoPrompt(@RequestBody FirstFrameRequestVO frameRequestVO) throws Exception {
        Storyboard storyboard = storyboardService.generateShotVideoPrompt(frameRequestVO.getShotId(),frameRequestVO.getProjectId(), frameRequestVO.getModelInstanceId());
        return Result.success(storyboard);
    }

    @PostMapping("/generate-shot-video")
    @Operation(summary = "生成分镜视频")
    public Result<VideoTask> generateCharacterVideo(@RequestBody FirstFrameRequestVO frameRequestVO) throws Exception {
        VideoTask videoTask = storyboardService.createShotVideoTask(frameRequestVO.getShotId(),frameRequestVO.getProjectId(), frameRequestVO.getModelInstanceId(),frameRequestVO.getParams());
        return Result.success(videoTask);
    }


}
