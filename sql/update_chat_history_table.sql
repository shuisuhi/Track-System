-- 更新chat_history表结构，添加agent_type字段

-- 添加agent_type字段
ALTER TABLE `chat_history` 
ADD COLUMN `agent_type` varchar(50) NOT NULL DEFAULT 'default' COMMENT 'Agent类型' AFTER `user_id`;

-- 删除旧的索引
DROP INDEX `idx_user_id` ON `chat_history`;

-- 创建新的复合索引
CREATE INDEX `idx_user_agent` ON `chat_history` (`user_id`, `agent_type`);

-- 创建时间索引（保留原有的时间索引）
CREATE INDEX `idx_create_time` ON `chat_history` (`create_time`);