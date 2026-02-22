package com.yihen.search.doc;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;
import java.util.Map;

/**
 * SceneDoc：场景搜索文档
 *
 * 设计目标：
 * 1）支持场景名称搜索（中文 + 拼音）
 * 2）支持按 projectId / episodeId 过滤
 * 3）支持按创建时间排序
 */
@Data
@Document(indexName = "scene")
@Setting(settingPath = "/es/scene-settings.json")
@Mapping(mappingPath = "/es/scene-mapping.json")
public class SceneDoc {

    /**
     * 文档ID
     * 直接使用 MySQL scene.id
     */
    @Id
    private Long id;

    /**
     * 所属项目ID（过滤用）
     */
    @Field(type = FieldType.Long)
    private Long projectId;

    /**
     * 所属章节ID（过滤用）
     */
    @Field(type = FieldType.Long)
    private Long episodeId;

    /**
     * 场景名称
     *
     * 支持：
     * - 中文全文检索
     * - 拼音搜索
     * - 精确匹配
     */
    @MultiField(
            mainField = @Field(type = FieldType.Text,
                    analyzer = "ik_max_word",
                    searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "kw",
                            type = FieldType.Keyword,
                            ignoreAbove = 256),

                    @InnerField(suffix = "pinyin",
                            type = FieldType.Text,
                            analyzer = "pinyin_analyzer")
            }
    )
    private String name;

    /**
     * 场景描述
     */
    @Field(type = FieldType.Text,
            analyzer = "ik_max_word",
            searchAnalyzer = "ik_smart")
    private String description;

    /**
     * 场景缩略图URL
     * 仅展示，不参与搜索
     */
    @Field(type = FieldType.Keyword, index = false)
    private String thumbnail;


    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date)
    private Date updateTime;
}
