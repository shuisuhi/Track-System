package org.example.tlias.ai.agents;

import org.example.tlias.ai.AiService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 总协调Agent，负责协调其他Agents的工作
 */
public class OrchestratorAgent extends BaseAgent {
    private static final String SYSTEM_PROMPT = 
        "你是一个智能任务协调专家，负责理解用户需求、分析任务类型、调用相应的专用Agents，并整合各Agents的响应结果。你需要：\n" +
        "1. 仔细分析用户的请求，理解其真实意图\n" +
        "2. 根据任务类型选择合适的专用Agents\n" +
        "3. 向专用Agents提供清晰、准确的任务描述\n" +
        "4. 整合各Agents的响应，形成连贯、完整的回答\n" +
        "5. 以友好、专业的态度与用户交流";
    
    private final AgentManager agentManager;
    
    public OrchestratorAgent(AiService aiService, AgentManager agentManager) {
        super("总协调Agent", SYSTEM_PROMPT, aiService);
        this.agentManager = agentManager;
    }
    
    @Override
    public AgentResponse process(AgentRequest request) {
        // 1. 分析用户意图
        List<String> selectedAgentNames = analyzeIntent(request.getUserMessage());
        
        // 2. 并行调用Agents
        List<CompletableFuture<AgentResponse>> futures = new ArrayList<>();
        for (String agentName : selectedAgentNames) {
            CompletableFuture<AgentResponse> future = CompletableFuture
                .supplyAsync(() -> {
                    // 创建Agent实例
                    Agent agent = agentManager.createAgent(getAgentTypeByName(agentName), request.getUserId());
                    if (agent != null) {
                        return agent.process(request);
                    }
                    return null;
                });
            futures.add(future);
        }
        
        // 3. 等待所有结果返回
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // 4. 整合结果
        Map<String, String> agentResponses = new HashMap<>();
        for (CompletableFuture<AgentResponse> future : futures) {
            AgentResponse response = future.getNow(null);
            if (response != null) {
                agentResponses.put(response.getAgentName(), response.getContent());
            }
        }
        
        // 5. 生成最终响应
        String finalResponse = generateStructuredResponse(agentResponses);
        
        // 构造响应对象
        AgentResponse response = new AgentResponse();
        response.setContent(finalResponse);
        response.setAgentName(name);
        
        return response;
    }
    
    /**
     * 分析用户意图，确定需要调用的Agents
     * @param userMessage 用户消息
     * @return 需要调用的Agent名称列表
     */
    private List<String> analyzeIntent(String userMessage) {
        // 简化实现，实际应使用大模型分析意图
        List<String> agentNames = new ArrayList<>();
        if (userMessage.contains("徒步") || userMessage.contains("路线")) {
            agentNames.add("徒步专家Agent");
        }
        if (userMessage.contains("天气")) {
            agentNames.add("天气查询Agent");
        }
        if (userMessage.contains("装备")) {
            agentNames.add("装备推荐Agent");
        }
        if (userMessage.contains("规划")) {
            agentNames.add("路线规划Agent");
        }
        // 默认添加徒步专家Agent
        if (agentNames.isEmpty()) {
            agentNames.add("徒步专家Agent");
        }
        return agentNames;
    }
    
    /**
     * 生成最终响应
     * @param agentResponses 各Agent的响应
     * @param userMessage 用户原始消息
     * @return 最终响应
     */
    private String generateFinalResponse(String agentResponses, String userMessage) {
        // 简化实现，实际应使用大模型整合响应
        return "综合各专家建议:\n\n" + agentResponses;
    }
    
    /**
     * 生成结构化的响应
     * @param agentResponses 各Agent的响应
     * @return 结构化的响应（JSON格式）
     */
    private String generateStructuredResponse(Map<String, String> agentResponses) {
        // 构建JSON格式的响应
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"summary\": \"综合各专家建议:\",\n");
        json.append("  \"responses\": {\n");
        
        int count = 0;
        for (Map.Entry<String, String> entry : agentResponses.entrySet()) {
            if (count > 0) {
                json.append(",\n");
            }
            json.append("    \"")
                .append(entry.getKey())
                .append("\": \"")
                .append(entry.getValue().replace("\"", "\\\""))  // 转义双引号
                .append("\"");
            count++;
        }
        
        json.append("\n  }\n}");
        return json.toString();
    }
    
    /**
     * 根据Agent名称获取Agent类型
     * @param agentName Agent名称
     * @return Agent类型
     */
    private String getAgentTypeByName(String agentName) {
        switch (agentName) {
            case "徒步专家Agent":
                return "hiking_expert";
            case "天气查询Agent":
                return "weather";
            case "路线规划Agent":
                return "route_planning";
            case "装备推荐Agent":
                return "equipment";
            default:
                return "default";
        }
    }
    
    /**
     * 格式化Agent响应，将Map转换为字符串
     * @param agentResponses Agent响应Map
     * @return 格式化后的字符串
     */
    private String formatAgentResponses(Map<String, String> agentResponses) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : agentResponses.entrySet()) {
            result.append(entry.getKey())
                  .append("的建议: ")
                  .append(entry.getValue())
                  .append("\n\n");
        }
        return result.toString();
    }
}