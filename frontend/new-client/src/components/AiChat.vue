<template>
  <div class="ai-chat-container">
    <div class="chat-header">
      <h2>徒步专家AI助手</h2>
      <el-button type="primary" @click="goBack">返回</el-button>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div
        v-for="message in messages"
        :key="message.id"
        :class="['message', message.type]"
      >
        <div class="message-content">
          <div class="avatar">
            <img
              v-if="message.type === 'user'"
              src="../assets/default-avatar.png"
              alt="用户头像"
            />
            <img
              v-else
              src="../assets/logo.png"
              alt="AI头像"
            />
          </div>
          <div class="text">
            <div class="username">{{ message.type === 'user' ? '我' : '徒步专家' }}</div>
            <div class="content">
              <div v-if="message.type === 'ai' && message.isLoading" class="loading-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
              <div v-else>{{ message.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        placeholder="请输入您的问题..."
        @keyup.enter="sendMessage"
        :disabled="isSending"
        maxlength="500"
        show-word-limit
      />
      <el-button
        type="primary"
        @click="sendMessage"
        :loading="isSending"
        :disabled="!inputMessage.trim()"
      >
        发送
      </el-button>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'AiChat',
  setup() {
    const router = useRouter()
    const messages = ref([])
    const inputMessage = ref('')
    const isSending = ref(false)
    const messagesContainer = ref(null)
    const userId = localStorage.getItem('userId') || 1 // 默认用户ID为1
    
    // 消息ID计数器
    let messageId = 0
    
    // 生成消息ID
    const generateMessageId = () => {
      return messageId++
    }
    
    // 发送消息
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || isSending.value) {
        return
      }
      
      // 添加用户消息到聊天记录
      const userMessage = {
        id: generateMessageId(),
        type: 'user',
        content: inputMessage.value
      }
      messages.value.push(userMessage)
      
      // 清空输入框
      const userMessageContent = inputMessage.value
      inputMessage.value = ''
      
      // 设置发送状态
      isSending.value = true
      
      // 添加AI回复占位符（带加载状态）
      const aiMessage = {
        id: generateMessageId(),
        type: 'ai',
        content: '',
        isLoading: true
      }
      messages.value.push(aiMessage)
      
      // 滚动到最新消息
      scrollToBottom()
      
      try {
        // 从localStorage获取token
        const token = localStorage.getItem('token')
        
        // 构造请求URL
        const baseUrl = `http://localhost:8123/api/ai/chat`
        const params = new URLSearchParams({
          message: userMessageContent,
          userId: userId
        })
        const url = `${baseUrl}?${params.toString()}`
        
        // 发送HTTP请求
        console.log('发送HTTP请求:', url)
        
        fetch(url)
          .then(response => {
            if (!response.ok) {
              throw new Error(`HTTP error! status: ${response.status}`)
            }
            return response.json()
          })
          .then(data => {
            console.log('收到AI响应:', data)
            // 更新AI消息内容
            aiMessage.content = data.data || data
            aiMessage.isLoading = false
            // 滚动到最新消息
            scrollToBottom()
            // 重置发送状态
            isSending.value = false
          })
          .catch(error => {
            console.error('AI请求失败:', error)
            ElMessage.error('与AI助手的连接出现错误')
            // 更新AI消息内容为错误信息
            aiMessage.content = '抱歉，我无法回答您的问题。请稍后再试。'
            aiMessage.isLoading = false
            // 重置发送状态
            isSending.value = false
          })
      } catch (error) {
        console.error('发送消息失败:', error)
        ElMessage.error('发送消息失败')
        // 更新AI消息内容为错误信息
        if (messages.value.length > 0) {
          const lastMessage = messages.value[messages.value.length - 1]
          if (lastMessage.type === 'ai') {
            lastMessage.content = '抱歉，发送消息失败。请稍后再试。'
            lastMessage.isLoading = false
          }
        }
        isSending.value = false
      }
    }
    
    // 滚动到最新消息
    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }
    
    // 返回主页
    const goBack = () => {
      router.push('/')
    }
    
    // 初始化欢迎消息
    onMounted(() => {
      messages.value.push({
        id: generateMessageId(),
        type: 'ai',
        content: '您好！我是徒步专家AI助手，很高兴为您服务。请问您想了解关于徒步旅行的哪些方面呢？'
      })
    })
    
    return {
      messages,
      inputMessage,
      isSending,
      messagesContainer,
      sendMessage,
      goBack
    }
  }
}
</script>

<style scoped>
.ai-chat-container {
  max-width: 800px;
  height: 100vh;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
  box-sizing: border-box;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #2E8B57;
  color: white;
  flex-shrink: 0; /* 防止头部收缩 */
}

.chat-header h2 {
  margin: 0;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  box-sizing: border-box;
}

.message {
  display: flex;
  justify-content: flex-start;
}

.message.user {
  justify-content: flex-end;
}

.message-content {
  display: flex;
  gap: 10px;
  max-width: 80%;
}

.message.user .message-content {
  flex-direction: row-reverse;
}

.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.text {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.content {
  background-color: white;
  padding: 10px 15px;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  word-wrap: break-word;
  word-break: break-all;
}

.message.user .content {
  background-color: #2E8B57;
  color: white;
}

/* 加载动画 */
.loading-dots {
  display: flex;
  align-items: center;
  height: 20px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background-color: #2E8B57;
  border-radius: 50%;
  margin-right: 5px;
  animation: wave 1.2s linear infinite;
}

.loading-dots span:nth-child(2) {
  animation-delay: -1.1s;
}

.loading-dots span:nth-child(3) {
  animation-delay: -1s;
}

@keyframes wave {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-5px);
  }
}

.chat-input {
  display: flex;
  gap: 10px;
  padding: 20px;
  background-color: white;
  border-top: 1px solid #eee;
  flex-shrink: 0; /* 防止输入区域收缩 */
  box-sizing: border-box;
}

.chat-input .el-input {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-chat-container {
    max-width: 100%;
  }
  
  .chat-messages {
    padding: 10px;
  }
  
  .message-content {
    max-width: 90%;
  }
  
  .chat-input {
    padding: 10px;
  }
}
</style>