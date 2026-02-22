package com.yihen.search.mapper;

import com.yihen.entity.Characters;
import com.yihen.entity.Scene;
import com.yihen.search.doc.CharactersDoc;
import com.yihen.search.doc.SceneDoc;


public class SceneDocMapper {

    private SceneDocMapper() {}

    public static SceneDoc toDoc(Scene scene) {
        if (scene == null) return null;

        SceneDoc doc = new SceneDoc();
        doc.setId(scene.getId());
        doc.setEpisodeId(scene.getEpisodeId());
        doc.setProjectId(scene.getProjectId());
        doc.setName(scene.getName());
        doc.setDescription(scene.getDescription());
        doc.setThumbnail(scene.getThumbnail());


        if (scene.getCreateTime() != null) doc.setCreateTime(scene.getCreateTime());
        if (scene.getUpdateTime() != null) doc.setUpdateTime(scene.getUpdateTime());

        return doc;
    }
}
