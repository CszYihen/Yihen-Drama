package com.yihen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.controller.vo.EpisodeCreateRequestVO;
import com.yihen.controller.vo.ExtractRequestVO;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.entity.Episode;

import java.util.List;

public interface EpisodeService extends IService<Episode> {

    List<Episode> getEpisodesByProjectId(Long projectId);


    Episode createEpisode(EpisodeCreateRequestVO episodeCreateRequestVO);

    List<Long> getEpisodeIdsByProjectId(Long projectId);


    /**
     * 获取章节已有资产
     * @param id
     * @return
     */
    ExtractionResultVO getPropertyById(Long id);


    /**
     * 删除章节
     * @param id
     */
    void deleteEpisode(Long id);

    /**
     * 通过章节Id获取项目Id
     * @param episodeId
     * @return
     */
    Long getProjectIdByEpisodeId(Long episodeId);

    /**
     * 根据章节Id获取章节内容
     * @param episodeId
     * @return
     */
    String getEpisodeContentById(Long episodeId);

    /**
     * 根据章节Id获取章节
     * @param id
     * @return
     */
    Episode getEpisodeById(Long id);

    void updateEpisode(Episode episode);
}
