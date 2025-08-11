package org.example.tlias.ai.agents;

import jakarta.annotation.PostConstruct;
import org.example.tlias.ai.AiService;
import org.example.tlias.ai.MultiAgentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent管理器，负责Agent的注册和管理
 */
@Component
public class AgentManager {
    private final Map<String, Agent> agents = new ConcurrentHashMap<>();
    
    @Autowired
    private MultiAgentFactory aiServiceFactory;
    
    /**
     * 初始化Agents
     */
    @PostConstruct
    public void initAgents() {
        // 这里只是示例，实际应用中应该从配置或数据库加载Agents
        // 注册Agents的逻辑应该在应用启动时完成
    }
    
    /**
     * 创建指定类型的Agent实例
     * @param agentType Agent类型
     * @param userId 用户ID
     * @return Agent实例
     */
    public Agent createAgent(String agentType, Long userId) {
        return aiServiceFactory.createAgent(agentType, userId);
    }
    
    /**
     * 注册Agent
     * @param agent Agent实例
     */
    public void registerAgent(Agent agent) {
        agents.put(agent.getName(), agent);
    }
    
    /**
     * 根据名称获取Agent
     * @param name Agent名称
     * @return Agent实例
     */
    public Agent getAgent(String name) {
        return agents.get(name);
    }
    
    /**
     * 获取所有已注册的Agents
     * @return Agent列表
     */
    public List<Agent> getAllAgents() {
        return new ArrayList<>(agents.values());
    }
}