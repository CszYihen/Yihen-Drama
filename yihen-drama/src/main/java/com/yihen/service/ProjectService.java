package com.yihen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.controller.vo.ProjectCreateRequestVO;
import com.yihen.entity.Project;
import com.yihen.enums.ProjectStyle;

import java.util.List;

public interface ProjectService extends IService<Project> {

    /**
     * 创建项目
     * @param project
     */
    Project createProject(ProjectCreateRequestVO project);


    // 获取项目已有资产
    ExtractionResultVO getPropertyById(Long id);


    // 添加章节数量
    void addEpisodeCount(Long projectId);

    // 减少章节数量
    void reduceEpisodeCount(Long projectId);

    // 获取项目风格Id
    Long getProjectStyleById(Long id);


    // 删除项目
    void deleteProject(Long id);

    Project getProjectById(Long id);
    // 更新项目
    void updateProjectById(Project project);

    List<Project> getProject(Page<Project> projectPage);
}
