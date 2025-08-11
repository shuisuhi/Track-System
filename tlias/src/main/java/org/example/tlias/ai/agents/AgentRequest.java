package org.example.tlias.ai.agents;

import java.util.Map;

/**
 * Agent请求对象
 */
public class AgentRequest {
    private String userMessage;
    private String taskDescription;
    private Map<String, Object> context;
    private Long userId;
    
    public AgentRequest() {
    }
    
    public AgentRequest(String userMessage, Long userId) {
        this.userMessage = userMessage;
        this.userId = userId;
    }
    
    // Getters and setters
    public String getUserMessage() {
        return userMessage;
    }
    
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
    
    public String getTaskDescription() {
        return taskDescription;
    }
    
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    
    public Map<String, Object> getContext() {
        return context;
    }
    
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}