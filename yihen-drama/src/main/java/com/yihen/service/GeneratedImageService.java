package com.yihen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.GeneratedImage;

import java.util.List;

public interface GeneratedImageService extends IService<GeneratedImage> {

    List<GeneratedImage> getImagesByEpisodeId(Long episodeId);

    List<GeneratedImage> getImagesByTargetId(Long episodeId, String imageType, Long targetId);

    void deleteImagesByEpisodeId(Long episodeId);

    void deleteImagesByTargetId(Long episodeId, String imageType, Long targetId);
}
