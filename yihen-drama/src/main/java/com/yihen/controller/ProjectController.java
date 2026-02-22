package com.yihen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihen.common.Result;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.controller.vo.ProjectCreateRequestVO;
import com.yihen.controller.vo.ProjectUpdateRequestVO;
import com.yihen.entity.Project;
import com.yihen.search.doc.ProjectDoc;
import com.yihen.search.service.ProjectSearchService;
import com.yihen.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "项目接口", description = "项目管理")
@RestController
@RequestMapping("/api/projects")
@Slf4j
public class ProjectController {

    @Resource
    private ProjectService projectService;

    @Autowired
    private ProjectSearchService projectSearchService;

    @GetMapping("/property/{id}")
    @Operation(summary = "获取项目中出现人物、场景信息")
    public Result<ExtractionResultVO> getProperty(@PathVariable Long id) {
        ExtractionResultVO property = projectService.getPropertyById(id);

        return Result.success(property);
    }

    @GetMapping("/list")
    @Operation(summary = "获取项目列表")
    public Result<Page<Project>> list(
            @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") Integer page,  // 参数描述
            @Parameter(description = "每页显示数量", example = "10") @RequestParam(defaultValue = "8") Integer size
    ) {

        Page<Project> projectPage = new Page<>(page, size);
        projectService.getProject(projectPage);
        projectService.page(projectPage);
        return Result.success(projectPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取项目详情")
    public Result<Project> get(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return Result.success(project);
    }

    @PostMapping("/create")
    @Operation(summary = "创建项目")
    public Result<Project> create(@RequestBody ProjectCreateRequestVO project) {
        Project craetedProject = projectService.createProject(project);
        return Result.success(craetedProject);
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "删除项目")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success(null);
    }


    @PutMapping("/update")
    @Operation(summary = "更新项目信息")
    public Result<Project> update(@RequestBody ProjectUpdateRequestVO projectUpdateRequestVO) {
        Project project = new Project();
        BeanUtils.copyProperties(projectUpdateRequestVO, project);
        projectService.updateProjectById(project);
        return Result.success(project);
    }

    /**
     * 项目搜索接口（ES）
     *
     * 示例：
     * GET /api/projects/search?keyword=十日&status=1&styleId=2&page=1&size=8
     */
    @GetMapping("/search")
    @Operation(summary = "项目搜索（Elasticsearch）")
    public Result<Page<ProjectDoc>> search(
            @Parameter(description = "关键词（查 name/description）") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态：0草稿/1处理中/2已完成") @RequestParam(required = false) Integer status,
            @Parameter(description = "风格ID") @RequestParam(required = false) Long styleId,
            @Parameter(description = "当前页，从1开始", example = "1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "8") Long size
    ) {

        // 1) MyBatis-Plus 的 Page：current 从 1 开始
        Page<ProjectDoc> projectDocPage = new Page<>(page, size);

        // 2) 调用搜索：内部会 setRecords + setTotal
        projectSearchService.search(keyword, status, styleId, projectDocPage);

        // 3) 直接返回 Page（包含 records + total + current + size 等）
        return Result.success(projectDocPage);
    }


    /**
     * 自动补全接口
     *
     * 示例：
     * GET /api/projects/suggest?prefix=十&size=10
     * GET /api/projects/suggest?prefix=sh&size=10
     */
    @GetMapping("/suggest")
    @Operation(summary = "项目搜索补全（Elasticsearch）")
    public Result<List<String>> suggest(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return Result.success(projectSearchService.suggest(prefix, size));
    }
}
