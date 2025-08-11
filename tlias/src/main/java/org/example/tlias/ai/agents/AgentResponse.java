package org.example.tlias.ai.agents;

import java.util.Map;

/**
 * Agent响应对象
 */
public class AgentResponse {
    private String content;
    private String agentName;
    private Map<String, Object> metadata;
    
    public AgentResponse() {
    }
    
    public AgentResponse(String content, String agentName) {
        this.content = content;
        this.agentName = agentName;
    }
    
    // Getters and setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getAgentName() {
        return agentName;
    }
    
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}