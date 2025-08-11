package org.example.tlias.ai.agents;

import org.example.tlias.ai.AiService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 装备推荐Agent，专门处理徒步装备推荐相关的咨询
 */
public class EquipmentAgent extends BaseAgent {
    private static final String SYSTEM_PROMPT = 
        "你是一个专业的户外装备顾问，熟悉各种徒步装备的性能、适用场景和选择要点。你的任务是根据用户的徒步计划、个人情况和预算，推荐合适的装备。";
    
    public EquipmentAgent(AiService aiService) {
        super("装备推荐Agent", SYSTEM_PROMPT, aiService);
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