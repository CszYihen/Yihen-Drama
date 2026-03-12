package com.yihen.core.model.strategy;


import com.yihen.YihenDramaApplication;
import com.yihen.controller.vo.EmbeddingModelRequestVO;
import com.yihen.controller.vo.ImageModelRequestVO;
import com.yihen.core.model.strategy.embedding.impl.VolcanoEmbeddingModelStrategy;
import com.yihen.core.model.strategy.image.impl.VolcanoImageModelStrategy;
import com.yihen.entity.Storyboard;
import com.yihen.service.StoryboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;

@SpringBootTest(classes = YihenDramaApplication.class,
        properties = {
                "app.websocket.enabled=false",
                "spring.main.lazy-initialization=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        })
public class VolcanoEmbeddingModelStrategyTest {

    @Autowired
    private VolcanoEmbeddingModelStrategy volcanoEmbeddingModelStrategy;

    @Autowired
    private StoryboardService storyboardService;

    @Test
    void create() throws Exception {


        EmbeddingModelRequestVO embeddingModelRequestVO = new EmbeddingModelRequestVO();
        embeddingModelRequestVO.setModelInstanceId(25L);
        embeddingModelRequestVO.setInput(List.of("你好","今天天气真好"));
        List<List<Float>> lists = volcanoEmbeddingModelStrategy.create(embeddingModelRequestVO);
        System.out.println(lists);
    }

    @Test
    void test() {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put("11", 11);
    }
}
