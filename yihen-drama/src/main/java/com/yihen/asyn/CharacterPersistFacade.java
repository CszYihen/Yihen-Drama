package com.yihen.asyn;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihen.entity.StoryBoardCharacter;
import com.yihen.entity.VideoTask;
import com.yihen.enums.TaskType;
import com.yihen.mapper.StoryBoardCharacterMapper;
import com.yihen.mapper.StoryboardMapper;
import com.yihen.mapper.VideoTaskMapper;
import com.yihen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CharacterPersistFacade {




    @Autowired
    private StoryBoardCharacterService storyBoardCharacterService;

    @Autowired
    private StoryBoardCharacterMapper storyBoardCharacterMapper;

    @Autowired
    private VideoTaskMapper  videoTaskMapper;


    /**
     * 异步执行，但事务仍然有效（关键：方法在 Spring Bean 上）
     * 删除章节对应资产
     */
    @Async("characterExecutor") // 你已有线程池的话配置成 Spring Async Executor
    @Transactional(rollbackFor = Exception.class)
    public void deleteCharacterRelatedContentAsync(Long characterId) {
        // 删除相关数据
        // 1. 删除角色与分镜关联
        LambdaQueryWrapper<StoryBoardCharacter> storyBoardCharacterLambdaQueryWrapper = new LambdaQueryWrapper<StoryBoardCharacter>().eq(StoryBoardCharacter::getCharacterId, characterId);
        storyBoardCharacterMapper.delete(storyBoardCharacterLambdaQueryWrapper);

        // 2. 删除对应的视频生成任务
        LambdaQueryWrapper<VideoTask> lambdaQueryWrapper = new LambdaQueryWrapper<VideoTask>()
                .eq(VideoTask::getTaskType, TaskType.CHARACTER_VIDEO_GENERATION)
                .eq(VideoTask::getTargetId, characterId);
        videoTaskMapper.delete(lambdaQueryWrapper);
    }




}
