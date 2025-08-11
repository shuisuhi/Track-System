package org.example.tlias.controller;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.tlias.ai.AiService;
import org.example.tlias.ai.AiServiceFactory;
import org.example.tlias.ai.model.message.AiResponseMessage;
import org.example.tlias.service.ChatHistoryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiController {

    @Resource
    private AiServiceFactory aiServiceFactory;

    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * 与 AI 进行对话（返回完整响应）
     *
     * @param message 用户消息
     * @param userId  用户 ID
     * @return 完整的 AI 响应
     */
    @GetMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> chat(@RequestParam String message, @RequestParam Long userId) {
        return chat(message, userId, "default");
    }

    /**
     * 与 AI 进行对话（返回完整响应，支持Agent类型）
     *
     * @param message    用户消息
     * @param userId     用户 ID
     * @param agentType  Agent类型
     * @return 完整的 AI 响应
     */
    @GetMapping(value = "/chat-with-agent", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> chat(@RequestParam String message, @RequestParam Long userId, @RequestParam String agentType) {
        try {
            // 保存用户消息到数据库
            chatHistoryService.addChatMessage(message, "user", userId, agentType);

            // 获取 AI 服务实例
            AiService aiService = aiServiceFactory.createAiService(userId);

            // 调用 AI 服务进行对话（流式）
            Flux<String> responseFlux = aiService.chat(message);

            // 收集 AI 响应内容
            StringBuilder aiResponseBuilder = new StringBuilder();

            // 将 AI 响应收集为完整字符串
            return responseFlux
                    .doOnNext(chunk -> {
                        // 收集 AI 响应内容
                        aiResponseBuilder.append(chunk);
                    })
                    .doOnComplete(() -> {
                        // 保存 AI 响应到数据库
                        String aiResponse = aiResponseBuilder.toString();
                        if (!aiResponse.isEmpty()) {
                            chatHistoryService.addChatMessage(aiResponse, "ai", userId, agentType);
                        }
                    })
                    .then(Mono.fromCallable(() -> {
                        // 构造最终响应
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(aiResponseBuilder.toString());
                        return JSONUtil.toJsonStr(aiResponseMessage);
                    }));
        } catch (Exception e) {
            log.error("AI 对话失败", e);
            // 发送错误消息
            String errorMessage = "{\"type\":\"error\",\"data\":\"AI 对话失败: " + e.getMessage() + "\"}";
            return Mono.just(errorMessage);
        }
    }
}