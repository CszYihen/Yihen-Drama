package com.yihen.core.model.impl;

import com.alibaba.fastjson.JSON;
import com.yihen.controller.vo.TextModelRequestVO;
import com.yihen.core.model.FirstFrameGenerateTextModelService;
import com.yihen.core.model.ShotVideoGenerateTextModelService;
import com.yihen.entity.PromptTemplate;
import com.yihen.entity.Storyboard;
import com.yihen.entity.StyleTemplate;
import com.yihen.mapper.ProjectMapper;
import com.yihen.service.ProjectService;
import com.yihen.service.PromptTemplateService;
import com.yihen.service.StyleTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ShotVideoGenerateTextModelServiceImpl extends TextModelServiceImpl implements ShotVideoGenerateTextModelService {
    @Autowired
    private PromptTemplateService promptTemplateService;

    @Autowired
    private StyleTemplateService styleTemplateService;


    @Autowired
    private ProjectMapper projectMapper;



    @Override
    public  String extract(TextModelRequestVO textModelRequestVO) throws Exception {
        // 1) 提示词模板
        PromptTemplate promptTemplate =
                promptTemplateService.getDefaultTemplateBySceneCode(textModelRequestVO.getSceneCode());

        // 2) 拿出分镜
        Storyboard storyboard =(Storyboard) textModelRequestVO.getObject();

        // 3) 获取风格模板
        Long projectStyleById = projectMapper.getProjectStyleById(textModelRequestVO.getProjectId());
        StyleTemplate styleTemplate = styleTemplateService.getById(projectStyleById);

        // 4) 替换模板变量
        String message = promptTemplate.getPromptContent()
                .replace("{{shot_description}}", storyboard.getDescription())
                .replace("{{first_frame_prompt}}", storyboard.getImagePrompt());
//                .replace("{{characters_json}}", JSON.toJSONString(storyboard.getCharacters()))
//                .replace("{{scenes_json}}", JSON.toJSONString(storyboard.getScenes()))
//                .replace("{{style_template}}", JSON.toJSONString(styleTemplate.getDescription()));

        textModelRequestVO.setText(message);

        // 5) 调用大模型
        String response = generate(textModelRequestVO.getModelId(), message);


        return response;
    }


}
