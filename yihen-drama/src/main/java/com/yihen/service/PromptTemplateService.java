package com.yihen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.PromptTemplate;
import com.yihen.entity.Storyboard;
import com.yihen.enums.SceneCode;

public interface PromptTemplateService extends IService<PromptTemplate> {

    /**
     * 根据业务场景编码获取提示词模板
     * @param sceneCode 业务场景编码
     * @return 提示词模板
     */
    PromptTemplate getDefaultTemplateBySceneCode(SceneCode sceneCode);

    /**
     * 修改提示词模板
     * @param template 提示词模板
     */
    void updateTemplate(PromptTemplate template);

    void deletePromptTemplateDefault(Long id);


}
