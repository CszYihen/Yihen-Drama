package com.yihen.core.model.strategy;


import com.yihen.YihenDramaApplication;
import com.yihen.controller.vo.AudioRequestVO;
import com.yihen.core.model.strategy.audio.impl.DashScopeAudioModelStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = YihenDramaApplication.class,
        properties = {
                "app.websocket.enabled=false",
                "spring.main.lazy-initialization=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        })
public class DashScopeAudioModelStrategyTest {

    @Autowired
    private DashScopeAudioModelStrategy dashScopeAudioModelStrategy;

    @Test
    void create() throws Exception {
        AudioRequestVO audioRequestVO = new AudioRequestVO();
        audioRequestVO.setModelInstanceId(2L);
        audioRequestVO.setText("跑不掉了吧？刚才那股气息，明明就在这儿。(你说话的情感是愤怒)");
        audioRequestVO.setVoice("longxian_v3");

        dashScopeAudioModelStrategy.create(audioRequestVO);
    }
}
