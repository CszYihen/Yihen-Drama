package com.yihen.core.model.strategy.image.impl;

import com.alibaba.fastjson.JSONObject;
import com.yihen.config.properties.MinioProperties;
import com.yihen.constant.MinioConstant;
import com.yihen.controller.vo.ImageModelRequestVO;
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

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
@Slf4j
@Component
public class VolcanoImageModelStrategy implements ImageModelStrategy {
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

    @Override
    public String create(ImageModelRequestVO imageModelRequestVO) throws Exception {
        // TODO
        return "";
    }

    @Override
    public String createByTextAndImage(ImageModelRequestVO imageModelRequestVO) throws Exception {
        // 0. 获取分镜对象
        Storyboard storyboard = (Storyboard) imageModelRequestVO.getObject();

        ArrayList<String> image = new ArrayList<>();

        // 将分镜关联图像转换为base64
        // 场景图
        List<Scene> scenes = storyboard.getScenes();
        for (Scene scene : scenes) {
            String objectName = scene.getThumbnail().replace(minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/", "");
            GetObjectResponse object = minioUtil.getObject(MinioConstant.BUCKET_NAME, objectName);
            String imageFormat = UrlUtils.extractFileExtension(scene.getThumbnail());
            byte[] bytes = object.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            String base64DataUri = "data:image/" + imageFormat + ";base64," + base64Image;
            image.add(base64DataUri);
        }

        // 角色图
        List<Characters> characters = storyboard.getCharacters();
        for (Characters character : characters) {
            String objectName = character.getAvatar().replace(minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/", "");
            GetObjectResponse object = minioUtil.getObject(MinioConstant.BUCKET_NAME, objectName);
            String imageFormat = UrlUtils.extractFileExtension(character.getAvatar());
            byte[] bytes = object.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            String base64DataUri = "data:image/" + imageFormat + ";base64," + base64Image;
            image.add(base64DataUri);
        }


        // 1. 获取模型实例
        ModelInstance modelInstance = modelManageService.getModelInstanceById(imageModelRequestVO.getModelInstanceId());

        // 2. 获取厂商定义的baseurl
        String baseUrl = modelDefinitionMapper.getBaseUrlById(modelInstance.getModelDefId());

        // 3. 拼接发送请求信息
        HashMap<String, Object> body = (HashMap<String, Object>) modelInstance.getParams();
        if (ObjectUtils.isEmpty(body)) {
            body = new HashMap<>();
        }
        body.put("model", modelInstance.getModelCode());

        // 放入文本提示词
        body.put("prompt", storyboard.getImagePrompt());

        // 放入参考图
        body.put("image", image);


        // 3. 发送请求
        String response = httpExecutor.post(baseUrl, modelInstance.getPath(), modelInstance.getApiKey(), body).block();

        // 4. 解析结果
        return extractResponse(response);
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
