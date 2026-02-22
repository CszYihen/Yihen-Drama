package com.yihen.core.model;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.ModelInstance;

public interface TextModelService extends IService<ModelInstance> {


    String generate(Long modelId , String text) throws Exception;


}
