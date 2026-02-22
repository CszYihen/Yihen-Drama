package com.yihen.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihen.constant.model.ModelManageRedisConstant;
import com.yihen.entity.ModelDefinition;
import com.yihen.entity.ModelInstance;
import com.yihen.enums.ModelType;
import com.yihen.mapper.ModelDefinitionMapper;
import com.yihen.mapper.ModelInstanceMapper;
import com.yihen.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ModelCacheInitializer implements ApplicationRunner {

    private final ModelInstanceMapper modelInstanceMapper;
    private final ModelDefinitionMapper modelDefinitionMapper;
    private final RedisUtils redisUtils;

    public ModelCacheInitializer(ModelInstanceMapper modelInstanceMapper,
                                 ModelDefinitionMapper modelDefinitionMapper,
                                 RedisUtils redisUtils) {
        this.modelDefinitionMapper = modelDefinitionMapper;
        this.modelInstanceMapper = modelInstanceMapper;
        this.redisUtils = redisUtils;
    }

    @Override
    public void run(ApplicationArguments args) {

        log.info("开始预热模型缓存...");

        // 1 先清空旧缓存（可选）
        // 1.1 清空模型实例缓存
        for (ModelType value : ModelType.values()) {
            redisUtils.delete(ModelManageRedisConstant.MODEL_INSTANCES_KEY + value);


            List<ModelInstance> modelByType = modelInstanceMapper.selectList(new LambdaQueryWrapper<ModelInstance>().eq(ModelInstance::getModelType, value));
            if(!ObjectUtils.isEmpty(modelByType)){
                // 4. 统计出所有厂商的id
                List<Long> providerIds = modelByType.stream().map(ModelInstance::getModelDefId).distinct().toList();
                // 5. 根据厂商id查询到对应厂商信息
                List<ModelDefinition> modelDefinitions = modelDefinitionMapper.selectBatchIds(providerIds);
                // 6. 整理数据  -> Map<Long,ModelDefinition>
                Map<Long, ModelDefinition> modelDefinitionMap = modelDefinitions.stream().collect(Collectors.toMap(ModelDefinition::getId, modelDefinition -> modelDefinition));
                // 7. 填充ModelInstance中的对应信息，并标记默认实例
                modelByType.forEach(modelInstance -> {
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

            }


            //2  批量写入 Redis
            redisUtils.lPushAll(
                    ModelManageRedisConstant.MODEL_INSTANCES_KEY + value,
                    modelByType
            );

            log.info("模型-{} 缓存预热完成，共加载 {} 条数据", value, modelByType.size());

        }

        // 2. 缓存model definition
        // 2.1 清空旧缓存
        redisUtils.delete(ModelManageRedisConstant.MODEL_DEFINITIONS_KEY);
        List<ModelDefinition> modelDefinitions = modelDefinitionMapper.selectList(null);
        //2.2 批量写入 Redis
        redisUtils.lPushAll(
                ModelManageRedisConstant.MODEL_DEFINITIONS_KEY,
                modelDefinitions
        );
        log.info("模型definition缓存预热完成，共加载 {} 条数据",  modelDefinitions.size());

    }

    public void runByType(ModelType modelType) {

        log.info("开始预热模型缓存...");

        // 1 先清空旧缓存（可选）
        // 1.1 清空模型实例缓存
            redisUtils.delete(ModelManageRedisConstant.MODEL_INSTANCES_KEY+modelType);


            List<ModelInstance> modelByType = modelInstanceMapper
                    .selectList(new LambdaQueryWrapper<ModelInstance>()
                            .eq(ModelInstance::getModelType,modelType));

        if(!ObjectUtils.isEmpty(modelByType)){
            // 4. 统计出所有厂商的id
            List<Long> providerIds = modelByType.stream().map(ModelInstance::getModelDefId).distinct().toList();
            // 5. 根据厂商id查询到对应厂商信息
            List<ModelDefinition> modelDefinitions = modelDefinitionMapper.selectBatchIds(providerIds);
            // 6. 整理数据  -> Map<Long,ModelDefinition>
            Map<Long, ModelDefinition> modelDefinitionMap = modelDefinitions.stream().collect(Collectors.toMap(ModelDefinition::getId, modelDefinition -> modelDefinition));
            // 7. 填充ModelInstance中的对应信息，并标记默认实例
            modelByType.forEach(modelInstance -> {
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

        }


            //2  批量写入 Redis
            redisUtils.lPushAll(
                    ModelManageRedisConstant.MODEL_INSTANCES_KEY+modelType,
                    modelByType
            );

            log.info("模型-{} 缓存预热完成，共加载 {} 条数据",modelType, modelByType.size());

        }


    public void runModelDefinition() {

        log.info("开始预热模型缓存...");

        // 2. 缓存model definition
        // 2.1 清空旧缓存

        // 2. 缓存model definition
        // 2.1 清空旧缓存
        redisUtils.delete(ModelManageRedisConstant.MODEL_DEFINITIONS_KEY);
        List<ModelDefinition> modelDefinitions = modelDefinitionMapper.selectList(null);
        //3  批量写入 Redis
        redisUtils.lPushAll(
                ModelManageRedisConstant.MODEL_DEFINITIONS_KEY,
                modelDefinitions
        );
        log.info("模型definition缓存预热完成，共加载 {} 条数据",  modelDefinitions.size());

    }

}
