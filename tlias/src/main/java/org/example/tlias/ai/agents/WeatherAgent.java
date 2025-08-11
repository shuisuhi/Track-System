package org.example.tlias.ai.agents;

import org.example.tlias.ai.AiService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 天气查询Agent，专门处理天气相关的咨询
 */
public class WeatherAgent extends BaseAgent {
    private static final String SYSTEM_PROMPT = 
        "你是一个天气查询专家，能够根据用户提供的地点和时间信息，给出相应的天气预报和建议。你需要结合徒步活动的特点，提供对徒步有影响的天气信息。";
    
    public WeatherAgent(AiService aiService) {
        super("天气查询Agent", SYSTEM_PROMPT, aiService);
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