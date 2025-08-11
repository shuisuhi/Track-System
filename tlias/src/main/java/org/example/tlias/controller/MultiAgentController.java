package org.example.tlias.controller;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.tlias.ai.MultiAgentFactory;
import org.example.tlias.ai.agents.AgentRequest;
import org.example.tlias.ai.agents.AgentResponse;
import org.example.tlias.ai.agents.OrchestratorAgent;
import org.example.tlias.ai.model.message.AiResponseMessage;
import org.example.tlias.service.ChatHistoryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 多Agent控制器，处理前端请求
 */
@RestController
@RequestMapping("/multi-agent")
@Slf4j
public class MultiAgentController {
    
    @Resource
    private MultiAgentFactory multiAgentFactory;
    
    @Resource
    private ChatHistoryService chatHistoryService;
    
    /**
     * 与多Agent系统进行对话
     *
     * @param message 用户消息
     * @param userId  用户 ID
     * @return 完整的 AI 响应
     */
    @GetMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public String chat(@RequestParam String message, @RequestParam Long userId) {
        return chat(message, userId, "orchestrator");
    }
    
    /**
     * 与多Agent系统进行对话（支持Agent类型）
     *
     * @param message    用户消息
     * @param userId     用户 ID
     * @param agentType  Agent类型
     * @return 完整的 AI 响应
     */
    @GetMapping(value = "/chat-with-agent", produces = MediaType.APPLICATION_JSON_VALUE)
    public String chat(@RequestParam String message, @RequestParam Long userId, @RequestParam String agentType) {
        try {
            // 保存用户消息到数据库
            chatHistoryService.addChatMessage(message, "user", userId, agentType);
            
            // 获取总协调Agent实例
            OrchestratorAgent orchestratorAgent = multiAgentFactory.createOrchestratorAgent(userId);
            
            // 创建请求
            AgentRequest request = new AgentRequest(message, userId);
            
            // 处理请求
            AgentResponse response = orchestratorAgent.process(request);
            
            // 保存 AI 响应到数据库
            if (response.getContent() != null && !response.getContent().isEmpty()) {
                chatHistoryService.addChatMessage(response.getContent(), "ai", userId, agentType);
            }
            
            // 构造最终响应
            AiResponseMessage aiResponseMessage = new AiResponseMessage(response.getContent());
            return JSONUtil.toJsonStr(aiResponseMessage);
        } catch (Exception e) {
            log.error("多Agent对话失败", e);
            // 发送错误消息
            String errorMessage = "{\"type\":\"error\",\"data\":\"多Agent对话失败: " + e.getMessage() + "\"}";
            return errorMessage;
        }
    }
}