package org.example.tlias.service.impl;

import org.example.tlias.mapper.ChatHistoryMapper;
import org.example.tlias.pojo.ChatHistory;
import org.example.tlias.pojo.ChatHistoryMessageTypeEnum;
import org.example.tlias.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 */
@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    @Autowired
    private ChatHistoryMapper chatHistoryMapper;

    @Override
    public boolean addChatMessage(String message, String messageType, Long userId) {
        // 基础校验
        if (message == null || message.isEmpty()) {
            return false;
        }
        if (messageType == null || messageType.isEmpty()) {
            return false;
        }
        if (userId == null || userId <= 0) {
            return false;
        }
        // 验证消息类型是否有效
        ChatHistoryMessageTypeEnum messageTypeEnum = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
        if (messageTypeEnum == null) {
            return false;
        }
        // 插入数据库
        ChatHistory chatHistory = ChatHistory.builder()
                .message(message)
                .messageType(messageType)
                .userId(userId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return chatHistoryMapper.insert(chatHistory) > 0;
    }

    @Override
    public List<ChatHistory> listChatHistoryByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return List.of();
        }
        return chatHistoryMapper.selectByUserId(userId);
    }
}