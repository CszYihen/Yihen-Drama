package com.yihen.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihen.controller.vo.CharacterSuggestItem;
import com.yihen.controller.vo.SceneSuggestItem;
import com.yihen.search.doc.CharactersDoc;
import com.yihen.search.doc.SceneDoc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SceneSearchService {

    @Autowired
    private  ElasticsearchOperations operations;


    /**
     * 项目内场景搜索
     *
     * 业务含义：
     * - projectId：必须传，强制过滤在某个项目内
     * - keyword：可选；为空则返回该项目下的全部角色（分页）
     * - page：MyBatis-Plus 的 Page，用来承载 records/total
     *
     * @param projectId 项目ID（必填）
     * @param keyword   关键词（可选：对 name/description 做全文检索 + 拼音）
     * @param page      分页对象（page.current 从 1 开始）
     * @return 当前页 records
     */
    public List<SceneDoc> searchInProject(Long projectId, String keyword, Page<SceneDoc> page) {

        // 0️⃣ projectId 必须存在，否则没意义（也避免误查全库）
        if (ObjectUtils.isEmpty(projectId)) {
            page.setTotal(0);
            page.setRecords(List.of());
            return List.of();
        }

        // 1️⃣ 构建 BoolQuery：must（关键词） + filter（项目过滤）
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 1.1 filter：项目内过滤（不参与打分，性能更稳定）
        boolBuilder.filter(f -> f.term(t -> t.field("projectId").value(projectId)));

        // 1.2 must：关键词搜索（可选）
        if (!ObjectUtils.isEmpty(keyword)) {

            /*
             * multi_match + bool_prefix：
             * - 更像“输入中”的体验（也可用于搜索框实时提示）
             * - 同时查 name / description / name.pinyin
             *
             * name^3：提高名称权重
             */
            boolBuilder.must(m -> m.multiMatch(mm -> mm
                    .query(keyword)
                    .type(TextQueryType.BoolPrefix)
                    .fields("name^3", "description", "name.pinyin")
            ));

        } else {
            // 没有关键词：match_all（但仍然会被 projectId filter 限制住）
            boolBuilder.must(m -> m.matchAll(ma -> ma));
        }

        // 2️⃣ 拼成最终 Query DSL
        Query queryDsl = new Query.Builder()
                .bool(boolBuilder.build())
                .build();

        // 3️⃣ 分页参数
        int size = (int) page.getSize();
        int current = (int) page.getCurrent(); // MP current 从 1 开始

        PageRequest pageable = PageRequest.of(Math.max(current - 1, 0), size);

        // 4️⃣ NativeQuery：把 DSL + 分页组装为 ES 查询
        NativeQuery query = NativeQuery.builder()
                .withQuery(queryDsl)
                .withPageable(pageable)
                .build();

        // 5️⃣ 执行查询
        SearchHits<SceneDoc> hits = operations.search(query, SceneDoc.class);

        // 6️⃣ total：命中总数
        long total = hits.getTotalHits();

        // 7️⃣ records：当前页数据
        List<SceneDoc> records = hits.getSearchHits()
                .stream()
                .map(h -> h.getContent())
                .toList();

        // 8️⃣ 回填分页对象（MP Page）
        page.setTotal(total);
        page.setRecords(records);

        return records;
    }

    public List<SceneSuggestItem> suggestInProject(Long projectId, String prefix, int size) {

        if (ObjectUtils.isEmpty(projectId)|| ObjectUtils.isEmpty(prefix) ) {
            return List.of();
        }

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        boolBuilder.filter(f -> f.term(t -> t.field("projectId").value(projectId)));
        boolBuilder.must(m -> m.multiMatch(mm -> mm
                .query(prefix)
                .type(TextQueryType.BoolPrefix)
                .fields("name^3", "name.pinyin", "description")
        ));

        Query queryDsl = new Query.Builder().bool(boolBuilder.build()).build();

        NativeQuery query = NativeQuery.builder()
                .withQuery(queryDsl)
                .withMaxResults(Math.max(size, 1))
                .build();

        SearchHits<SceneDoc> hits = operations.search(query, SceneDoc.class);

        // 轻量返回 + 去重（按 id 去重更靠谱）
        Set<Long> seen = new LinkedHashSet<>();
        List<SceneSuggestItem> list = hits.getSearchHits().stream()
                .map(h -> h.getContent())
                .filter(doc -> doc != null && doc.getId() != null && StringUtils.hasText(doc.getName()))
                .filter(doc -> seen.add(doc.getId()))
                .map(doc -> {
                    SceneSuggestItem item = new SceneSuggestItem();
                    item.setId(doc.getId());
                    item.setName(doc.getName());
                    item.setThumbnail(doc.getThumbnail());
                    return item;
                })
                .toList();

        return list;
    }

}
