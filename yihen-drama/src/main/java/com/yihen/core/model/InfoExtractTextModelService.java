package com.yihen.core.model;

import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.controller.vo.TextModelRequestVO;

public interface InfoExtractTextModelService extends TextModelService {

    ExtractionResultVO extract(TextModelRequestVO textModelRequestVO) throws Exception;
}
