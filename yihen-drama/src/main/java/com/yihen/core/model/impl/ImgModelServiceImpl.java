package com.yihen.core.model.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.core.model.ImgModelService;
import com.yihen.core.model.TextModelService;
import com.yihen.entity.ModelInstance;
import com.yihen.http.HttpExecutor;
import com.yihen.mapper.ModelDefinitionMapper;
import com.yihen.mapper.ModelInstanceMapper;
import com.yihen.service.PromptTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ImgModelServiceImpl extends ServiceImpl<ModelInstanceMapper, ModelInstance> implements ImgModelService {
    @Autowired
    private ModelDefinitionMapper modelDefinitionMapper;

    @Autowired
    private PromptTemplateService promptTemplateService;

    @Autowired
    private HttpExecutor  httpExecutor;

    @Override
    public String generate(Long modelId , String text) throws Exception {

        // 1. 获取模型实例信息
        ModelInstance modelInstance = getById(modelId);

        // 2. 获取厂商定义的baseurl
        String baseUrl = modelDefinitionMapper.getBaseUrlById(modelInstance.getModelDefId());
        // 3. 拼接发送请求信息
        HashMap<String, Object> body = (HashMap<String, Object>) modelInstance.getParams();
        if (ObjectUtils.isEmpty(body)) {
            body= new HashMap<>();
        }
        body.put("model", modelInstance.getModelCode());

        // 3.1 构建消息发送， 该例子为火山引擎
        body.put("prompt", text);

        // 4. 发送请求
        // 5. 获取响应结果
        String response = httpExecutor.post(baseUrl, modelInstance.getPath(), modelInstance.getApiKey(), body).block();

        // 解析结果
        String content = extractResponse(response);

        return content;
    }

    // 响应结果提取
    private String extractResponse(String response) throws Exception {

            // 1. 转成JSONObject对象
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject.containsKey("error")) {
                // 调用失败
                String errorMessage = jsonObject.getJSONObject("error").getString("message");
                throw new Exception(errorMessage);
            }

            String content = jsonObject.getJSONArray("data")
                    .getJSONObject(0).getString("url");

            if (org.springframework.util.ObjectUtils.isEmpty(content)) {
                throw new Exception("返回结果结构正确，但是返回数据为空！再次尝试");
            }

            return content;

    }
}
