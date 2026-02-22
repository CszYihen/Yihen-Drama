package com.yihen.service;

import com.yihen.YihenDramaApplication;
import com.yihen.controller.vo.TextModelRequestVO;
import com.yihen.core.model.ShotGenerateTextModelService;
import com.yihen.enums.SceneCode;
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
public class StoryboardServiceTest {

    @Autowired
    private StoryboardService  storyboardService;

    @Test
    public void test() throws Exception {

        storyboardService.generateFirstFramePrompt(2L, 3L,3L);
    }
}
