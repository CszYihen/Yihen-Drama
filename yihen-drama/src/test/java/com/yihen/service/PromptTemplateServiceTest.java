package com.yihen.service;

import com.yihen.YihenDramaApplication;
import com.yihen.entity.PromptTemplate;
import com.yihen.enums.SceneCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = YihenDramaApplication.class,
        properties = {
                "spring.main.lazy-initialization=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        }
)
public class PromptTemplateServiceTest {

    @Autowired
    private PromptTemplateService promptTemplateService;

    @Test
    public void testGetBySceneCode() {
        PromptTemplate promptTemplate = promptTemplateService.getDefaultTemplateBySceneCode(SceneCode.INFO_EXTRACT);
        System.out.println(promptTemplate);
    }
}
