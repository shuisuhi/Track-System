package org.example.tlias.ai;

import org.example.tlias.ai.agents.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 多Agent工厂，用于创建不同类型的Agent实例
 */
@Component
public class MultiAgentFactory extends AiServiceFactory {
    
    public MultiAgentFactory(
            @Value("${spring.data.redis.host}") String redisHost,
            @Value("${spring.data.redis.port}") Integer redisPort,
            @Value("${langchain4j.redis.prefix:tlias_chat_}") String keyPrefix,
            @Value("${langchain4j.redis.ttl:3600}") Long ttl) {
        super(redisHost, redisPort, keyPrefix, ttl);
    }
    
    /**
     * 创建指定类型的Agent实例
     * @param agentType Agent类型
     * @param userId 用户ID
     * @return Agent实例
     */
    public Agent createAgent(String agentType, Long userId) {
        return createAgent(agentType, userId, agentType);
    }
    
    /**
     * 创建指定类型的Agent实例（支持Agent类型）
     * @param agentType Agent类型
     * @param userId 用户ID
     * @param memoryAgentType 记忆Agent类型
     * @return Agent实例
     */
    public Agent createAgent(String agentType, Long userId, String memoryAgentType) {
        // 获取AI服务实例
        AiService aiService = createAiService(userId, memoryAgentType);
        
        // 根据类型创建相应的Agent实例
        switch (agentType) {
            case "hiking_expert":
                return new HikingExpertAgent(aiService);
            case "weather":
                return new WeatherAgent(aiService);
            case "route_planning":
                return new RoutePlanningAgent(aiService);
            case "equipment":
                return new EquipmentAgent(aiService);
            default:
                throw new IllegalArgumentException("Unknown agent type: " + agentType);
        }
    }
}