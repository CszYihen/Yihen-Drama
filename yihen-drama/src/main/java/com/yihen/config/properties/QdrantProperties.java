package com.yihen.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qdrant")
public class QdrantProperties {

    private String host;

    private int port;

    private String collectionName;

    private int dimension;

    private int maxSegmentSizeInTokens;

    private int maxOverlapSizeInTokens;

    private int maxResult;

    private double minScore;

}