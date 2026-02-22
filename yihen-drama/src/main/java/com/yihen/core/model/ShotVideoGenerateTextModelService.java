package com.yihen.core.model;

import com.yihen.controller.vo.TextModelRequestVO;

public interface ShotVideoGenerateTextModelService extends TextModelService {

    String extract(TextModelRequestVO textModelRequestVO) throws Exception;
}
