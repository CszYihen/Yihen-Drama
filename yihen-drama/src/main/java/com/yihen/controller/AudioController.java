package com.yihen.controller;

import com.yihen.common.Result;
import com.yihen.controller.vo.AudioRequestVO;
import com.yihen.controller.vo.CharactersAddRequestVO;
import com.yihen.controller.vo.CharactersUpdateRequestVO;
import com.yihen.entity.Characters;
import com.yihen.service.CharacterService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Tag(name = "音频接口", description = "音频管理")
@RestController
@RequestMapping("/api/audio")
@Slf4j
public class AudioController {



    @Autowired
    private CharacterService characterService;

    @PostMapping("/create")
    @Operation(summary = "创建音频")
    public Result<Void> createAudio(@RequestBody AudioRequestVO audioRequestVO) {
        return Result.success("修改成功");
    }


}
