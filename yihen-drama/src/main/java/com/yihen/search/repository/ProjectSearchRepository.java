package com.yihen.search.repository;

import com.yihen.search.doc.ProjectDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ProjectSearchRepository：用于将 ProjectDoc 写入/删除 ES
 * ElasticsearchRepository<实体类型, 主键类型>
 * 适用场景：
 * - save(doc)：新增/更新（upsert）
 * - deleteById(id)：删除文档
 *
 * 复杂查询建议用 ElasticsearchOperations（下面会给）
 */
public interface ProjectSearchRepository extends ElasticsearchRepository<ProjectDoc,Long> {
}
