package com.yihen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.PromptTemplateDefault;
import com.yihen.enums.SceneCode;

import java.util.Map;

public interface PromptTemplateDefaultService extends IService<PromptTemplateDefault> {

    // 根据场景码获取默认模板ID
    Long getDefaultTemplateIdBySceneCode(SceneCode sceneCode);

    // 更新默认模板
    void updateDefault(PromptTemplateDefault promptTemplateDefault);

    Map<SceneCode, PromptTemplateDefault> getAllDefaultUnderScene();

    // 根据模板id查询该模板是否为默认模板
    boolean checkExistByTemplateId(Long templateId);
}
