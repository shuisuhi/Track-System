package org.example.tlias.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史 实体类。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 消息
     */
    private String message;

    /**
     * user/ai
     */
    private String messageType;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * Agent类型
     */
    private String agentType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}