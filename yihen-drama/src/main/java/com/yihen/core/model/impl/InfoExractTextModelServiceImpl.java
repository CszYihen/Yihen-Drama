package com.yihen.core.model.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.core.model.InfoExtractTextModelService;
import com.yihen.entity.Characters;
import com.yihen.entity.PromptTemplate;
import com.yihen.entity.Scene;
import com.yihen.mapper.ProjectMapper;
import com.yihen.service.*;
import com.yihen.controller.vo.TextModelRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InfoExractTextModelServiceImpl extends TextModelServiceImpl implements InfoExtractTextModelService {
    @Autowired
    private PromptTemplateService promptTemplateService;




    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public ExtractionResultVO extract(TextModelRequestVO textModelRequestVO) throws Exception {
        // 构建提示词
        PromptTemplate promptTemplate = promptTemplateService.getDefaultTemplateBySceneCode(textModelRequestVO.getSceneCode());

        // 查询已有资产
        ExtractionResultVO property = projectMapper.getPropertyById(textModelRequestVO.getProjectId());

        // 替换对应提示词
        String message = promptTemplate.getPromptContent()
                .replace("{{input}}",textModelRequestVO.getText())
                .replace("{{existing_characters}}", property.getCharacters().toString())
                .replace("{{existing_scenes}}", property.getScenes().toString());

        textModelRequestVO.setText(message);

        String response = generate(textModelRequestVO.getModelId(), message);
        
        ExtractionResultVO result = new ExtractionResultVO();
        
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            
            JSONArray charactersArray = jsonObject.getJSONArray("characters");
            JSONArray scenesArray = jsonObject.getJSONArray("scenes");
            
            if (charactersArray != null) {
                for (int i = 0; i < charactersArray.size(); i++) {
                    JSONObject charObj = charactersArray.getJSONObject(i);
                    Characters character = new Characters();
                    character.setName(charObj.getString("name"));
                    character.setDescription(charObj.getString("description"));
                    character.setNew(Boolean.parseBoolean(charObj.getString("isNew")));
                    result.getCharacters().add(character);
                }
            }
            
            if (scenesArray != null) {
                for (int i = 0; i < scenesArray.size(); i++) {
                    JSONObject sceneObj = scenesArray.getJSONObject(i);
                    Scene scene = new Scene();
                    scene.setName(sceneObj.getString("name"));
                    scene.setDescription(sceneObj.getString("description"));
                    scene.setNew(Boolean.parseBoolean(sceneObj.getString("isNew")));
                    result.getScenes().add(scene);
                }
            }
            
        } catch (Exception e) {
            log.error("解析提取结果失败: {}", e.getMessage());
            throw new Exception("解析提取结果失败: " + e.getMessage());
        }
        
        return result;
    }
}
