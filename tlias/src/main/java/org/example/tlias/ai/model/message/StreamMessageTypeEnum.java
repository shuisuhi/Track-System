package org.example.tlias.ai.model.message;

import lombok.Getter;

/**
 * 流消息类型枚举
 */
@Getter
public enum StreamMessageTypeEnum {
    /**
     * AI 响应消息
     */
    AI_RESPONSE("ai_response"),

    /**
     * 工具调用请求消息
     */
    TOOL_REQUEST("tool_request"),

    /**
     * 工具执行结果消息
     */
    TOOL_EXECUTED("tool_executed");

    private final String value;

    StreamMessageTypeEnum(String value) {
        this.value = value;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 值
     * @return 枚举
     */
    public static StreamMessageTypeEnum getEnumByValue(String value) {
        for (StreamMessageTypeEnum typeEnum : StreamMessageTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}