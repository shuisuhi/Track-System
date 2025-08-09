package org.example.tlias.ai.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 流消息基类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class StreamMessage {
    /**
     * 消息类型
     */
    private String type;

    public StreamMessage(String type) {
        this.type = type;
    }
}