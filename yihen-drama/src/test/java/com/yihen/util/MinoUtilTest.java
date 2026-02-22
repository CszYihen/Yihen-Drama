package com.yihen.util;

import com.yihen.YihenDramaApplication;
import com.yihen.http.HttpExecutor;
import io.minio.GetObjectResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootTest(classes = YihenDramaApplication.class,
        properties = {
                "app.websocket.enabled=false",
                "spring.main.lazy-initialization=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        })
public class MinoUtilTest {

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private HttpExecutor httpExecutor;

    @Test
    public void testUpload() throws Exception {
        String imgUrl = "";
        byte[] img = httpExecutor.downloadImage(imgUrl).block();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(img);
        try {
            minioUtil.uploadFile(byteArrayInputStream,img.length,"yihen-drama", "project/1/50-宋伊裳.png");
        } catch (Exception e) {
            minioUtil.createBucket("yihen-drama");
            try {
                minioUtil.uploadFile(byteArrayInputStream,img.length,"yihen-drama", "project/1/50-宋伊裳.png");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Test
    public void testGetObjectUrl() throws Exception {
        String objectName = "project/1/50-宋伊裳.png";
        String bucketName = "yihen-drama";
        String objectUrl = minioUtil.getObjectUrl(bucketName, objectName);
        System.out.println(objectUrl);
    }


    @Test
    public void testGetObjectUnderBase64() throws Exception {
//        http://127.0.0.1:9000/yihen-drama/project/3/characters/50-宋伊棠.jpeg http://127.0.0.1:9000/yihen-drama/project/3/characters/49-林砚.jpeg
        GetObjectResponse object = minioUtil.getObject("yihen-drama", "/project/3/shots/episode-21/2-shot2.jpeg");
        String imageFormat = "jpeg";  // 假设你已经知道图片格式为 "jpeg"

        byte[] bytes = object.readAllBytes();

        String base64Image = Base64.getEncoder().encodeToString(bytes);

        // 构建符合要求的 Base64 数据格式
        String base64DataUri = "data:image/" + imageFormat + ";base64," + base64Image;

        try (FileWriter fileWriter = new FileWriter("base64_.txt")) {
            fileWriter.write(base64DataUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println( base64DataUri);
    }
}
