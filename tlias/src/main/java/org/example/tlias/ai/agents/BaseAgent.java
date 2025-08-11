package org.example.tlias.ai.agents;

import org.example.tlias.ai.AiService;

/**
 * Agent基类，提供所有Agent的通用实现
 */
public abstract class BaseAgent implements Agent {
    protected final String name;
    protected final String systemPrompt;
    protected final AiService aiService;
    
    public BaseAgent(String name, String systemPrompt, AiService aiService) {
        this.name = name;
        this.systemPrompt = systemPrompt;
        this.aiService = aiService;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getSystemPrompt() {
        return systemPrompt;
    }
}