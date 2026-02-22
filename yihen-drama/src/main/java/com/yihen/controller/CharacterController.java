package com.yihen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihen.common.Result;
import com.yihen.controller.vo.*;
import com.yihen.core.model.InfoExtractTextModelService;
import com.yihen.core.model.impl.EpisodeExtractOrchestrator;
import com.yihen.entity.Characters;
import com.yihen.entity.Episode;
import com.yihen.entity.Scene;
import com.yihen.entity.VideoTask;
import com.yihen.search.doc.CharactersDoc;
import com.yihen.search.service.CharactersSearchService;
import com.yihen.service.CharacterService;
import com.yihen.service.EpisodeService;
import com.yihen.service.VideoTaskService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Tag(name = "角色接口", description = "角色管理")
@RestController
@RequestMapping("/api/character")
@Slf4j
public class CharacterController {



    @Autowired
    private CharacterService characterService;

    @Autowired
    private CharactersSearchService charactersSearchService;

    @PostMapping("/update")
    @Operation(summary = "修改角色信息")
    public Result<Void> updateCharacter(@RequestBody CharactersUpdateRequestVO charactersUpdateRequestVO) {
        characterService.updateCharacterInfo(charactersUpdateRequestVO.getId(), charactersUpdateRequestVO.getName(), charactersUpdateRequestVO.getDescription());
        return Result.success("修改成功");
    }

    @PostMapping("/add")
    @Operation(summary = "添加角色")
    public Result<Characters> addCharacter(@RequestBody CharactersAddRequestVO charactersAddRequestVO) {
        Characters characters = characterService.addCharacterInfo(charactersAddRequestVO.getEpisodeId(), charactersAddRequestVO.getName(), charactersAddRequestVO.getDescription());

        return Result.success(characters);
    }

    @DeleteMapping("/{characterId}")
    @Operation(summary = "删除角色")
    public Result<Void> deleteCharacter( @PathVariable("characterId") Long characterId) {
         characterService.deleteCharacter(characterId);

        return Result.success("删除成功");
    }

    @PostMapping("/upload/{characterId}")
    @Operation(summary = "上传角色图片")
    public Result<Characters> uploadCharacter(@PathVariable("characterId") Long characterId, @RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Characters characters= characterService.upload(characterId, file);
        return Result.success(characters);
    }


    @GetMapping("/project/{projectId}")
    @Operation(summary = "根据项目获取角色")
    public Result<Page<Characters>> getCharacterByProjectId(@PathVariable("projectId") Long projectId,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size
    ){
        Page<Characters> charactersPage = new Page<>();
        characterService.getByProjectId(projectId, charactersPage);
        return Result.success(charactersPage);
    }


    /**
     * 项目内角色搜索
     *
     * 示例：
     * GET /api/character/10/search?keyword=林&page=1&size=10
     * GET /api/character/10/search?keyword=lin&page=1&size=10
     */
    @GetMapping("/{projectId}/search")
    @Operation(summary = "角色搜索（Elasticsearch）")
    public Result<Page<CharactersDoc>> searchCharactersInProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<CharactersDoc> p = new Page<>(page, size);
        charactersSearchService.searchInProject(projectId, keyword, p);
        return Result.success(p);
    }


    /**
     * 自动补全接口
     *
     * 示例：
     * GET /api/character/10/suggest?prefix=十&size=10
     * GET /api/character/10/suggest?prefix=sh&size=10
     */
    @GetMapping("/{projectId}/suggest")
    @Operation(summary = "角色搜索补全（Elasticsearch）")
    public Result<List<CharacterSuggestItem>> suggest(
            @PathVariable Long projectId,
            @RequestParam String prefix,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return Result.success(charactersSearchService.suggestInProject(projectId,prefix, size));
    }
}
