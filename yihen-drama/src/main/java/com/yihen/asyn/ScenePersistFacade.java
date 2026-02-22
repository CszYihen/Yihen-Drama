package com.yihen.asyn;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihen.entity.StoryBoardCharacter;
import com.yihen.entity.StoryBoardScene;
import com.yihen.entity.VideoTask;
import com.yihen.enums.TaskType;
import com.yihen.mapper.StoryBoardSceneMapper;
import com.yihen.mapper.VideoTaskMapper;
import com.yihen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScenePersistFacade {







    @Autowired
    private StoryBoardSceneMapper storyBoardSceneMapper;


    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 删除章节对应资产
     */
    @Async("sceneExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void deleteSceneRelatedContentAsync(Long sceneId) {
        // 删除相关数据
        // 1. 删除场景与分镜关联
        LambdaQueryWrapper<StoryBoardScene> storyBoardSceneLambdaQueryWrapper = new LambdaQueryWrapper<StoryBoardScene>().eq(StoryBoardScene::getSceneId, sceneId);
        storyBoardSceneMapper.delete(storyBoardSceneLambdaQueryWrapper);

    }




}
