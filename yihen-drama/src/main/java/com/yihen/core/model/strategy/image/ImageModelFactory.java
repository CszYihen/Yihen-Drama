package com.yihen.core.model.strategy.image;

import com.yihen.core.model.strategy.image.impl.VolcanoImageModelStrategy;
import com.yihen.core.model.strategy.video.VideoModelStrategy;
import com.yihen.core.model.strategy.video.impl.VolcanoVideoModelStrategy;
import com.yihen.entity.ModelInstance;
import com.yihen.service.ModelManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图像模型策略工厂
 * 根据模型实例获取对应的策略实现
 */
@Slf4j
@Component
public class ImageModelFactory {

    private final Map<Long, ImageModelStrategy> strategyMap = new ConcurrentHashMap<>();

    @Autowired
    private List<ImageModelStrategy> strategies;

    @Autowired
    private VolcanoImageModelStrategy volcanoImageModelStrategy;

    @Autowired
    private ModelManageService modelManageService;

    /**
     * 根据模型实例获取对应的策略
     * @param modelInstanceId 模型实例
     * @return 视频模型策略
     */
    public ImageModelStrategy getStrategy(Long modelInstanceId) {
        if (modelInstanceId == null) {
            throw new IllegalArgumentException("模型实例Id不能为空");
        }

        // 如果策略已缓存，直接返回
        if (strategyMap.containsKey(modelInstanceId)) {
            return strategyMap.get(modelInstanceId);
        }
        // 获取模型实例
        ModelInstance modelInstance = modelManageService.getModelInstanceById(modelInstanceId);
        // 遍历所有策略，找到支持该模型的策略
        for (ImageModelStrategy strategy : strategies) {
            if (strategy.supports(modelInstance)) {
                // 缓存策略
                strategyMap.put(modelInstanceId, strategy);
                log.debug("为模型 [{}] 选择策略: {}",modelInstanceId, strategy.getClass().getSimpleName());
                return strategy;
            }
        }

        // 默认使用火山引擎策略
        log.warn("未找到匹配的策略，使用默认火山引擎策略");
        return volcanoImageModelStrategy;
    }

    /**
     * 创建缓存key
     */
    private String getCacheKey(ModelInstance modelInstance) {
        return modelInstance.getModelDefId() + "_" + modelInstance.getId();
    }

    /**
     * 根据策略类型获取策略
     * @param strategyType 策略类型（如：volcano, jimeng, minimax）
     * @return 视频模型策略
     */
    public ImageModelStrategy getStrategyByType(String strategyType) {
        for (ImageModelStrategy strategy : strategies) {
            if (strategy.getStrategyType().equalsIgnoreCase(strategyType)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("未找到策略类型: " + strategyType);
    }
}
