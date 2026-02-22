package com.yihen.core.model.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihen.controller.vo.CharactersRequestVO;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.controller.vo.SceneRequestVO;
import com.yihen.controller.vo.TextModelRequestVO;
import com.yihen.core.model.InfoExtractTextModelService;
import com.yihen.core.model.PropertyGenerateImgModelService;
import com.yihen.entity.Characters;
import com.yihen.entity.PromptTemplate;
import com.yihen.entity.Scene;
import com.yihen.enums.ProjectStyle;
import com.yihen.enums.SceneCode;
import com.yihen.mapper.ProjectMapper;
import com.yihen.mapper.StyleTemplateMapper;
import com.yihen.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PropertyGenerateImgModelServiceImpl extends ImgModelServiceImpl implements PropertyGenerateImgModelService {
    @Autowired
    private PromptTemplateService promptTemplateService;

    @Autowired
    private StyleTemplateService styleTemplateService;

    @Autowired
    private CharacterService characterService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private ProjectMapper projectMapper;



    @Override
    public Characters generateCharacter(CharactersRequestVO charactersRequestVO) throws Exception {
        // 获取项目风格描述
        Long projectStyleId = projectMapper.getProjectStyleById(charactersRequestVO.getProjectId());
        String description = styleTemplateService.getById(projectStyleId).getDescription();

        // 构建提示词
        PromptTemplate promptTemplate = promptTemplateService.getDefaultTemplateBySceneCode(SceneCode.CHARACTER_GEN);

        String message = promptTemplate.getPromptContent()
                .replace("{{input}}", charactersRequestVO.getDescription())
                .replace("{{style_template}}", description);

        String imgUrl = generate(charactersRequestVO.getModelInstanceId(), message);

        // 获取角色
        Characters characters = characterService.getById(charactersRequestVO.getCharcterId());
        characters.setAvatar(imgUrl);
        characters.setDescription(charactersRequestVO.getDescription());

        return characters;
    }

    @Override
    public Scene generateScene(SceneRequestVO sceneRequestVO) throws Exception {
        // 获取项目风格描述
        Long projectStyleId = projectMapper.getProjectStyleById(sceneRequestVO.getProjectId());
        String description = styleTemplateService.getById(projectStyleId).getDescription();

        // 构建提示词
        PromptTemplate promptTemplate = promptTemplateService.getDefaultTemplateBySceneCode(SceneCode.SCENE_GEN);

        String message = promptTemplate.getPromptContent()
                .replace("{{input}}", sceneRequestVO.getDescription())
                .replace("{{style_template}}", description);

        String imgUrl = generate(sceneRequestVO.getModelInstanceId(), message);

        // 获取角色
        Scene scene = sceneService.getById(sceneRequestVO.getSceneId());
        scene.setThumbnail(imgUrl);
        scene.setDescription(sceneRequestVO.getDescription());

        return scene;
    }
}
