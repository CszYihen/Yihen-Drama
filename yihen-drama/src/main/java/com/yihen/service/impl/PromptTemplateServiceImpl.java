package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.entity.PromptTemplate;
import com.yihen.entity.PromptTemplateDefault;
import com.yihen.entity.Storyboard;
import com.yihen.enums.SceneCode;
import com.yihen.mapper.PromptTemplateMapper;
import com.yihen.service.PromptTemplateDefaultService;
import com.yihen.service.PromptTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PromptTemplateServiceImpl extends ServiceImpl<PromptTemplateMapper, PromptTemplate> implements PromptTemplateService {

    private static final ExecutorService EXECUTORSERVICE = Executors.newFixedThreadPool(2);

    @Autowired
    private PromptTemplateMapper promptTemplateMapper;

    @Autowired
    private PromptTemplateDefaultService promptTemplateDefaultService;

    @Override
    public boolean save(PromptTemplate entity) {
        boolean result = super.save(entity);

        if (result) {
            CompletableFuture.runAsync(() -> {
                boolean hasDefault = promptTemplateDefaultService.count(new LambdaQueryWrapper<PromptTemplateDefault>()
                        .eq(PromptTemplateDefault::getSceneCode, entity.getSceneCode())) > 0;

                if (!hasDefault) {
                    PromptTemplateDefault defaultTemplate = new PromptTemplateDefault();
                    defaultTemplate.setSceneCode(entity.getSceneCode());
                    defaultTemplate.setPromptTemplateId(entity.getId());
                    defaultTemplate.setStatus((byte) 1);
                    promptTemplateDefaultService.save(defaultTemplate);
                }
            }, EXECUTORSERVICE);
        }

        return result;
    }

    @Override
    public PromptTemplate getDefaultTemplateBySceneCode(SceneCode sceneCode) {
        Long defaultTemplateId = promptTemplateDefaultService.getDefaultTemplateIdBySceneCode(sceneCode);

        if (defaultTemplateId == null) {
            return null;
        }

        return getById(defaultTemplateId);
    }

    @Override
    public void updateTemplate(PromptTemplate template) {
        updateById(template);
    }

    @Override
    public void deletePromptTemplateDefault(Long id) {
        // 检查该模板是否为默认模板
        boolean isDefault = promptTemplateDefaultService.checkExistByTemplateId(id);
        if (isDefault) {
            throw new RuntimeException("该模板为默认模板，不能删除，请设置其他默认模板后删除");
        }

        removeById(id);
    }



}


