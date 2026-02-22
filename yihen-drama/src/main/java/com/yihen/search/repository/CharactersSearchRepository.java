package com.yihen.search.repository;

import com.yihen.search.doc.CharactersDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface CharactersSearchRepository extends ElasticsearchRepository<CharactersDoc,Long> {
}
