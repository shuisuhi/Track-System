package org.example.tlias.ai.agents;

import org.example.tlias.ai.agents.AgentRequest;
import org.example.tlias.ai.agents.AgentResponse;

/**
 * Agent接口，定义所有Agent的通用方法
 */
public interface Agent {
    /**
     * 获取Agent名称
     * @return Agent名称
     */
    String getName();
    
    /**
     * 获取Agent的系统提示词
     * @return 系统提示词
     */
    String getSystemPrompt();
    
    /**
     * 处理请求
     * @param request 请求对象
     * @return 响应对象
     */
    AgentResponse process(AgentRequest request);
}