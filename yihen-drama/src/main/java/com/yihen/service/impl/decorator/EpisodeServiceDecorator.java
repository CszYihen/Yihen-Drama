package com.yihen.service.impl.decorator;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.constant.episode.EpisodeRedisConstant;
import com.yihen.constant.project.ProjectRedisConstant;
import com.yihen.controller.vo.EpisodeCreateRequestVO;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.entity.Episode;
import com.yihen.mapper.EpisodeMapper;
import com.yihen.service.EpisodeService;
import com.yihen.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Primary // 让业务默认注入到“带缓存的实现�?
@Slf4j
public class EpisodeServiceDecorator extends ServiceImpl<EpisodeMapper, Episode> implements EpisodeService {

    private final EpisodeService episodeService;            // 被装饰�?
    private final RedisUtils redisUtils;


    public EpisodeServiceDecorator(
            @Qualifier("episodeServiceImpl") EpisodeService episodeService, RedisUtils redisUtils) {
        this.episodeService = episodeService;
        this.redisUtils = redisUtils;
    }


    @Override
    public List<Episode> getEpisodesByProjectId(Long projectId) {
        // 1. 查询缓存，是否存在对应数�?
        List<Episode> episodes =(List<Episode>) redisUtils.get(ProjectRedisConstant.PROJECT_EPISODES_KEY + projectId);
        if (ObjectUtils.isEmpty(episodes)) {
            // 2. 缓存为空，查询数据库
            episodes= episodeService.getEpisodesByProjectId(projectId);
            // 3. 添加缓存
            redisUtils.set(ProjectRedisConstant.PROJECT_EPISODES_KEY + projectId, episodes,1 , TimeUnit.DAYS);
        }
        return episodes;
    }

    @Override
    public Episode createEpisode(EpisodeCreateRequestVO episodeCreateRequestVO) {
        Episode episode = episodeService.createEpisode(episodeCreateRequestVO);
        // 删除对应缓存
        redisUtils.delete(ProjectRedisConstant.PROJECT_EPISODES_KEY + episode.getProjectId());
        return episode;
    }

    @Override
    public List<Long> getEpisodeIdsByProjectId(Long projectId) {
        return episodeService.getEpisodeIdsByProjectId(projectId);
    }

    @Override
    public ExtractionResultVO getPropertyById(Long id) {
        return episodeService.getPropertyById(id);
    }

    @Override
    public void deleteEpisode(Long id) {
        episodeService.deleteEpisode(id);
        // 删除对应缓存
        Long projectId = episodeService.getProjectIdByEpisodeId(id);
        redisUtils.delete(ProjectRedisConstant.PROJECT_EPISODES_KEY + projectId);
        redisUtils.delete(EpisodeRedisConstant.EPISODE_INFO_KEY + id);

    }

    @Override
    public Long getProjectIdByEpisodeId(Long episodeId) {
        return episodeService.getProjectIdByEpisodeId(episodeId);
    }

    @Override
    public String getEpisodeContentById(Long episodeId) {
        return episodeService.getEpisodeContentById(episodeId);
    }

    @Override
    public Episode getEpisodeById(Long id) {
        // 1. 查询缓存，是否存在对应数据
        Episode episode =(Episode) redisUtils.getHash(EpisodeRedisConstant.EPISODE_INFO_KEY + id,Episode.class);
        if (ObjectUtils.isEmpty(episode)) {
            // 2. 缓存为空，查询数据库
            episode= episodeService.getEpisodeById(id);
            // 3. 添加缓存
            redisUtils.putHash(EpisodeRedisConstant.EPISODE_INFO_KEY + id, episode,1 , TimeUnit.DAYS);
        }

        return episode;    }

    @Override
    public void updateEpisode(Episode episode) {
        episodeService.updateEpisode(episode);
        // 更新缓存
        redisUtils.updateHashPartial(EpisodeRedisConstant.EPISODE_INFO_KEY + episode.getId(),episode);
    }
}
