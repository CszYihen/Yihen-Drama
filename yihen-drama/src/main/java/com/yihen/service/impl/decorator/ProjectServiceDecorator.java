package com.yihen.service.impl.decorator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.constant.project.ProjectMQConstant;
import com.yihen.constant.project.ProjectRedisConstant;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.controller.vo.ProjectCreateRequestVO;
import com.yihen.entity.Project;
import com.yihen.mapper.ProjectMapper;
import com.yihen.service.ProjectService;
import com.yihen.util.LambdaFieldUtils;
import com.yihen.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Primary // 让业务默认注入到“带缓存的实现”
@Slf4j
public class ProjectServiceDecorator extends ServiceImpl<ProjectMapper, Project>  implements ProjectService {

    private final ProjectService projectService;            // 被装饰者
    private final RedisUtils redisUtils;
    private final RabbitTemplate rabbitTemplate ;

    public ProjectServiceDecorator(
            @Qualifier("projectServiceImpl") ProjectService projectService, RedisUtils redisUtils, RabbitTemplate rabbitTemplate) {
        this.projectService = projectService;
        this.redisUtils = redisUtils;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Project createProject(ProjectCreateRequestVO projectCreateRequestVO) {
        Project project = projectService.createProject(projectCreateRequestVO);

        // 发送ES同步消息
        rabbitTemplate.convertAndSend(ProjectMQConstant.PROJECT_INFO_EXCHANGE, ProjectMQConstant.PROJECT_INFO_ADD_KEY, project);
        return project;
    }

    @Override
    public ExtractionResultVO getPropertyById(Long id) {
        return projectService.getPropertyById(id);
    }

    @Override
    public void addEpisodeCount(Long projectId) {
        projectService.addEpisodeCount(projectId);
        // 更新对应项目信息
        redisUtils.hIncrement(ProjectRedisConstant.PROJECT_INFO_KEY + projectId, LambdaFieldUtils.resolveFieldName(Project::getChapterCount),1);
    }

    @Override
    public void reduceEpisodeCount(Long projectId) {
        projectService.reduceEpisodeCount(projectId);
        // 更新对应项目信息
        redisUtils.hSubtract(ProjectRedisConstant.PROJECT_INFO_KEY + projectId, LambdaFieldUtils.resolveFieldName(Project::getChapterCount),1);
    }

    @Override
    public Long getProjectStyleById(Long id) {

        // 1. 查询缓存，是否存在对应数据
        Long styleId =(Long) redisUtils.get(ProjectRedisConstant.PROJECT_STYLE_KEY + id);
        if (ObjectUtils.isEmpty(styleId)) {
            // 2. 缓存为空，查询数据库
            styleId = projectService.getProjectStyleById(id);
            // 3. 添加缓存
            redisUtils.set(ProjectRedisConstant.PROJECT_STYLE_KEY + id, styleId,1 , TimeUnit.DAYS);
        }
        return styleId;
    }

    @Override
    public void deleteProject(Long id) {
        projectService.deleteProject(id);

        // 删除对应项目缓存
        // 1. 删除对应关联资产
        redisUtils.delete(ProjectRedisConstant.PROJECT_PROPERTY_KEY + id);
        // 2. 删除对应项目信息
        redisUtils.delete(ProjectRedisConstant.PROJECT_INFO_KEY+id);

        // 同步ES
        rabbitTemplate.convertAndSend(ProjectMQConstant.PROJECT_INFO_EXCHANGE, ProjectMQConstant.PROJECT_INFO_DELETE_KEY, id);
    }

    @Override
    public Project getProjectById(Long id) {
        // 1. 查询缓存，是否存在对应数据
        Project project = redisUtils.getHash(ProjectRedisConstant.PROJECT_INFO_KEY + id,Project.class);
        if (ObjectUtils.isEmpty(project)) {
            // 2. 缓存为空，查询数据库
            project = projectService.getProjectById(id);
            // 3. 添加缓存
            redisUtils.putHash(ProjectRedisConstant.PROJECT_INFO_KEY + id, project,1 , TimeUnit.DAYS);
        }

        return project;
    }

    @Override
    public void updateProjectById(Project project) {
        projectService.updateProjectById(project);
        // 更新对应缓存
        redisUtils.updateHashPartial(ProjectRedisConstant.PROJECT_INFO_KEY + project.getId(), project);

        // 发送同步ES消息
        rabbitTemplate.convertAndSend(ProjectMQConstant.PROJECT_INFO_EXCHANGE, ProjectMQConstant.PROJECT_INFO_UPDATE_KEY, project);

    }

    @Override
    public List<Project> getProject(Page<Project> projectPage) {
        // 1. 查询缓存，是否存在对应数据
        // 1.1 构建分页参数
        long page = projectPage.getCurrent();
        long size = projectPage.getSize();
        long start = (page - 1) * size;
        long end = start + size - 1;

        List<Project> projects =redisUtils.lRange(ProjectRedisConstant.PROJECT_KEY, start, end, Project.class);
        Long total = redisUtils.lSize(ProjectRedisConstant.PROJECT_KEY);
        projectPage.setRecords(projects);
        projectPage.setTotal(total);


        if (ObjectUtils.isEmpty(projects)) {
            // 2. 缓存为空，查询数据库
            projects = projectService.getProject(projectPage);

            // 发送加载Redis消息
            rabbitTemplate.convertAndSend(ProjectMQConstant.PROJECT_INFO_EXCHANGE, ProjectMQConstant.PROJECT_INFO_LOAD_KEY,1);

        }
        return projects;

    }
}
