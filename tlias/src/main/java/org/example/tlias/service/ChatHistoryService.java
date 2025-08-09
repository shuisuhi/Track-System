package org.example.tlias.service;

import org.example.tlias.pojo.ChatHistory;

import java.util.List;

/**
 * 对话历史 服务层。
 */
public interface ChatHistoryService {

    /**
     * 添加对话历史
     *
     * @param message     消息
     * @param messageType 消息类型
     * @param userId      用户 id
     * @return 是否成功
     */
    boolean addChatMessage(String message, String messageType, Long userId);

    /**
     * 根据用户 ID 查询对话历史
     *
     * @param userId 用户 ID
     * @return 对话历史列表
     */
    List<ChatHistory> listChatHistoryByUserId(Long userId);
}