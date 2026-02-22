package com.yihen.config;


import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashScopeConfig {


    // 语音模型 （用于阿里百炼平台 ）
    @Bean
    public SpeechSynthesizer speechSynthesizer() {
        return new SpeechSynthesizer();
    }
}
