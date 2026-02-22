package com.yihen.search.mapper;

import com.yihen.entity.Characters;
import com.yihen.entity.Project;
import com.yihen.search.doc.CharactersDoc;
import com.yihen.search.doc.ProjectDoc;


public class CharactersDocMapper {

    private CharactersDocMapper() {}

    public static CharactersDoc toDoc(Characters characters) {
        if (characters == null) return null;

        CharactersDoc doc = new CharactersDoc();
        doc.setId(characters.getId());
        doc.setEpisodeId(characters.getEpisodeId());
        doc.setProjectId(characters.getProjectId());
        doc.setName(characters.getName());
        doc.setDescription(characters.getDescription());
        doc.setAvatar(characters.getAvatar());
        doc.setVideoUrl(characters.getVideoUrl());
        doc.setFeatures(characters.getFeatures());


        if (characters.getCreateTime() != null) doc.setCreateTime(characters.getCreateTime());
        if (characters.getUpdateTime() != null) doc.setUpdateTime(characters.getUpdateTime());

        return doc;
    }
}
