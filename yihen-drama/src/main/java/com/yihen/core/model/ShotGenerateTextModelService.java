package com.yihen.core.model;

import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.controller.vo.TextModelRequestVO;
import com.yihen.entity.Storyboard;

import java.util.List;

public interface ShotGenerateTextModelService extends TextModelService {

    List<Storyboard> extract(TextModelRequestVO textModelRequestVO) throws Exception;
}
