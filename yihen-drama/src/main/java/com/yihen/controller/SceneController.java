package com.yihen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihen.common.Result;
import com.yihen.controller.vo.*;
import com.yihen.entity.Characters;
import com.yihen.entity.Scene;
import com.yihen.search.doc.CharactersDoc;
import com.yihen.search.doc.SceneDoc;
import com.yihen.search.service.SceneSearchService;
import com.yihen.service.CharacterService;
import com.yihen.service.SceneService;
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
import java.util.List;

@Tag(name = "场景接口", description = "场景管理")
@RestController
@RequestMapping("/api/scene")
@Slf4j
public class SceneController {

    @Autowired
    private SceneService sceneService;

    @Autowired
    private SceneSearchService sceneSearchService;

    @PostMapping("/add")
    @Operation(summary = "添加场景")
    public Result<Scene> addScene(@RequestBody SceneAddRequestVO sceneAddRequestVO) {
        Scene scene = sceneService.addScene(sceneAddRequestVO.getEpisodeId(), sceneAddRequestVO.getName(), sceneAddRequestVO.getDescription());
        return Result.success(scene);
    }

    @PostMapping("/update")
    @Operation(summary = "修改场景")
    public Result<Void> updateScene(@RequestBody SceneUpdateRequestVO sceneUpdateRequestVO) {
        sceneService.updateScene(sceneUpdateRequestVO.getId(), sceneUpdateRequestVO.getName(), sceneUpdateRequestVO.getDescription());
        return Result.success("修改成功");
    }

    @DeleteMapping("/{sceneId}")
    @Operation(summary = "删除场景")
    public Result<Void> deleteScene(@PathVariable("sceneId") Long sceneId) {
        sceneService.deleteScene(sceneId);
        return Result.success("删除成功");
    }

    @PostMapping("/upload/{sceneId}")
    @Operation(summary = "上传场景图片")
    public Result<Scene> uploadScene(@PathVariable("sceneId") Long sceneId,@RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Scene scene= sceneService.upload(sceneId, file);

        return Result.success(scene);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "根据项目获取场景")
    public Result<Page<Scene>> getSceneByProjectId(@PathVariable("projectId") Long projectId,
                                                            @RequestParam(defaultValue = "1") Integer page,
                                                            @RequestParam(defaultValue = "10") Integer size
    ){
        Page<Scene> scenePage = new Page<>();
        sceneService.getByProjectId(projectId, scenePage);
        return Result.success(scenePage);
    }

    /**
     * 项目内场景搜索
     *
     * 示例：
     * GET /api/scene/10/search?keyword=林&page=1&size=10
     * GET /api/scene/10/search?keyword=lin&page=1&size=10
     */
    @GetMapping("/{projectId}/search")
    @Operation(summary = "场景搜索（Elasticsearch）")
    public Result<Page<SceneDoc>> searchSceneInProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<SceneDoc> p = new Page<>(page, size);
        sceneSearchService.searchInProject(projectId, keyword, p);
        return Result.success(p);
    }


    /**
     * 自动补全接口
     *
     * 示例：
     * GET /api/scene/10/suggest?prefix=十&size=10
     * GET /api/scene/10/suggest?prefix=sh&size=10
     */
    @GetMapping("/{projectId}/suggest")
    @Operation(summary = "场景搜索补全（Elasticsearch）")
    public Result<List<SceneSuggestItem>> suggest(
            @PathVariable Long projectId,
            @RequestParam String prefix,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return Result.success(sceneSearchService.suggestInProject(projectId,prefix, size));
    }
}
