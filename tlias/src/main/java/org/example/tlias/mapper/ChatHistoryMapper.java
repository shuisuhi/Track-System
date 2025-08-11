package org.example.tlias.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.tlias.pojo.ChatHistory;

import java.util.List;

/**
 * 对话历史 映射层。
 */
@Mapper
public interface ChatHistoryMapper {

    /**
     * 插入对话历史
     *
     * @param chatHistory 对话历史记录
     * @return 是否成功
     */
    @Insert("INSERT INTO chat_history (message, message_type, user_id, agent_type, create_time, update_time) " +
            "VALUES (#{message}, #{messageType}, #{userId}, #{agentType}, #{createTime}, #{updateTime})")
    int insert(ChatHistory chatHistory);

    /**
     * 根据用户 ID 查询对话历史
     *
     * @param userId 用户 ID
     * @return 对话历史列表
     */
    @Select("SELECT * FROM chat_history WHERE user_id = #{userId} ORDER BY create_time ASC")
    List<ChatHistory> selectByUserId(Long userId);

    /**
     * 根据用户 ID 和 Agent 类型查询对话历史
     *
     * @param userId 用户 ID
     * @param agentType Agent 类型
     * @return 对话历史列表
     */
    @Select("SELECT * FROM chat_history WHERE user_id = #{userId} AND agent_type = #{agentType} ORDER BY create_time ASC")
    List<ChatHistory> selectByUserIdAndAgentType(Long userId, String agentType);
}