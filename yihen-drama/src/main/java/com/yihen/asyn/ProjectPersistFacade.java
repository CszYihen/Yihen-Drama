package com.yihen.asyn;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihen.config.properties.MinioProperties;
import com.yihen.entity.Characters;
import com.yihen.entity.Scene;
import com.yihen.http.HttpExecutor;
import com.yihen.mapper.EpisodeMapper;
import com.yihen.service.CharacterService;
import com.yihen.service.EpisodeService;
import com.yihen.service.ProjectService;
import com.yihen.service.SceneService;
import com.yihen.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectPersistFacade {





    @Autowired
    private EpisodeService episodeService;


    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 删除章节对应资产
     */
    @Async("projectExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectRelatedContentAsync(Long projectId) {
        // 获取名下所有的章节Id
        List<Long> episodeIds = episodeService.getEpisodeIdsByProjectId(projectId);
        // 删除所有关联资产
        // episode -> characters -> storyboard_characters
        // episode -> scene -> storyboard_scene
        // episode -> storyboard -> storyboard_scene,storyboard_characters
        episodeIds.forEach(episodeId -> episodeService.deleteEpisode(episodeId));

    }




}
