package com.yihen.core.model.strategy.embedding.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihen.config.properties.MinioProperties;
import com.yihen.constant.MinioConstant;
import com.yihen.controller.vo.EmbeddingModelRequestVO;
import com.yihen.controller.vo.ImageModelRequestVO;
import com.yihen.core.model.strategy.embedding.EmbeddingModelStrategy;
import com.yihen.core.model.strategy.image.ImageModelStrategy;
import com.yihen.entity.*;
import com.yihen.http.HttpExecutor;
import com.yihen.mapper.ModelDefinitionMapper;
import com.yihen.service.ModelManageService;
import com.yihen.util.MinioUtil;
import com.yihen.util.UrlUtils;
import io.minio.GetObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class VolcanoEmbeddingModelStrategy implements EmbeddingModelStrategy {
    private static final String STRATEGY_TYPE = "volcano";
    private static final String MODEL_BASE_URL = "https://ark.cn-beijing.volces.com/api/v3";

    @Autowired
    private ModelManageService modelManageService;

    @Autowired
    private ModelDefinitionMapper modelDefinitionMapper;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private HttpExecutor httpExecutor;


    // 响应结果提取
    private  List<List<Float>> extractResponse(String response) throws Exception {

        // 1. 转成JSONObject对象
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.containsKey("error")) {
            // 调用失败
            String errorMessage = jsonObject.getJSONObject("error").getString("message");
            throw new Exception(errorMessage);
        }

        JSONArray dataArray = jsonObject.getJSONObject("data").getJSONArray("embedding");

        // 3. 存储 embedding
        List<List<Float>> embeddings = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++) {

            JSONObject item = dataArray.getJSONObject(i);

            JSONArray embeddingArray = item.getJSONArray("embedding");

            List<Float> embedding = new ArrayList<>();

            for (int j = 0; j < embeddingArray.size(); j++) {
                embedding.add(embeddingArray.getFloat(j));
            }

            embeddings.add(embedding);
        }




        return embeddings;

    }

    @Override
    public  List<List<Float>> create(EmbeddingModelRequestVO embeddingModelRequestVO) throws Exception {
        // 1. 获取模型实例
        ModelInstance modelInstance = modelManageService.getModelInstanceById(embeddingModelRequestVO.getModelInstanceId());

        // 2. 获取厂商定义的baseurl
        String baseUrl = modelDefinitionMapper.getBaseUrlById(modelInstance.getModelDefId());

        // 3. 拼接发送请求信息
        HashMap<String, Object> body = (HashMap<String, Object>) modelInstance.getParams();
        if (ObjectUtils.isEmpty(body)) {
            body = new HashMap<>();
        }
        body.put("model", modelInstance.getModelCode());

        // 将input组织为 火山引擎 合适格式
        List<HashMap<String, String>> list = embeddingModelRequestVO.getInput().stream()
                .map(input -> {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "text");
                    map.put("text", input);
                    return map;
                }).toList();
        body.put("input", list);

        // 3. 发送请求
        String response = httpExecutor.post(baseUrl, modelInstance.getPath(), modelInstance.getApiKey(), body).block();

        // 4. 解析结果
        return extractResponse(response);
    }

    @Override
    public String getStrategyType() {
        return STRATEGY_TYPE;
    }

    @Override
    public boolean supports(ModelInstance modelInstance) {
        // 可以根据 modelDefId 或其他属性判断是否支持
        ModelDefinition modelDefinition = modelManageService.getById(modelInstance.getModelDefId());
        // 判断该模型实例对应的厂商BaseURL是否属于火山引擎
        if (MODEL_BASE_URL.equals(modelDefinition.getBaseUrl())) {
            return true;
        }
        return false;
    }
}
