CREATE TABLE `chat_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message` text NOT NULL COMMENT '消息内容',
  `message_type` varchar(10) NOT NULL COMMENT '消息类型 (user/ai)',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `agent_type` varchar(50) NOT NULL DEFAULT 'default' COMMENT 'Agent类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_user_agent` (`user_id`, `agent_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话历史表';