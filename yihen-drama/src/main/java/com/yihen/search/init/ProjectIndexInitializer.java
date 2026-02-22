package com.yihen.search.init;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.yihen.entity.Project;
import com.yihen.mapper.ProjectMapper;
import com.yihen.search.doc.ProjectDoc;
import com.yihen.search.doc.SceneDoc;
import com.yihen.search.mapper.ProjectDocMapper;
import com.yihen.search.repository.ProjectSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * ProjectIndexInitializer：服务启动时确保 project 索引存在，并写入 mapping
 *
 * 为什么需要：
 * - ES 不像 MySQL，会自动从实体创建表结构；需要提前创建索引/映射
 * - 避免“动态映射”导致字段类型漂移（尤其 date/long/text）
 *
 * 注意：
 * - 生产环境通常用“索引模板 + 别名 + 灰度重建”管理，这里先做最小可用版本
 */
@Slf4j
@Component
public class ProjectIndexInitializer implements ApplicationRunner {

    @Autowired
    private  ElasticsearchOperations operations;
    @Autowired
    private  ProjectMapper projectMapper;
    @Autowired
    private  ProjectSearchRepository projectSearchRepository;


    @Override
    public void run(ApplicationArguments args) {
        // IndexOperations 是 Spring Data ES 对索引管理的抽象
        IndexOperations indexOps = operations.indexOps(ProjectDoc.class);
        if (!indexOps.exists()) {
            // 1) 创建索引（settings + 空 mapping）
            indexOps.create();

            // 2) 根据 ProjectDoc 注解生成 mapping 并写入
            indexOps.putMapping(indexOps.createMapping(ProjectDoc.class));

            log.info("✅ Elasticsearch 索引 project 创建完成（含 mapping）");



        } else {
            log.info("ℹ️ Elasticsearch 索引 project 已存在，跳过创建");
        }


        // 没有数据则继续数据初始化
        long count = operations.count(
                NativeQuery.builder()
                        .withQuery(new Query.Builder().matchAll(m -> m).build())
                        .build(),
                ProjectDoc.class
        );

        if (count == 0) {
            // 3) 数据初始化
            dataInit();
        }

    }

    private void dataInit() {
        log.info("🚀 开始初始化 Project 搜索数据（MySQL -> ES）...");
        // 1) 查询 MySQL：只取未逻辑删除的数据
        List<Project> projects = projectMapper.selectList(null );
        if (ObjectUtils.isEmpty(projects)) {
            log.info("ℹ️ MySQL 中没有可导入的 Project 数据，跳过初始化");
        }
        // 2) 转为 ES Doc
        List<ProjectDoc> docs = projects.stream()
                .map(ProjectDocMapper::toDoc)
                .toList();

        // 3) 批量写入 ES（Spring Data 内部会走 bulk）
        projectSearchRepository.saveAll(docs);

        log.info("✅ Project 搜索数据初始化完成，共导入 {} 条", docs.size());
    }
}
