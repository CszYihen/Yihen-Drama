package com.yihen.core.model.strategy.image;


import com.yihen.controller.vo.CharactersRequestVO;
import com.yihen.controller.vo.ImageModelRequestVO;
import com.yihen.entity.ModelInstance;

/**
 * 图形模型策略接口
 * 统一各厂商图像生成服务的调用方式
 */
public interface ImageModelStrategy {
    /**
     * 文生图 生成任务
     * @param imageModelRequestVO 图像信息
     */
    String create(ImageModelRequestVO imageModelRequestVO) throws Exception;

    /**
     * 文+图 生图 生成任务
     * @param imageModelRequestVO 图像信息
     */
    String createByTextAndImage(ImageModelRequestVO imageModelRequestVO) throws Exception;

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
