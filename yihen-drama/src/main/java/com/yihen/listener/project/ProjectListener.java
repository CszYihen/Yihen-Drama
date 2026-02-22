package com.yihen.listener.project;


import com.yihen.constant.project.ProjectMQConstant;
import com.yihen.constant.project.ProjectRedisConstant;
import com.yihen.entity.Project;
import com.yihen.init.ProjectCacheInitializer;
import com.yihen.search.mapper.ProjectDocMapper;
import com.yihen.search.repository.ProjectSearchRepository;
import com.yihen.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectListener {
    @Autowired
    private ProjectSearchRepository projectSearchRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ProjectCacheInitializer projectInitializer;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ProjectMQConstant.PROJECT_INFO_ADD_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ProjectMQConstant.PROJECT_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ProjectMQConstant.PROJECT_INFO_ADD_KEY
            )
    )
    public void addProject(Project project) {
        // ES同步
        projectSearchRepository.save(ProjectDocMapper.toDoc(project));
        // Redis同步
        redisUtils.lPush(ProjectRedisConstant.PROJECT_KEY, project);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ProjectMQConstant.PROJECT_INFO_UPDATE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ProjectMQConstant.PROJECT_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ProjectMQConstant.PROJECT_INFO_UPDATE_KEY
            )
    )
    public void updateProject(Project project) {
        // ES同步
        projectSearchRepository.save(ProjectDocMapper.toDoc(project));
        // Redis同步
        projectInitializer.run(null);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ProjectMQConstant.PROJECT_INFO_DELETE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ProjectMQConstant.PROJECT_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ProjectMQConstant.PROJECT_INFO_DELETE_KEY
            )
    )
    public void deleteProject(Long id) {
        // ES同步
        projectSearchRepository.deleteById(id);
        // Redis同步
        projectInitializer.run(null);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ProjectMQConstant.PROJECT_INFO_LOAD_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ProjectMQConstant.PROJECT_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ProjectMQConstant.PROJECT_INFO_LOAD_KEY
            )
    )
    public void loadRedis(int id) {
        // Redis同步
        projectInitializer.run(null);
    }



}
