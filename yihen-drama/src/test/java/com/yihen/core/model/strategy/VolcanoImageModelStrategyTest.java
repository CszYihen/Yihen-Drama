package com.yihen.core.model.strategy;


import com.yihen.YihenDramaApplication;
import com.yihen.controller.vo.AudioRequestVO;
import com.yihen.controller.vo.ImageModelRequestVO;
import com.yihen.core.model.strategy.audio.impl.DashScopeAudioModelStrategy;
import com.yihen.core.model.strategy.image.ImageModelStrategy;
import com.yihen.core.model.strategy.image.impl.VolcanoImageModelStrategy;
import com.yihen.core.model.strategy.video.impl.VolcanoVideoModelStrategy;
import com.yihen.entity.Storyboard;
import com.yihen.service.StoryboardService;
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
public class VolcanoImageModelStrategyTest {

    @Autowired
    private VolcanoImageModelStrategy volcanoImageModelStrategy;

    @Autowired
    private StoryboardService storyboardService;

    @Test
    void create() throws Exception {
        Storyboard storyboard = storyboardService.getStoryboardsById(2L);

        ImageModelRequestVO imageModelRequestVO = new ImageModelRequestVO();
        imageModelRequestVO.setModelInstanceId(7L);
        imageModelRequestVO.setObject(storyboard);
        String byTextAndImage = volcanoImageModelStrategy.createByTextAndImage(imageModelRequestVO);
        System.out.println(byTextAndImage);
    }
}
