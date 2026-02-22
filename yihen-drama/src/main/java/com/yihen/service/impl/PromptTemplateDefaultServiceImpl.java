package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.common.Result;
import com.yihen.entity.PromptTemplate;
import com.yihen.entity.PromptTemplateDefault;
import com.yihen.enums.SceneCode;
import com.yihen.mapper.PromptTemplateDefaultMapper;
import com.yihen.service.PromptTemplateDefaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PromptTemplateDefaultServiceImpl extends ServiceImpl<PromptTemplateDefaultMapper, PromptTemplateDefault> implements PromptTemplateDefaultService {
    @Autowired
    private PromptTemplateDefaultMapper promptTemplateDefaultMapper;
    @Override
    public Long getDefaultTemplateIdBySceneCode(SceneCode sceneCode) {
        Long promptTemplateDefaultId = promptTemplateDefaultMapper.getPromptTemplateDefaultIdBySceneCode(sceneCode);
        return promptTemplateDefaultId;
    }

    @Override
    @Transactional
    public void updateDefault(PromptTemplateDefault promptTemplateDefault) {
        if (promptTemplateDefault.getPromptTemplateId() == null) {
            throw new RuntimeException("提示词模板ID不能为空");
        }
        if (promptTemplateDefault.getSceneCode() == null) {
            throw new RuntimeException("场景代码不能为空");
        }


        LambdaQueryWrapper<PromptTemplateDefault> queryWrapper = new LambdaQueryWrapper<PromptTemplateDefault>()
                .eq(PromptTemplateDefault::getSceneCode, promptTemplateDefault.getSceneCode());
        
        PromptTemplateDefault existing = getOne(queryWrapper);
        
        if (existing == null) {
            PromptTemplateDefault newDefault = new PromptTemplateDefault();
            newDefault.setSceneCode(promptTemplateDefault.getSceneCode());
            newDefault.setPromptTemplateId(promptTemplateDefault.getPromptTemplateId());
            newDefault.setStatus((byte) 1);
            save(newDefault);
        } else {
            LambdaUpdateWrapper<PromptTemplateDefault> updateWrapper = new LambdaUpdateWrapper<PromptTemplateDefault>()
                    .eq(PromptTemplateDefault::getSceneCode, promptTemplateDefault.getSceneCode())
                    .set(PromptTemplateDefault::getPromptTemplateId, promptTemplateDefault.getPromptTemplateId());

            update(updateWrapper);
        }
    }

    @Override
    public Map<SceneCode, PromptTemplateDefault> getAllDefaultUnderScene() {
        List<PromptTemplateDefault> promptTemplateDefaults = list();
        Map<SceneCode, PromptTemplateDefault> collect = promptTemplateDefaults.stream().collect(Collectors.toMap(PromptTemplateDefault::getSceneCode, promptTemplateDefault -> promptTemplateDefault));

        return collect;
    }

    @Override
    public boolean checkExistByTemplateId(Long templateId) {
        LambdaQueryWrapper<PromptTemplateDefault> eq = new LambdaQueryWrapper<PromptTemplateDefault>()
                .eq(PromptTemplateDefault::getPromptTemplateId, templateId);

       return count(eq) > 0;
    }
}
