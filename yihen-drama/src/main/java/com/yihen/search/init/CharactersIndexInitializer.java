package com.yihen.search.init;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.yihen.entity.Characters;
import com.yihen.entity.Project;
import com.yihen.mapper.CharacterMapper;
import com.yihen.mapper.ProjectMapper;
import com.yihen.search.doc.CharactersDoc;
import com.yihen.search.doc.ProjectDoc;
import com.yihen.search.doc.SceneDoc;
import com.yihen.search.mapper.CharactersDocMapper;
import com.yihen.search.mapper.ProjectDocMapper;
import com.yihen.search.repository.CharactersSearchRepository;
import com.yihen.search.repository.ProjectSearchRepository;
import com.yihen.search.service.CharactersSearchService;
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
 * CharactersIndexInitializer：服务启动时确保 character 索引存在，并写入 mapping
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
public class CharactersIndexInitializer implements ApplicationRunner {

    @Autowired
    private  ElasticsearchOperations operations;
    @Autowired
    private CharacterMapper characterMapper;
    @Autowired
    private CharactersSearchRepository charactersSearchRepository;


    @Override
    public void run(ApplicationArguments args) {
        // IndexOperations 是 Spring Data ES 对索引管理的抽象
        IndexOperations indexOps = operations.indexOps(CharactersDoc.class);
        if (!indexOps.exists()) {
            // 1) 创建索引（settings + 空 mapping）
            indexOps.create();

            // 2) 根据 CharactersDoc 注解生成 mapping 并写入
            indexOps.putMapping(indexOps.createMapping(CharactersDoc.class));

            log.info("✅ Elasticsearch 索引 characters 创建完成（含 mapping）");



        } else {
            log.info("ℹ️ Elasticsearch 索引 characters 已存在，跳过创建");
        }

        // 没有数据则继续数据初始化
        long count = operations.count(
                NativeQuery.builder()
                        .withQuery(new Query.Builder().matchAll(m -> m).build())
                        .build(),
                CharactersDoc.class
        );

        if (count == 0) {
            // 3) 数据初始化
            dataInit();
        }

    }

    private void dataInit() {
        log.info("🚀 开始初始化 Character 搜索数据（MySQL -> ES）...");
        // 1) 查询 MySQL：只取未逻辑删除的数据
        List<Characters> characters = characterMapper.selectList(null );
        if (ObjectUtils.isEmpty(characters)) {
            log.info("ℹ️ MySQL 中没有可导入的 Characters 数据，跳过初始化");
            return;
        }
        // 2) 转为 ES Doc
        List<CharactersDoc> docs = characters.stream()
                .map(CharactersDocMapper::toDoc)
                .toList();

        // 3) 批量写入 ES（Spring Data 内部会走 bulk）
        charactersSearchRepository.saveAll(docs);

        log.info("✅ Characters 搜索数据初始化完成，共导入 {} 条", docs.size());
    }
}
