package com.yihen.core.model.strategy.audio.impl;

import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.yihen.controller.vo.AudioRequestVO;
import com.yihen.core.model.strategy.audio.AudioModelStrategy;
import com.yihen.entity.ModelDefinition;
import com.yihen.entity.ModelInstance;
import com.yihen.service.ModelManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * 音频模型策略接口
 * 统一各厂商音频生成成服务的调用方式
 */
@Component
@Slf4j
public class DashScopeAudioModelStrategy  implements AudioModelStrategy {

    private static final String STRATEGY_TYPE = "dashscope";
    private static final String MODEL_BASE_URL = "https://dashscope.aliyuncs.com";

    @Autowired
    private SpeechSynthesizer speechSynthesizer;

    @Autowired
    private ModelManageService modelManageService;

    @Override
    public ByteBuffer create(AudioRequestVO audioRequestVO) throws Exception {
        // 1. 获取模型实例
        // 1. 获取模型实例
        ModelInstance modelInstance = modelManageService.getModelInstanceById(audioRequestVO.getModelInstanceId());

        // 构建请求参数

        SpeechSynthesisParam speechSynthesisParam = SpeechSynthesisParam.builder()
                .apiKey(modelInstance.getApiKey())
                .model(modelInstance.getModelCode())
                .voice(audioRequestVO.getVoice())
                .build();

        speechSynthesizer.updateParamAndCallback(speechSynthesisParam,null);
        // 发送请求
        ByteBuffer audio = null;
        try {
            audio = speechSynthesizer.call(audioRequestVO.getText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            speechSynthesizer.getDuplexApi().close(1000, "bye");
        }


        return audio;

    }

    @Override
    public String getStrategyType() {
        return STRATEGY_TYPE;
    }

    @Override
    public boolean supports(ModelInstance modelInstance) {
        // 可以根据 modelDefId 或其他属性判断是否支持

        ModelDefinition modelDefinition = modelManageService.getById(modelInstance.getModelDefId());
        // 判断该模型实例对应的厂商BaseURL是否属于火山引擎
        if (MODEL_BASE_URL.equals(modelDefinition.getBaseUrl())) {
            return true;
        }
        return false;
    }
}
