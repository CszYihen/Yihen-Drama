package com.yihen.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihen.search.doc.ProjectDoc;
import lombok.extern.slf4j.Slf4j;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * ProjectSearchService：项目搜索（基于 Elasticsearch）
 *
 * 这个类负责：
 * 1. 构建 ES 查询 DSL
 * 2. 执行查询
 * 3. 处理分页
 * 4. 返回总条数 + 当前页数据
 *
 * 当前支持：
 * - keyword：全文搜索（name + description）
 * - status：状态过滤
 * - styleId：风格过滤
 * - page/size：分页
 */
@Slf4j
@Service
public class ProjectSearchService {

    /**
     * ElasticsearchOperations：
     * Spring Data Elasticsearch 提供的核心操作类
     *
     * 作用：
     * - 执行查询
     * - 保存文档
     * - 删除文档
     */
    @Autowired
    private  ElasticsearchOperations operations;


    /**
     * 搜索方法
     *
     * @param keyword 关键词（全文检索）
     * @param status  状态过滤（可选）
     * @param styleId 风格过滤（可选）
     */
    public List<ProjectDoc> search(String keyword,
                                   Integer status,
                                   Long styleId,
                                   Page<ProjectDoc> projectDocPage) {

        // ==============================
        // 1️⃣ 构建 bool 查询
        // ==============================

        /*
         * BoolQuery 是 ES 最常用的复合查询结构
         *
         * 它包含：
         * must   → 必须满足（参与评分）
         * filter → 过滤条件（不参与评分，性能更好）
         * should → 可选条件
         *
         * 这里我们使用：
         * must   → 关键词搜索
         * filter → status / styleId 精确过滤
         */
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();


        // ==============================
        // 2️⃣ 处理关键词搜索
        // ==============================

        if (!ObjectUtils.isEmpty(keyword)) {

            /*
             * multi_match：
             * 同时在多个字段中搜索
             *
             * fields("name^3", "description")
             *
             * name^3 表示：
             * name 字段权重为 3
             * 即：如果项目名匹配，分数更高
             */
            boolBuilder.must(m -> m
                    .multiMatch(mm -> mm
                            .query(keyword)              // 搜索内容
                            .fields("name^3", "description")  // 搜索字段
                    )
            );

        } else {

            /*
             * 如果没有关键词
             * 使用 match_all 查询
             *
             * 相当于：
             * 查询全部数据（后面再用 filter 限制）
             */
            boolBuilder.must(m -> m.matchAll(ma -> ma));
        }


        // ==============================
        // 3️⃣ 添加过滤条件（不参与打分）
        // ==============================

        if (status != null) {
            /*
             * term 查询：
             * 精确匹配
             *
             * 适用于：
             * - 数字
             * - keyword 类型
             *
             * 不适用于 text 类型
             */
            boolBuilder.filter(f ->
                    f.term(t -> t.field("status").value(status))
            );
        }

        if (styleId != null) {
            boolBuilder.filter(f ->
                    f.term(t -> t.field("styleId").value(styleId))
            );
        }


        // ==============================
        // 4️⃣ 把 BoolQuery 封装成 ES Query
        // ==============================

        Query queryDsl = new Query.Builder()
                .bool(boolBuilder.build())
                .build();


        // ==============================
        // 5️⃣ 构建分页参数
        // ==============================

        /*
         * Spring 的 PageRequest 页码是从 0 开始
         * 但我们前端 page 是从 1 开始
         */
        int size =(int) projectDocPage.getSize();
        int pages =(int) projectDocPage.getCurrent();
        PageRequest pageable = PageRequest.of(
                Math.max(pages- 1, 0),
                size
        );


        // ==============================
        // 6️⃣ 构建 NativeQuery（最终发送给 ES 的查询）
        // ==============================

        NativeQuery query = NativeQuery.builder()
                .withQuery(queryDsl)     // 设置 DSL
                .withPageable(pageable)  // 设置分页
                .build();


        // ==============================
        // 7️⃣ 执行查询
        // ==============================

        SearchHits<ProjectDoc> hits =
                operations.search(query, ProjectDoc.class);


        // ==============================
        // 8️⃣ 获取总条数
        // ==============================

        long total = hits.getTotalHits();


        // ==============================
        // 9️⃣ 获取当前页数据
        // ==============================

        List<ProjectDoc> records = hits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent()) // 取文档内容
                .toList();

        projectDocPage.setRecords(records);
        projectDocPage.setTotal(total);
        // ==============================
        // 🔟 封装返回结果
        // ==============================

        return records;
    }


    /**
     * 自动补全（建议）
     *
     * 典型使用方式：
     * - 用户在搜索框输入 1 个字/拼音（如：十 / sh / sr）
     * - 后端返回 5~10 条候选项目（通常只需要 id + name + cover）
     *
     * 设计要点：
     * 1) 用 multi_match + bool_prefix：更贴近“输入中”的前缀匹配体验
     * 2) 同时匹配 name（中文分词）和 name.pinyin（拼音分词）
     * 3) 控制返回字段和 size，避免补全接口太重
     * 4) 可选：status/styleId 过滤（你要也可以加参数）
     *
     * @param prefix 用户输入内容（建议最少 1 个字符就发请求）
     * @param size   返回条数（建议 5~20）
     */
    public List<String> suggest(String prefix, int size) {

        // 0) 入参兜底：prefix 为空直接返回空列表（避免 match_all 造成全表）
        if (prefix == null || prefix.trim().isEmpty()) {
            return List.of();
        }

        // 1) 构建 bool 查询
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        /*
         * 2) must：multi_match + bool_prefix
         *
         * bool_prefix 特点：
         * - 对用户“正在输入”的前缀更友好
         * - 适合自动补全
         *
         * fields：
         * - name^3：项目名权重高
         * - name.pinyin：支持拼音/首字母（前提是你的 pinyin_analyzer 配好了）
         *
         * 注意：如果你没有 search_as_you_type 字段，用 bool_prefix 也能跑，
         * 但体验会略弱于 search_as_you_type。
         */
        boolBuilder.must(m -> m.multiMatch(mm -> mm
                .query(prefix)
                .type(TextQueryType.BoolPrefix)       // 关键：让查询更像“前缀输入”
                .fields("name^3", "name.pinyin", "description") // description 可选
        ));

        // 3) 只返回前 size 条
        NativeQuery query = NativeQuery.builder()
                .withQuery(new Query.Builder().bool(boolBuilder.build()).build())
                .withMaxResults(Math.max(size, 1))
                .build();

        // 4) 执行查询
        SearchHits<ProjectDoc> hits = operations.search(query, ProjectDoc.class);

        // 5) 取结果（去重：有时不同字段命中会导致 name 重复）
        Set<ProjectDoc> dedup = new LinkedHashSet<>();
        hits.getSearchHits().forEach(h -> dedup.add(h.getContent()));
        List<String> suggestions = dedup.stream().map(ProjectDoc::getName)
                .distinct()
                .toList();
        return suggestions;
    }
}
