package org.example.tlias.controller;

import org.example.tlias.pojo.ChatHistory;
import org.example.tlias.service.ChatHistoryService;
import org.example.tlias.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对话历史 控制层。
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    /**
     * 查询某个用户的对话历史
     *
     * @param userId 用户ID
     * @return 对话历史列表
     */
    @GetMapping("/user/{userId}")
    public Result listUserChatHistory(@PathVariable Long userId) {
        List<ChatHistory> result = chatHistoryService.listChatHistoryByUserId(userId);
        return Result.success(result);
    }
}