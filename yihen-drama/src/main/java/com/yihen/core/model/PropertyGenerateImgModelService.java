package com.yihen.core.model;

import com.yihen.controller.vo.CharactersRequestVO;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.controller.vo.SceneRequestVO;
import com.yihen.controller.vo.TextModelRequestVO;
import com.yihen.entity.Characters;
import com.yihen.entity.Scene;

public interface PropertyGenerateImgModelService extends ImgModelService {

    // 角色生成
    Characters generateCharacter(CharactersRequestVO charactersRequestVO) throws Exception;

    // 场景生成
    Scene generateScene(SceneRequestVO sceneRequestVO) throws Exception;
}
