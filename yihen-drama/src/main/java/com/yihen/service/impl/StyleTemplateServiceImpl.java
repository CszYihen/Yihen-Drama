package com.yihen.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.entity.StyleTemplate;
import com.yihen.mapper.StyleTemplateMapper;
import com.yihen.service.StyleTemplateService;
import org.springframework.stereotype.Service;

@Service
public class StyleTemplateServiceImpl extends ServiceImpl<StyleTemplateMapper, StyleTemplate> implements StyleTemplateService {
}
