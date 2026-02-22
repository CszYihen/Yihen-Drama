package com.yihen.service;

import com.yihen.entity.ModelDefinition;
import com.yihen.entity.ModelInstance;
import com.yihen.enums.ModelType;
import com.yihen.enums.SceneCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class ModelMangeServiceTest {

    @Autowired
    private ModelManageService modelManageService;

    @Test
    void testAddModelDefinition() {
        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setProviderCode("Kimi");
        modelDefinition.setBaseUrl("https://api.moonshot.cn/v1");
        modelManageService.addModelDefinition(modelDefinition);
    }

    @Test
    void testAddModelInstance() {
        ModelInstance modelInstance = new ModelInstance();
        modelInstance.setModelDefId(3L);
        modelInstance.setModelCode("moonshot-v1-32k");
        modelInstance.setPath("/chat/completions");
        modelInstance.setSceneCode(SceneCode.INFO_EXTRACT);
        modelInstance.setModelType(ModelType.TEXT);
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("temperature", 0.3);
        stringObjectHashMap.put("maxTokens", 8192);
        modelInstance.setParams(stringObjectHashMap);
        modelInstance.setApiKey("sk-sO5YIzcJt2xBMi2vQ5br5thMiBG37PwHrxdX3VkzSaRrOeca");
        modelManageService.addModelInstance(modelInstance);
    }


}
