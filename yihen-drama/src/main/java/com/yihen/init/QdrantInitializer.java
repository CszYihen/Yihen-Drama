package com.yihen.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihen.config.properties.QdrantProperties;
import com.yihen.constant.model.ModelManageRedisConstant;
import com.yihen.entity.ModelDefinition;
import com.yihen.entity.ModelInstance;
import com.yihen.enums.ModelType;
import com.yihen.mapper.ModelDefinitionMapper;
import com.yihen.mapper.ModelInstanceMapper;
import com.yihen.util.RedisUtils;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.apache.commons.math3.exception.util.LocalizedFormats.DIMENSION;

@Component
@Slf4j
public class QdrantInitializer implements ApplicationRunner {

    private final QdrantProperties qdrantProperties;

    public QdrantInitializer(QdrantProperties qdrantProperties) {
        this.qdrantProperties = qdrantProperties;
    }


    @Override
    public void run(ApplicationArguments args) throws ExecutionException, InterruptedException {

        // 1. 原生 Qdrant 客户端：负责创建 / 删除 collection
        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(qdrantProperties.getHost(), qdrantProperties.getPort(), false).build()
        );

        if (!client.collectionExistsAsync(qdrantProperties.getCollectionName()).get()) {
            // 3. 创建新的 collection，维度固定为 384
            client.createCollectionAsync(
                    qdrantProperties.getCollectionName(),
                    Collections.VectorParams.newBuilder()
                            .setDistance(Collections.Distance.Cosine)
                            .setSize(qdrantProperties.getDimension())
                            .build()
            ).get();
        }


    }



}
