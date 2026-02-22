package com.yihen.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component // 将该类加入到配置容器，这样才可以读取配置参数
@ConfigurationProperties(prefix = "minio") // 从配置文件中批量读取属性值
public class MinioProperties {

    private String endPoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
