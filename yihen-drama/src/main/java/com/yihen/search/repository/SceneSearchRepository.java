package com.yihen.search.repository;

import com.yihen.search.doc.CharactersDoc;
import com.yihen.search.doc.SceneDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface SceneSearchRepository extends ElasticsearchRepository<SceneDoc,Long> {
}
