package com.yihen.core.model.strategy.audio;

import com.yihen.controller.vo.AudioRequestVO;
import com.yihen.entity.ModelInstance;

import java.nio.ByteBuffer;

/**
 * 音频模型策略接口
 * 统一各厂商音频生成成服务的调用方式
 */
public interface AudioModelStrategy {

    /**
     * 创建音频生成任务
     * @param audioRequestVO 音频信息
     */
    ByteBuffer create(AudioRequestVO audioRequestVO) throws Exception;

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


}
