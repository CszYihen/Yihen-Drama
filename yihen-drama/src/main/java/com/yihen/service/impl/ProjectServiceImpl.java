package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.asyn.ProjectPersistFacade;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.controller.vo.ProjectCreateRequestVO;
import com.yihen.entity.Characters;
import com.yihen.entity.Episode;
import com.yihen.entity.Project;
import com.yihen.entity.Scene;
import com.yihen.enums.ProjectStyle;
import com.yihen.mapper.ProjectMapper;
import com.yihen.service.CharacterService;
import com.yihen.service.EpisodeService;
import com.yihen.service.ProjectService;
import com.yihen.service.SceneService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service("projectServiceImpl")
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Autowired
    private CharacterService characterService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private ProjectPersistFacade projectPersistFacade;

    private static final ExecutorService EXECUTORSERVICE = Executors.newFixedThreadPool(5);

    @Override
    public Project createProject(ProjectCreateRequestVO project) {
        // 合法性校验
        if (ObjectUtils.isEmpty(project.getName())) {
            throw new RuntimeException("项目名称不可为空");
        }

        // 创建项目对象
        Project createdProject = new Project();
        createdProject.setName(project.getName());
        createdProject.setStyleId(project.getStyle());

        save(createdProject);
        return createdProject;
    }

    @Override
    public ExtractionResultVO getPropertyById(Long id) {
        ExtractionResultVO extractionResultVO = projectMapper.getPropertyById(id);

//
//        LambdaQueryWrapper<Characters> charactersLambdaQueryWrapper = new LambdaQueryWrapper<Characters>().eq(Characters::getProjectId, id);
//            List<Characters> characters = characterService.list(charactersLambdaQueryWrapper);
//
//            LambdaQueryWrapper<Scene> sceneLambdaQueryWrapper = new LambdaQueryWrapper<Scene>().eq(Scene::getProjectId, id);
//            List<Scene> scenes = sceneService.list(sceneLambdaQueryWrapper);
//
//            ExtractionResultVO extractionResultVO = new ExtractionResultVO();
//            extractionResultVO.setCharacters(characters);
//            extractionResultVO.setScenes(scenes);
            return extractionResultVO;
    }

    @Override
    public void addEpisodeCount(Long projectId) {
        projectMapper.addEpisodeCount(projectId);
    }

    @Override
    public void reduceEpisodeCount(Long projectId) {
        projectMapper.reduceEpisodeCount(projectId);
    }

    @Override
    public Long getProjectStyleById(Long id) {
        Long projectStyleById = projectMapper.getProjectStyleById(id);

        return projectStyleById;
    }

    @Override
    public void deleteProject(Long id) {
        removeById(id);

        // 删除关联内容
        projectPersistFacade.deleteProjectRelatedContentAsync(id);

    }

    @Override
    public Project getProjectById(Long id) {
        return getById(id);
    }

    @Override
    public void updateProjectById(Project project) {
        updateById(project);
    }

    @Override
    public List<Project> getProject(Page<Project> projectPage) {
        Page<Project> page = page(projectPage);
        return page.getRecords();
    }


}
