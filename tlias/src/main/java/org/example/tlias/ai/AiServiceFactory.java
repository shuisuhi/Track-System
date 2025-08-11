package org.example.tlias.ai;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.example.tlias.ai.agents.AgentManager;
import org.example.tlias.ai.agents.OrchestratorAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

public class AiServiceFactory {

    // 保留其他模型的注入（如果需要）
    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    @Lazy
    private AgentManager agentManager;

    // 手动管理的RedisChatMemoryStore实例
    private final RedisChatMemoryStore redisChatMemoryStore;

    // 构造方法：手动初始化RedisChatMemoryStore（从配置文件读取参数）
    public AiServiceFactory(
            @Value("${spring.data.redis.host}") String redisHost,
            @Value("${spring.data.redis.port}") Integer redisPort,// 无密码则为空
            @Value("${langchain4j.redis.prefix:tlias_chat_}") String keyPrefix,
            @Value("${langchain4j.redis.ttl:3600}") Long ttl  // 默认7天过期
    ) {
        // 手动构建RedisChatMemoryStore实例
        this.redisChatMemoryStore = RedisChatMemoryStore.builder()
                .host(redisHost)       // Redis主机地址
                .port(redisPort)       // Redis端口
                .prefix(keyPrefix)     // 存储键前缀（避免与其他数据冲突）
                .ttl(ttl)              // 聊天记录过期时间（秒）
                .build();
    }

    /**
     * 创建AI服务实例（使用手动创建的RedisChatMemoryStore）
     */
    public AiService createAiService(Long userId) {
        return createAiService(userId, "default");
    }
    
    /**
     * 创建AI服务实例（使用手动创建的RedisChatMemoryStore，支持Agent类型）
     * @param userId 用户ID
     * @param agentType Agent类型
     * @return AI服务实例
     */
    public AiService createAiService(Long userId, String agentType) {
        // 构建对话记忆（关联手动创建的Redis存储）
        // 使用用户ID和Agent类型组合作为记忆唯一标识
        String memoryId = userId.toString() + "_" + agentType;
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(memoryId)  // 用用户ID和Agent类型组合作为记忆唯一标识
                .chatMemoryStore(redisChatMemoryStore)  // 手动传入存储实例
                .maxMessages(10)       // 最多保留10条对话记录
                .build();

        // 创建并返回AI服务
        return AiServices.builder(AiService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }
    
    /**
     * 创建总协调Agent实例
     * @param userId 用户ID
     * @return 总协调Agent实例
     */
    public OrchestratorAgent createOrchestratorAgent(Long userId) {
        AiService aiService = createAiService(userId, "orchestrator");
        return new OrchestratorAgent(aiService, agentManager);
    }
}