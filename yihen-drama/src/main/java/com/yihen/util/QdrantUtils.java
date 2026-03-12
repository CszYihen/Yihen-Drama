package com.yihen.util;

import ai.djl.modality.nlp.embedding.TextEmbedding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihen.config.properties.QdrantProperties;
import com.yihen.dto.NovelChunk;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.logical.And;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.cms.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
@Slf4j
public class QdrantUtils {

    @Autowired
    private ObjectMapper objectMapper ;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private DocumentByParagraphSplitter documentSplitter;

    @Autowired
    private QdrantProperties qdrantProperties;

    public  void ingest(String text , NovelChunk novelChunk) {

        // 包装元数据
        Map<String,Object> map = objectMapper.convertValue(novelChunk, Map.class);
        Metadata metadata = Metadata.from(map);


        Document document = Document.from(text, metadata);
        EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(documentSplitter)
                .build()
                .ingest(document);
    }

    public List<String> search(String query, NovelChunk novelChunk) {



        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever
                .builder()
                // 设置用于生成嵌入向量的嵌入模型
                .embeddingModel(embeddingModel)
                // 指定要使用的嵌入存储
                .embeddingStore(embeddingStore)
                // 设置最大检索结果,这里表示最多返回1条匹配结果
                .maxResults(qdrantProperties.getMaxResult())
                // 设置最小得分阈值,只有得分>=0.8的结果才会被返回
                .minScore(qdrantProperties.getMinScore())
                .filter(new IsEqualTo("episodeId",novelChunk.getEpisodeId())) // 暂时只根据章节Id过滤
                // 构建最终的EmbeddingStoreContentRetriever 实例
                .build();

        // 查询内容
        List<Content> contents = retriever.retrieve(
                Query.from(query)
        );

        // 输出结果
        if (contents == null || contents.isEmpty()) {
            log.info("没有检索到相关内容");
            return List.of();
        }

        // 包装输出
        List<String> result = contents.stream()
                .map(content -> content.textSegment().text())
                .toList();


        return result;

    }



}
