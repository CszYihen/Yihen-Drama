package com.yihen.init;

import com.yihen.constant.project.ProjectRedisConstant;
import com.yihen.entity.Project;
import com.yihen.mapper.ProjectMapper;
import com.yihen.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProjectCacheInitializer implements ApplicationRunner {

    private final ProjectMapper projectMapper;
    private final RedisUtils redisUtils;

    public ProjectCacheInitializer(ProjectMapper projectMapper,
                                   RedisUtils redisUtils) {
        this.projectMapper = projectMapper;
        this.redisUtils = redisUtils;
    }

    @Override
    public void run(ApplicationArguments args) {

        log.info("开始预热项目缓存...");

        // 1 先清空旧缓存（可选）
        redisUtils.delete(ProjectRedisConstant.PROJECT_KEY);
        // 2. 查询项目信息
        List<Project> projects = projectMapper.selectList(null);


        //2  批量写入 Redis
        redisUtils.lPushAll(
                ProjectRedisConstant.PROJECT_KEY,
                projects
        );

            log.info("项目-缓存预热完成，共加载 {} 条数据", projects.size());

        }

    }



