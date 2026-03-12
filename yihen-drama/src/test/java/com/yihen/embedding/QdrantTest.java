package com.yihen.embedding;

import com.yihen.YihenDramaApplication;
import com.yihen.dto.NovelChunk;
import com.yihen.util.QdrantUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenizer;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.WithVectorsSelectorFactory;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = YihenDramaApplication.class,
        properties = {
                "app.websocket.enabled=false",
                "spring.main.lazy-initialization=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        })
public class QdrantTest {

    /**
     * 你的 Qdrant gRPC 端口。
     * 注意：
     * 6333 是 HTTP REST
     * 6334 是 gRPC
     *
     * LangChain4j 的 QdrantEmbeddingStore 这里走的是 gRPC，所以要用 6334。
     */
    private static final String HOST = "localhost";
    private static final int PORT = 6334;

    /**
     * 你的小说分片集合名。
     * 后续真实项目里也建议沿用这个名字。
     */
    private static final String COLLECTION_NAME = "novel_chunks_2";

    /**
     * 官方示例使用的是 AllMiniLmL6V2EmbeddingModel。
     * 这个模型输出维度是 384，所以 collection 的 size 也必须是 384。
     */
    private static final int DIMENSION = 384;

    /**
     * 相似度计算方式，和官方示例一样使用 Cosine。
     */
    private static final Collections.Distance DISTANCE = Collections.Distance.Cosine;

    @Test
    void test_store_and_search_novel_chunks() throws Exception {

        // 1. 原生 Qdrant 客户端：负责创建 / 删除 collection
        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(HOST, PORT, false).build()
        );

        // 2. 每次测试前，先删除旧 collection，避免历史脏数据影响
        try {
            if (client.collectionExistsAsync(COLLECTION_NAME).get()) {
                client.deleteCollectionAsync(COLLECTION_NAME).get();
                System.out.println("已删除旧 collection: " + COLLECTION_NAME);
            }
        } catch (Exception e) {
            System.out.println("删除旧 collection 时忽略异常: " + e.getMessage());
        }

        // 3. 创建新的 collection，维度固定为 384
        client.createCollectionAsync(
                COLLECTION_NAME,
                Collections.VectorParams.newBuilder()
                        .setDistance(Collections.Distance.Cosine)
                        .setSize(DIMENSION)
                        .build()
        ).get();

        System.out.println("已创建新 collection: " + COLLECTION_NAME);

        // 4. LangChain4j 的 QdrantEmbeddingStore：负责 add / search
        EmbeddingStore<TextSegment> embeddingStore = QdrantEmbeddingStore.builder()
                .host(HOST)
                .port(PORT)
                .collectionName(COLLECTION_NAME)
                .build();

        // 5. 使用官方示例同款本地 embedding 模型
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

        // 6. 模拟你的小说分片
        TextSegment chunk1 = TextSegment.from("雨下得很大，林晚抱着书包冲进便利店，第一次见到了顾川。");
        TextSegment chunk2 = TextSegment.from("顾川把伞递给她，但林晚误以为他是在故意嘲讽自己。");
        TextSegment chunk3 = TextSegment.from("另一边，反派正在安排手下调查顾川的家庭背景。");

        // 7. 对分片进行向量化
        Embedding embedding1 = embeddingModel.embed(chunk1).content();
        Embedding embedding2 = embeddingModel.embed(chunk2).content();
        Embedding embedding3 = embeddingModel.embed(chunk3).content();

        System.out.println("embedding1 dimension = " + embedding1.vector().length);
        System.out.println("embedding2 dimension = " + embedding2.vector().length);
        System.out.println("embedding3 dimension = " + embedding3.vector().length);

        // 8. 入库
        embeddingStore.add(embedding1, chunk1);
        embeddingStore.add(embedding2, chunk2);
        embeddingStore.add(embedding3, chunk3);

        System.out.println("小说分片向量已存入 Qdrant");

        // 9. 模拟分镜生成时的查询
        String queryText = "男女主初次相遇并发生误会的剧情";

        Embedding queryEmbedding = embeddingModel.embed(queryText).content();

        System.out.println("queryEmbedding dimension = " + queryEmbedding.vector().length);

        // 10. 检索
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)
                .build();

        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(request).matches();

        // 11. 打印结果
        System.out.println("===== 检索结果 =====");
        for (EmbeddingMatch<TextSegment> match : matches) {
            System.out.println("score = " + match.score());
            System.out.println("text  = " + match.embedded().text());
            System.out.println("--------------------");
        }

        Assertions.assertFalse(matches.isEmpty(), "检索结果不能为空");
    }

    @Test
    void testDocumentSegmentWithQdrant() throws ExecutionException, InterruptedException {

        Document document = FileSystemDocumentLoader.loadDocument(
                "D:\\IdeaProject\\Yihen-Drama\\README.md"
        );

        DocumentByParagraphSplitter documentSplitter = new DocumentByParagraphSplitter(
                100,
                30,
                new HuggingFaceTokenizer()
        );

        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel(); // 你的向量模型

        // 1. 原生 Qdrant 客户端：负责创建 / 删除 collection
        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(HOST, PORT, false).build()
        );

        if (!client.collectionExistsAsync("knowledge_store").get()) {
            // 3. 创建新的 collection，维度固定为 384
            client.createCollectionAsync(
                    "knowledge_store",
                    Collections.VectorParams.newBuilder()
                            .setDistance(Collections.Distance.Cosine)
                            .setSize(DIMENSION)
                            .build()
            ).get();
        }



        EmbeddingStore<TextSegment> embeddingStore = QdrantEmbeddingStore.builder()
                .host("localhost")
                .port(6334) // gRPC端口，常见是6334；HTTP通常是6333
                .collectionName("knowledge_store")
                .build();

        EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(documentSplitter)
                .build()
                .ingest(document);
    }

    @Test
    void testSearchWithQdrant() {

        EmbeddingStore<TextSegment> embeddingStore = QdrantEmbeddingStore.builder()
                .host("localhost")
                .port(6334) // gRPC端口，常见是6334；HTTP通常是6333
                .collectionName("knowledge_store")
                .build();

        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever
                .builder()
                // 设置用于生成嵌入向量的嵌入模型
                .embeddingModel(new BgeSmallEnV15QuantizedEmbeddingModel())
                // 指定要使用的嵌入存储
                .embeddingStore(embeddingStore)
                // 设置最大检索结果,这里表示最多返回1条匹配结果
                .maxResults(3)
                // 设置最小得分阈值,只有得分>=0.8的结果才会被返回
                .minScore(0.8)
                // 构建最终的EmbeddingStoreContentRetriever 实例
                .build();
        // 查询内容
        String question = "子模块文档在哪里";
        List<Content> contents = retriever.retrieve(Query.from(question));

        // 输出结果
        if (contents == null || contents.isEmpty()) {
            System.out.println("没有检索到相关内容");
            return;
        }

        for (int i = 0; i < contents.size(); i++) {
            Content content = contents.get(i);
            System.out.println("=== 结果 " + (i + 1) + " ===");
            System.out.println(content.textSegment().text());
            System.out.println("metadata = " + content.textSegment().metadata());
        }

    }


    @Autowired
    QdrantUtils qdrantUtils;

    @Test
    public void testQdrantUtilsIngest() {
        String s = """
                一个老旧的钨丝灯被黑色的电线悬在屋子中央，闪烁着昏暗的光芒。
                　　静谧的气氛犹如墨汁滴入清水，正在房间内晕染蔓延。
                　　房间的正中央放着一张大圆桌，看起来已经斑驳不堪，桌子中央立着一尊小小的座钟，花纹十分繁复，此刻正滴答作响。
                　　而围绕桌子一周，坐着十个衣着各异的人，他们的衣服看起来有些破旧，面庞也沾染了不少灰尘。
                　　他们有的趴在桌面上，有的仰坐在椅子上，都沉沉的睡着。
                　　在这十人的身边，静静地站着一个戴着山羊头面具、身穿黑色西服的男人。
                　　他的目光从破旧的山羊头面具里穿出，饶有兴趣的盯着十个人。
                　　桌上的座钟响了起来，分针与时针同时指向了「十二」。
                　　房间之外很遥远的地方，传来了低沉的钟声。
                　　同一时刻，围坐在圆桌旁边的十个男男女女慢慢苏醒了。
                　　他们逐渐清醒之后，先是迷惘的看了看四周，又疑惑的看了看对方。
                　　看来谁都不记得自己为何出现在此处。
                　　“早安，九位。”山羊头率先说话了，“很高兴能在此与你们见面，你们已经在我面前沉睡了十二个小时了。”
                　　眼前这个男人的装扮实在是诡异，在昏暗的灯光下吓了众人一跳。
                　　他的面具仿佛是用真正的山羊头做成的，很多毛发已经发黄变黑，打结粘在了一起。
                　　山羊面具的眼睛处挖了两个空洞，露出了他那狡黠的双眼。
                　　他的举手投足之间不仅散发着山羊身上独有的膻腥味，更有一股隐隐的腐烂气息。
                　　一个纹着花臂的男人愣了几秒，才终于发现这件事情的不合理之处，带着犹豫开口问道山羊头：“你……是谁？”
                　　“相信你们都有这个疑问，那我就跟九位介绍一下。”山羊头高兴的挥舞起双手，看起来他早就准备好答案了。
                　　一位名叫齐夏的年轻人坐在距离山羊头最远的地方，他迅速打量了一下屋内的情况，片刻之后，神色就凝重了起来。
                　　奇怪，这个房间真是太奇怪了。
                　　这里没有门，四面都是墙。
                　　换句话说，这个屋子四周、屋顶和地板都是封闭的，偏偏在屋中央放着一张桌子。
                　　既然如此，他们是怎么来到这里的？
                　　难不成是先把人送过来，而后再砌成的墙吗？
                　　齐夏又看了看四周，这里不管是地板、墙面还是天花板，统统都有横竖交错的线条，这些线条将墙体和地面分成了许多大方格。
                　　另外让齐夏在意的一点，是那个山羊头口中所说的「九位」。
                　　坐在圆桌四周的无论怎么数都是十个人，加上山羊头自己，这屋里一共有十一个人。
                　　「九位」是什么意思？
                　　他伸手摸了摸自己的口袋，不出所料，手机早就被收走了。
                　　“不必跟我们介绍了。”一个清冷的女人开口对山羊头说道，“我劝你早点停止自己的行为，我怀疑你拘禁我们已经超过了二十四个小时，构成了「非法拘禁罪」，你现在所说的每一句话都会被记录下来，会形成对你不利的证词。”
                　　她一边说着话，一边嫌弃的搓弄着手臂上的灰尘，仿佛对于被囚禁来说，她更讨厌被弄脏。
                　　清冷女人的一番话确实让众人清醒不少，无论对方是谁，居然敢一个人绑架十个人，不论如何都已经触犯法律的底线了。
                　　“等等……”一个穿着白大褂的中年男人打断了众人的思路，他缓缓的看向那个清冷女人，开口问道，“我们都刚刚才醒过来，你怎么知道我们被囚禁了「二十四个小时」？”
                """;

        NovelChunk novelChunk = new NovelChunk();
        novelChunk.setName("第一章");
        novelChunk.setEpisodeId(1L);
        novelChunk.setProjectId(1L);
        novelChunk.setChapterNumber(1);

        qdrantUtils.ingest(s,novelChunk);
    }

    @Test
    public void testQdrantUtilsSearch() {
        String query = "白大褂中年男人说了什么";
        NovelChunk novelChunk = new NovelChunk();
        novelChunk.setEpisodeId(1L);
        List<String> search = qdrantUtils.search(query, novelChunk);
        search.forEach(System.out::println);
    }
}
