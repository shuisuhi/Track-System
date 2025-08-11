package org.example.tlias.ai.agents;

import org.example.tlias.ai.AiService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 路线规划Agent，专门处理徒步路线规划相关的咨询
 */
public class RoutePlanningAgent extends BaseAgent {
    private static final String SYSTEM_PROMPT = 
        "你是一个专业的路线规划师，专门负责为徒步活动设计和推荐路线。你需要根据用户的需求、经验水平、时间安排等因素，提供个性化的路线建议。同时给出往返的大交通规划";
    
    public RoutePlanningAgent(AiService aiService) {
        super("路线规划Agent", SYSTEM_PROMPT, aiService);
    }
    
    @Override
    public AgentResponse process(AgentRequest request) {
        // 调用AI服务进行对话
        Flux<String> responseFlux = aiService.chat(
            systemPrompt + "\n\n用户问题: " + request.getUserMessage());
        
        // 收集响应内容
        StringBuilder responseBuilder = new StringBuilder();
        responseFlux.doOnNext(chunk -> responseBuilder.append(chunk)).blockLast();
        
        // 构造响应对象
        AgentResponse response = new AgentResponse();
        response.setContent(responseBuilder.toString());
        response.setAgentName(name);
        
        return response;
    }
}