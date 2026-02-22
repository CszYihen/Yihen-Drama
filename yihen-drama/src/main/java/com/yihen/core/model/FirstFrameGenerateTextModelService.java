package com.yihen.core.model;

import com.yihen.controller.vo.TextModelRequestVO;
import com.yihen.entity.Storyboard;

import java.util.List;

public interface FirstFrameGenerateTextModelService extends TextModelService {

    String extract(TextModelRequestVO textModelRequestVO) throws Exception;
}
