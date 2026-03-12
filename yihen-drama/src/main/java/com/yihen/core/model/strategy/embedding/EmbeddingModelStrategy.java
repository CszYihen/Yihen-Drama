package com.yihen.core.model.strategy.embedding;


import com.yihen.controller.vo.EmbeddingModelRequestVO;
import com.yihen.controller.vo.ImageModelRequestVO;
import com.yihen.entity.ModelInstance;

import java.util.List;

/**
 * 向量模型策略接口
 * 统一各厂商图像生成服务的调用方式
 */
public interface EmbeddingModelStrategy {
    /**
     * 文本生成向量
     * @param embeddingModelRequestVO 图像信息
     */
    List<List<Float>> create(EmbeddingModelRequestVO embeddingModelRequestVO) throws Exception;



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
