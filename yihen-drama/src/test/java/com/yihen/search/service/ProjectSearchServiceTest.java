package com.yihen.search.service;

import com.yihen.YihenDramaApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = YihenDramaApplication.class,
        properties = {
                "app.websocket.enabled=false",
                "spring.main.lazy-initialization=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        })
public class ProjectSearchServiceTest {

    @Autowired
    private ProjectSearchService projectSearchService;

    @Test
    void  searchProject() {
//        PageResult<ProjectDoc> docPageResult = projectSearchService.search("十日", null, null, 1, 5);
//        System.out.printf(docPageResult.toString());
    }

    @Test
    void  suggestProject() {
        List<String> suggest = projectSearchService.suggest("十", 10);
        System.out.println(suggest);
    }
}
