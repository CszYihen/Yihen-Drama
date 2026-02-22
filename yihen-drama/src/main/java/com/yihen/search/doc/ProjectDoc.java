package com.yihen.search.doc;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

/**
 * ProjectDoc：ES 中用于“项目搜索”的文档模型
 *
 * 设计原则：
 * 1) 只放“搜索需要的字段”，不要把 MySQL 所有列都搬过来，避免索引臃肿
 * 2) 可检索文本用 text（分词），可过滤/聚合字段用 keyword 或数值类型
 * 3) _id 直接使用 project.id，方便同步（upsert / delete）以及排查问题
 */
@Data
@Document(indexName = "project") // 索引名：project（建议后续用别名，这里先最小可用）
@Setting(settingPath = "/es/project-settings.json")
@Mapping(mappingPath = "/es/project-mapping.json")
public class ProjectDoc {
    /**
     * ES 文档的主键（_id）
     * 用 MySQL 的 project.id 保持一致，便于：
     * - 保存时覆盖更新（upsert）
     * - 删除时 deleteById
     */
    @Id
    private Long id;

    /**
     * 项目名称：需要支持中文检索、相关性排序
     *
     * MultiField：一个字段同时以两种方式存储：
     * - 主字段（text）：用于全文检索（分词）
     * - 子字段 name.kw（keyword）：用于精确匹配/聚合/排序（比如精确筛选名字）
     *
     * analyzer/searchAnalyzer：需要 IK 插件才可用
     * - analyzer（索引时）：ik_max_word 分词更细，有利于召回
     * - searchAnalyzer（查询时）：ik_smart 更“收敛”，减少噪声
     *
     * 如果你暂时没装 IK：把 analyzer/searchAnalyzer 改成 "standard" 即可先跑通
     */
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "kw", type = FieldType.Keyword, ignoreAbove = 256),
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "pinyin_analyzer")
            }
    )
    private String name;

    /**
     * 项目描述：全文检索字段
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word" , searchAnalyzer = "ik_smart")
    private String description;

    /**
     * 封面 URL：仅展示，不用于检索
     * index=false：不建立倒排索引，减少体积
     */
    @Field(type = FieldType.Keyword, index = false)
    private String cover;

    /**
     * 风格 ID：过滤字段（term 查询）
     * 使用 Long 类型，便于 termQuery("styleId", 2)
     */
    @Field(type = FieldType.Long)
    private Long styleId;

    /**
     * 项目状态：过滤字段
     * 0-草稿 1-处理中 2-已完成
     */
    @Field(type = FieldType.Integer)
    private Integer status;

    /**
     * 章节数量：可用于排序/过滤（一般不参与全文检索）
     */
    @Field(type = FieldType.Integer)
    private Integer chapterCount;

    /**
     * 总体进度百分比：可用于排序/过滤
     */
    @Field(type = FieldType.Integer)
    private Integer progress;

    /**
     * 创建/更新时间：排序、范围过滤
     * Instant 对应 ES date 类型（ISO8601）
     */
    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Date)
    private Date updateTime;
}
