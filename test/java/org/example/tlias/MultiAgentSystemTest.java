package org.example.tlias;

import org.example.tlias.ai.MultiAgentFactory;
import org.example.tlias.ai.agents.AgentRequest;
import org.example.tlias.ai.agents.AgentResponse;
import org.example.tlias.ai.agents.OrchestratorAgent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.annotation.Resource;

/**
 * 多Agent系统测试类
 */
@SpringBootTest
public class MultiAgentSystemTest {
    
    @Resource
    private MultiAgentFactory multiAgentFactory;
    
    @Test
    public void testMultiAgentSystem() {
        try {
            // 创建总协调Agent实例
            OrchestratorAgent orchestratorAgent = multiAgentFactory.createOrchestratorAgent(1L);
            
            // 创建测试请求
            AgentRequest request = new AgentRequest("我想了解徒步旅行的装备和天气情况", 1L);
            
            // 处理请求
            AgentResponse response = orchestratorAgent.process(request);
            
            // 输出结果
            System.out.println("多Agent系统响应: " + response.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}