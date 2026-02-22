package com.yihen.util;

import com.yihen.YihenDramaApplication;
import com.yihen.entity.Characters;
import com.yihen.http.HttpExecutor;
import io.minio.GetObjectResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

@SpringBootTest(classes = YihenDramaApplication.class,
        properties = {
                "app.websocket.enabled=false",
                "spring.main.lazy-initialization=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        })
public class RedisUtilTest {

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void test() {
        Characters character = new Characters();

        character.setName("李豪");
        character.setId(1L);

        redisUtils.set("character", character);

        Characters character1 =(Characters) redisUtils.get("character");
        System.out.println(character1.getId());
    }
}
