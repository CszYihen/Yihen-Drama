package com.yihen.service;

import com.yihen.controller.vo.TextModelRequestVO;
import com.yihen.core.model.ImgModelService;
import com.yihen.core.model.TextModelService;
import com.yihen.enums.SceneCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImgModelServiceTest {
    @Autowired
    @Qualifier("imgModelServiceImpl")
    private ImgModelService imgModelService;

    @Test
    void generate() throws Exception {
        String generate = imgModelService.generate(7L, "请生成一个角色正面全身照，白色背景，下面是角色的特征： 男性，年轻，眉头微蹙，眼神专注，掌心带薄汗，指尖能泛起淡青色气流。风格为 现代二次元动漫风格");

        System.out.println(  generate);
    }
}
