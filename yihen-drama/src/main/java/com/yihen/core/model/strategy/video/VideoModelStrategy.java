package com.yihen.core.model.strategy.video;

import com.yihen.controller.vo.CharactersRequestVO;
import com.yihen.controller.vo.FirstFrameRequestVO;
import com.yihen.controller.vo.ImageModelRequestVO;
import com.yihen.controller.vo.VideoModelRequestVO;
import com.yihen.core.model.strategy.video.dto.VideoTaskDTO;
import com.yihen.entity.ModelInstance;

/**
 * 视频模型策略接口
 * 统一各厂商视频生成服务的调用方式
 */
public interface VideoModelStrategy {

    /**
     * 创建视频生成任务
     * @param charactersRequestVO 角色信息
     */
    String create(CharactersRequestVO charactersRequestVO) throws Exception;

    /**
     * 创建分镜视频生成任务
     * @param videoModelRequestVO 视频信息
     */
    String createShotVideoTask(VideoModelRequestVO videoModelRequestVO) throws Exception;

    /**
     * 获取策略类型（厂商标识）
     * @return 策略类型标识
     */
    String getStrategyType();

    /**
     * 判断是否支持该模型
     * @param  modelInstance 模型实例
     * @return 是否支持
     */
    boolean supports(ModelInstance modelInstance);

    // 查询生成任务
    VideoTaskDTO queryTask(String taskId, Long modelInstanceId) throws Exception;
}
