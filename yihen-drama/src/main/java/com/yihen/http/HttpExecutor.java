package com.yihen.http;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class HttpExecutor {

    private final WebClientFactory webClientFactory;
    public HttpExecutor(WebClientFactory webClientFactory) {
        this.webClientFactory = webClientFactory;
    }

    public Mono<String> post(
            String baseUrl,
            String path,
            String apiKey,
            Object body
    ){
        return webClientFactory.getWebClient(baseUrl)
                .post()
                .uri(path)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> get(
            String baseUrl,
            String path,
            String apiKey
    ){
        return webClientFactory.getWebClient(baseUrl)
                .get()
                .uri(path)
                .header("Authorization", "Bearer " + apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }


    public Mono<byte[]> downloadImage(String imageUrl) {
        String decodedUrl = URLDecoder.decode(imageUrl, StandardCharsets.UTF_8);
        return webClientFactory.getImgWebClient().get()
                .uri(decodedUrl)
                .retrieve()
                .bodyToMono(byte[].class);
    }

    public Mono<ResponseEntity<Resource>> downloadVideoResource(String videoUrl) {
        String decodedUrl = URLDecoder.decode(videoUrl, StandardCharsets.UTF_8);
        return webClientFactory.getVideoWebClient().get()
                .uri(decodedUrl)
                .retrieve()
                .toEntity(Resource.class);
    }




}
