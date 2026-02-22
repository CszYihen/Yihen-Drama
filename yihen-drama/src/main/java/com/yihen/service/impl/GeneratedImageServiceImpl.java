package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.entity.GeneratedImage;
import com.yihen.mapper.GeneratedImageMapper;
import com.yihen.service.GeneratedImageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneratedImageServiceImpl extends ServiceImpl<GeneratedImageMapper, GeneratedImage> implements GeneratedImageService {

    @Override
    public List<GeneratedImage> getImagesByEpisodeId(Long episodeId) {
        return list(new LambdaQueryWrapper<GeneratedImage>()
                .eq(GeneratedImage::getEpisodeId, episodeId));
    }

    @Override
    public List<GeneratedImage> getImagesByTargetId(Long episodeId, String imageType, Long targetId) {
        return list(new LambdaQueryWrapper<GeneratedImage>()
                .eq(GeneratedImage::getEpisodeId, episodeId)
                .eq(GeneratedImage::getImageType, imageType)
                .eq(GeneratedImage::getTargetId, targetId));
    }

    @Override
    public void deleteImagesByEpisodeId(Long episodeId) {
        lambdaUpdate().eq(GeneratedImage::getEpisodeId, episodeId)
                .remove();
    }

    @Override
    public void deleteImagesByTargetId(Long episodeId, String imageType, Long targetId) {
        lambdaUpdate()
                .eq(GeneratedImage::getEpisodeId, episodeId)
                .eq(GeneratedImage::getImageType, imageType)
                .eq(GeneratedImage::getTargetId, targetId)
                .remove();
    }
}
