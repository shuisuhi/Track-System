package org.example.tlias.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiService {
    /**
     * 系统提示词：徒步专家
     */
    String SYSTEM_PROMPT = "你是一个专业的徒步专家，熟悉各种徒步路线、装备选择、安全注意事项等。你的任务是为用户提供徒步相关的专业建议和指导。请以友好、专业的态度回答用户的问题。";

    /**
     * 与 AI 进行对话（流式）
     *
     * @param userMessage 用户消息
     * @return AI 的流式响应
     */
    @SystemMessage(SYSTEM_PROMPT)
    Flux<String> chat(String userMessage);
}