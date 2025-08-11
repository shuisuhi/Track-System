package org.example.tlias.configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.example.tlias.ai.MultiAgentFactory;
import org.example.tlias.ai.agents.*;
import org.springframework.context.annotation.Configuration;

/**
 * Agent配置类，用于在应用启动时注册所有Agents
 */
@Configuration
public class AgentConfiguration {
    
    @Resource
    private AgentManager agentManager;
    
    @Resource
    private MultiAgentFactory aiServiceFactory;
    
    /**
     * 初始化并注册所有Agents
     */
    @PostConstruct
    public void initAgents() {
        // 创建并注册徒步专家Agent
        // 注意：这里只是示例，实际应用中应该从配置或数据库加载Agents
        // 在实际应用中，Agents的创建和注册应该通过工厂模式或依赖注入完成
        
        // 注册Agents的逻辑将在运行时通过MultiAgentFactory完成
        // 这里只是确保AgentManager已准备好
    }
}