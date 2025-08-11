package org.example.tlias.ai.agents;

import org.example.tlias.ai.AiService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 徒步专家Agent，专门处理徒步相关的咨询
 */
public class HikingExpertAgent extends BaseAgent {
    private static final String SYSTEM_PROMPT = 
        "你是一个专业的徒步专家，熟悉各种徒步路线、装备选择、安全注意事项等。你的任务是为用户提供徒步相关的专业建议和指导。请以友好、专业的态度回答用户的问题。";
    
    public HikingExpertAgent(AiService aiService) {
        super("徒步专家Agent", SYSTEM_PROMPT, aiService);
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