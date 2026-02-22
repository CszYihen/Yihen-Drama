package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.entity.*;
import com.yihen.enums.EpisodeStep;
import com.yihen.enums.ModelType;
import com.yihen.mapper.ModelDefinitionMapper;
import com.yihen.mapper.ModelInstanceMapper;
import com.yihen.service.ModelInstanceDefaultService;
import com.yihen.service.ModelManageService;
import com.yihen.controller.vo.ModelInstanceResponseVo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service("modelManageServiceImpl")
public class ModelManageServiceImpl extends ServiceImpl<ModelDefinitionMapper, ModelDefinition> implements ModelManageService {

    @Autowired
    private ModelInstanceMapper modelInstanceMapper;

    @Autowired
    private ModelInstanceDefaultService modelInstanceDefaultService;

    private static final ExecutorService EXECUTORSERVICE = Executors.newFixedThreadPool(5);


    @Override
    public void addModelDefinition(ModelDefinition modelDefinition) {
       save(modelDefinition);
    }

    @Override
    public void addModelInstance(ModelInstance modelInstance) {
        // 字段校验
        checkModelInstance(modelInstance);

        // 判断是否需要设置为默认
        // 创建异步任务，异步更新数据库
        CompletableFuture.runAsync(() -> {
            boolean checkedExistUnderType = modelInstanceDefaultService.checkExistUnderType(modelInstance.getModelType());

            // 没有默认，则设置首个为默认
            if (!checkedExistUnderType) {
                ModelInstanceDefault modelInstanceDefault = new ModelInstanceDefault();
                modelInstanceDefault.setModelInstanceId(modelInstance.getId());
                modelInstanceDefault.setModelType(modelInstance.getModelType());
                modelInstanceDefaultService.addDefault(modelInstanceDefault);
            }

        }, EXECUTORSERVICE);


        modelInstanceMapper.insert(modelInstance);
    }

    @Override
    public List<ModelInstance> getModelInstanceByType(Page<ModelInstance> modelInstancePage,ModelType modelType) {
        // 1. 根据modelType查询模型实例
        LambdaQueryWrapper<ModelInstance> modelInstanceLambdaQueryWrapper = new LambdaQueryWrapper<ModelInstance>().eq(ModelInstance::getModelType, modelType);
        modelInstanceMapper.selectPage(modelInstancePage, modelInstanceLambdaQueryWrapper);
        List<ModelInstance> modelInstances = modelInstancePage.getRecords();

        if (ObjectUtils.isEmpty(modelInstances)) {
            return new ArrayList<ModelInstance>();
        }

        // 4. 统计出所有厂商的id
        List<Long> providerIds = modelInstances.stream().map(ModelInstance::getModelDefId).distinct().toList();
        // 5. 根据厂商id查询到对应厂商信息
        List<ModelDefinition> modelDefinitions = listByIds(providerIds);
        // 6. 整理数据  -> Map<Long,ModelDefinition>
        Map<Long, ModelDefinition> modelDefinitionMap = modelDefinitions.stream().collect(Collectors.toMap(ModelDefinition::getId, modelDefinition -> modelDefinition));
        // 7. 填充ModelInstance中的对应信息，并标记默认实例
        modelInstances.forEach(modelInstance -> {
            ModelDefinition modelDefinition = modelDefinitionMap.get(modelInstance.getModelDefId());
            if (modelDefinition != null) {
                // 统一使用小写
                modelInstance.setProviderCode(modelDefinition.getProviderCode() != null ? 
                    modelDefinition.getProviderCode().toLowerCase() : null);
                modelInstance.setBaseUrl(modelDefinition.getBaseUrl());
            } else {
                modelInstance.setProviderCode(null);
                modelInstance.setBaseUrl(null);
            }
        });


        return modelInstances;
    }

    @Override
    public ModelInstance getDefaultModelInstanceByType(ModelType modelType) {
        // 查询该类型的默认模型实例
        LambdaQueryWrapper<ModelInstanceDefault> defaultQueryWrapper = new LambdaQueryWrapper<ModelInstanceDefault>()
                .eq(ModelInstanceDefault::getModelType, modelType)
                .eq(ModelInstanceDefault::getStatus, (byte) 1)
                .last("LIMIT 1");
        ModelInstanceDefault defaultInstance = modelInstanceDefaultService.getOne(defaultQueryWrapper);
        
        if (defaultInstance == null) {
            return null;
        }
        
        // 查询对应的模型实例
        ModelInstance modelInstance = modelInstanceMapper.selectById(defaultInstance.getModelInstanceId());
        if (modelInstance != null) {
            // 填充额外信息
            ModelDefinition modelDefinition = getById(modelInstance.getModelDefId());
            if (modelDefinition != null) {
                modelInstance.setProviderCode(modelDefinition.getProviderCode() != null ? 
                    modelDefinition.getProviderCode().toLowerCase() : null);
                modelInstance.setBaseUrl(modelDefinition.getBaseUrl());
            }
//            modelInstance.setIsDefault(true);
        }
        
        return modelInstance;
    }

    @Override
    public void updateModelDefinition(ModelDefinition modelDefinition) {
        if (modelDefinition.getId() == null) {
            throw new RuntimeException("厂商ID不能为空");
        }
        updateById(modelDefinition);
    }

    @Override
    public ModelInstance getModelInstanceById(Long id) {
        ModelInstance modelInstance = modelInstanceMapper.selectById(id);
        return modelInstance;
    }

    @Override
    public String getBaseUrlById(Long id) {
        return getBaseMapper().getBaseUrlById( id);
    }

    @Override
    public List<ModelDefinition> getModelDefinition(Page<ModelDefinition> modelDefinitionPage) {
        page(modelDefinitionPage);

        return modelDefinitionPage.getRecords();
    }

    @Override
    public void testModelInstanceConnectivity(Long id) {

    }

    @Override
    public void deleteModelInstance(Long id) {

        modelInstanceMapper.deleteById(id);
    }

    @Override
    public void deleteModelDefinition(Long id) {
        // 如果该厂商下有模型实例，则不允许删除
        Long count = modelInstanceMapper.selectCount(new LambdaQueryWrapper<ModelInstance>().eq(ModelInstance::getModelDefId, id));

        if (count > 0) {
            throw new RuntimeException("该厂商下有模型实例，请先删除模型实例");
        }
        removeById(id);
    }

    @Override
    public void updateModelInstance(ModelInstance modelInstance) {
        modelInstanceMapper.updateById(modelInstance);
    }

    private static void checkModelInstance(ModelInstance modelInstance) {
        if (ObjectUtils.isEmpty(modelInstance.getModelType())) {
            throw new RuntimeException("模型类型不能为空");
        }
        if (ObjectUtils.isEmpty(modelInstance.getModelCode())) {
            throw new RuntimeException("模型编码不能为空");
        }
        if (ObjectUtils.isEmpty(modelInstance.getModelDefId())) {
            throw new RuntimeException("厂商定义ID不能为空");
        }
        if (ObjectUtils.isEmpty(modelInstance.getApiKey())) {
            throw new RuntimeException("apiKey不能为空");
        }

        if (ObjectUtils.isEmpty(modelInstance.getInstanceName())) {
            modelInstance.setInstanceName(modelInstance.getModelCode());
        }
    }
}
