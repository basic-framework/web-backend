package com.zl.web.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zl.common.constant.ChatConstant;
import com.zl.web.manager.springai.handler.AlibabaOpenAiChatModel;
import com.zl.web.manager.springai.handler.RedisChatMemory;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreAutoConfiguration;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import redis.clients.jedis.JedisPooled;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * SpringAI配置类
 * @Author GuihaoLv
 */
@Configuration
// 禁用SpringAI提供的RedisStack向量数据库的自动配置，会和Redis的配置冲突。
@EnableAutoConfiguration(exclude = {RedisVectorStoreAutoConfiguration.class})
// 读取RedisStack的配置信息
@EnableConfigurationProperties({RedisVectorStoreProperties.class})
public class SpringAIConfig {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 添加Java 8日期时间模块支持
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public ChatMemory chatMemory() {
       return new RedisChatMemory(stringRedisTemplate, objectMapper());
    }



//    @Bean
//    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
//        return SimpleVectorStore.builder(embeddingModel).build();
//    }

    /**
     * 创建RedisStack向量数据库
     *
     * @param embeddingModel 嵌入模型
     * @param properties     redis-stack的配置信息
     * @return vectorStore 向量数据库
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel,
                                   RedisVectorStoreProperties properties,
                                   RedisConnectionDetails redisConnectionDetails) {
        JedisPooled jedisPooled = new JedisPooled(redisConnectionDetails.getStandalone().getHost(),
                redisConnectionDetails.getStandalone().getPort()
                , redisConnectionDetails.getUsername(),
                redisConnectionDetails.getPassword());
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(properties.getIndex())
                .prefix(properties.getPrefix())
                .initializeSchema(properties.isInitializeSchema())
                .build();
    }



    @Bean
    public ChatClient chatSenceClient(OpenAiChatModel model, ChatMemory chatMemory,VectorStore vectorStore) {
        return ChatClient
                .builder(model)
                .defaultOptions(ChatOptions.builder().model("qwen-omni-turbo").build())
                .defaultSystem(ChatConstant.CHAT_ROLE)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(
                                vectorStore,
                                SearchRequest.builder()
                                        .similarityThreshold(0.6)
                                        .topK(2)
                                        .build()
                        )
                )
                .build();
    }



     //引入此模型解决OpenAI规范不兼容的问题
     @Bean
     public AlibabaOpenAiChatModel alibabaOpenAiChatModel(OpenAiConnectionProperties commonProperties, OpenAiChatProperties chatProperties, ObjectProvider<RestClient.Builder> restClientBuilderProvider, ObjectProvider<WebClient.Builder> webClientBuilderProvider, ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler, ObjectProvider<ObservationRegistry> observationRegistry, ObjectProvider<ChatModelObservationConvention> observationConvention) {
         String baseUrl = StringUtils.hasText(chatProperties.getBaseUrl()) ? chatProperties.getBaseUrl() : commonProperties.getBaseUrl();
         String apiKey = StringUtils.hasText(chatProperties.getApiKey()) ? chatProperties.getApiKey() : commonProperties.getApiKey();
         String projectId = StringUtils.hasText(chatProperties.getProjectId()) ? chatProperties.getProjectId() : commonProperties.getProjectId();
         String organizationId = StringUtils.hasText(chatProperties.getOrganizationId()) ? chatProperties.getOrganizationId() : commonProperties.getOrganizationId();
         Map<String, List<String>> connectionHeaders = new HashMap<>();
         if (StringUtils.hasText(projectId)) {
             connectionHeaders.put("OpenAI-Project", List.of(projectId));
         }

         if (StringUtils.hasText(organizationId)) {
             connectionHeaders.put("OpenAI-Organization", List.of(organizationId));
         }
         RestClient.Builder restClientBuilder = restClientBuilderProvider.getIfAvailable(RestClient::builder);
         WebClient.Builder webClientBuilder = webClientBuilderProvider.getIfAvailable(WebClient::builder);
         OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(baseUrl).apiKey(new SimpleApiKey(apiKey)).headers(CollectionUtils.toMultiValueMap(connectionHeaders)).completionsPath(chatProperties.getCompletionsPath()).embeddingsPath("/v1/embeddings").restClientBuilder(restClientBuilder).webClientBuilder(webClientBuilder).responseErrorHandler(responseErrorHandler).build();
         AlibabaOpenAiChatModel chatModel = AlibabaOpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(chatProperties.getOptions()).toolCallingManager(toolCallingManager).retryTemplate(retryTemplate).observationRegistry((ObservationRegistry) observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)).build();
         Objects.requireNonNull(chatModel);
         observationConvention.ifAvailable(chatModel::setObservationConvention);
         return chatModel;
     }



}
