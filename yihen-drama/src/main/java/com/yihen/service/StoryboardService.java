package com.yihen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.controller.vo.FirstFrameRequestVO;
import com.yihen.entity.Storyboard;
import com.yihen.entity.VideoTask;

import java.util.List;
import java.util.Map;

public interface StoryboardService extends IService<Storyboard> {

    List<Storyboard> getStoryboardsByEpisodeId(Long episodeId);

    Storyboard getStoryboardsById(Long id);


    List<Storyboard> generate(Long episodeId,Long projectId ,Long modelId) throws Exception;

    /**
     * 生成首帧提示词
     * @param id 分镜Id
     * @return
     */
    Storyboard generateFirstFramePrompt(Long id,Long projectId,Long modelId) throws Exception;

    Storyboard generateFirstFrame(Long shotId, Long projectId, Long modelInstanceId) throws Exception;

    Storyboard generateShotVideoPrompt(Long shotId, Long projectId, Long modelInstanceId) throws Exception;

    VideoTask createShotVideoTask(Long shotId, Long projectId, Long modelInstanceId, Map<String,Object> params) throws Exception;

    Storyboard updateFirstFramePrompt(Long shotId, Long projectId, String text);

    void deleteShot(Long shotId);

    Storyboard updateStoryboard(Storyboard storyboard);

    void deleteShotByEpisodeId(Long episodeId);
}
