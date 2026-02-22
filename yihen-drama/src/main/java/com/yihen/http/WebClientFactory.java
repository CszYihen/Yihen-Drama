package com.yihen.http;


import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebClientFactory {

    private final Map<String, WebClient> cache = new ConcurrentHashMap<>();

    public WebClient getWebClient(String baseUrl) {
        return cache.computeIfAbsent(baseUrl,
                url -> WebClient.builder()
                        .baseUrl(url)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build()
                );
    }

    // 获取图片http请求发送器
    public WebClient getImgWebClient() {
        return cache.computeIfAbsent("IMG",
                url -> {

                    return WebClient.builder()
                            .defaultHeader("User-Agent", "Mozilla/5.0")
                            .defaultHeader("Accept", "image/*")
                            .build();
                }
        );
    }

    // 获取视频 http 请求发送器
    public WebClient getVideoWebClient() {
        return cache.computeIfAbsent("VIDEO",
                key -> WebClient.builder()
                        .defaultHeader("User-Agent", "Mozilla/5.0")
                        .defaultHeader("Accept", "video/*")
                        .build()
        );
    }


}
