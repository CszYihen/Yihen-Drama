package com.yihen.search.mapper;

import com.yihen.entity.Project;
import com.yihen.search.doc.ProjectDoc;

import java.time.Instant;

/**
 * ProjectDocMapper：负责 MySQL 实体 <-> ES 文档的转换
 *
 * 好处：
 * - 代码集中，减少重复
 * - 字段变更时容易维护
 */
public class ProjectDocMapper {

    private ProjectDocMapper() {}

    public static ProjectDoc toDoc(Project p) {
        if (p == null) return null;

        ProjectDoc doc = new ProjectDoc();
        doc.setId(p.getId());
        doc.setName(p.getName());
        doc.setDescription(p.getDescription());
        doc.setCover(p.getCover());
        doc.setStyleId(p.getStyleId());
        doc.setStatus(p.getStatus() == null ? null : p.getStatus().getCode()); // 视你的枚举实现调整
        doc.setChapterCount(p.getChapterCount());
        doc.setProgress(p.getProgress());

        if (p.getCreateTime() != null) doc.setCreateTime(p.getCreateTime());
        if (p.getUpdateTime() != null) doc.setUpdateTime(p.getUpdateTime());

        return doc;
    }
}
