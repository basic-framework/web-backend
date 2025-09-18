package com.zl.zllangchain4j.config;

import com.zl.zllangchain4j.aiservice.ConsultantService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
@Configuration
public class CommonConfig {
//    @Autowired
//    private OpenAiChatModel model;

//    @Bean
//    public ConsultantService consultantService() {
//        ConsultantService cs = AiServices.builder(ConsultantService.class)
//                .chatModel(model)//设置对话时使用的模型对象
//                .build();
//        return cs;
//    }
        @Bean
        public ChatMemory chatMemory() {
            return MessageWindowChatMemory.builder()
                    .maxMessages(20)//最大保存的会话记录数量
                    .build();
        }
    @Resource
    private ChatMemoryStore redisChatMemoryStore;
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)//id值
                        .maxMessages(20)//最大会话记录数量
                        .chatMemoryStore(redisChatMemoryStore)//配置ChatMemoryStore
                        .build();
            }
        };
        return chatMemoryProvider;
    }



//    @Bean
//    public EmbeddingStore store(){
//        //1.加载文档进内存
//        //加载文档的时候指定解析器
//        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content",new ApachePdfBoxDocumentParser());
//        //2.构建向量数据库操作对象  操作的是内存版本的向量数据库
////        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content");
////        List<Document> documents = FileSystemDocumentLoader.loadDocuments("C:\\Users\\Administrator\\ideaProjects\\consultant\\src\\main\\resources\\content");
//        //2.构建向量数据库操作对象  操作的是内存版本的向量数据库
//        InMemoryEmbeddingStore store = new InMemoryEmbeddingStore();
//        //3.构建一个EmbeddingStoreIngestor对象,完成文本数据切割,向量化, 存储
//        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
//                .embeddingStore(store)
//                .build();
//        ingestor.ingest(documents);
//        return store;
//    }
@Bean
public EmbeddingStore store(){
    //1.加载文档进内存
    //List<Document> documents = ClassPathDocumentLoader.loadDocuments("content");
    //加载文档的时候指定解析器
    List<Document> documents = ClassPathDocumentLoader.loadDocuments("content",new ApachePdfBoxDocumentParser());
    //2.构建向量数据库操作对象  操作的是内存版本的向量数据库
    InMemoryEmbeddingStore store = new InMemoryEmbeddingStore();
    //构建文档分割器对象
    DocumentSplitter ds = DocumentSplitters.recursive(500,100);
    //3.构建一个EmbeddingStoreIngestor对象,完成文本数据切割,向量化, 存储
    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .embeddingStore(store)
            .documentSplitter(ds)
            .build();
    ingestor.ingest(documents);
    return store;
}
    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore store){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)//设置向量数据库操作对象
                .minScore(0.5)//设置最小分数
                .maxResults(3)//设置最大片段数量
                .build();
    }
}