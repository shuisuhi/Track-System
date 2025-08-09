<template>
  <div class="home-container">
    <div class="content-wrapper">
      <!-- AI对话窗口按钮 -->
      <div class="ai-chat-button" @click="goToAiChat">
        <el-button type="primary" circle size="large" :icon="ChatRound" class="ai-chat-btn" />
      </div>
      
      <!-- 左侧分类导航 -->
      <div class="category-sidebar">
        <div class="category-card">
          <h3>分类</h3>
          <div class="category-list">
            <div 
              v-for="category in categories" 
              :key="category.id" 
              class="category-item"
              :class="{ active: currentCategory === category.id }"
              @click="selectCategory(category.id)"
            >
              {{ category.name }}
            </div>
          </div>
        </div>
      </div>

      <!-- 中间帖子展示区 -->
      <div class="posts-content">
        <div class="posts-grid">
          <div 
            v-for="post in filteredPosts" 
            :key="post.postId" 
            class="post-grid-item"
            @click="viewPostDetail(post)"
          >
            <div class="post-grid-content">
              <div v-if="post.mediaUrl" class="post-grid-media">
                <img :src="post.mediaUrl" alt="帖子图片" />
              </div>
              <div class="post-grid-info">
                <div class="post-grid-title">{{ post.title || '无标题' }}</div>
                <div class="post-grid-user">
                  <img :src="post.avatarUrl || './assets/default-avatar.png'" alt="头像" class="avatar" />
                  <span>{{ post.username || '用户' + post.userId }}</span>
                </div>
                <div class="post-grid-stats">
                  <span class="likes">{{ post.likesCount || 0 }} 点赞</span>
                  <span class="comments">{{ post.commentsCount || 0 }} 评论</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧侧边栏 -->
      <div class="sidebar">
        <!-- 用户信息卡片 -->
        <div class="user-card">
          <div class="user-header">
            <img :src="currentUser.avatar || './assets/default-avatar.png'" alt="头像" class="avatar" />
            <div class="user-details">
              <div class="username">{{ currentUser.username || '用户' }}</div>
              <div class="user-intro">{{ currentUser.introduction || '暂无简介' }}</div>
            </div>
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-number">{{ currentUser.postCount || 0 }}</div>
              <div class="stat-label">帖子</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ currentUser.followers || 0 }}</div>
              <div class="stat-label">粉丝</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ currentUser.following || 0 }}</div>
              <div class="stat-label">关注</div>
            </div>
          </div>
          <el-button type="primary" @click="goToProfile">查看个人主页</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ChatRound } from '@element-plus/icons-vue'
import { postApi, userApi } from '../utils/api'

export default {
  name: 'Home',
  setup() {
    const router = useRouter()
    const posts = ref([])
    const currentUser = ref({})
    const currentCategory = ref('all')
    
    // 分类数据
    const categories = ref([
      { id: 'all', name: '全部路线' },
      { id: 'mountain', name: '山地徒步' },
      { id: 'forest', name: '森林徒步' },
      { id: 'coastal', name: '海岸徒步' },
      { id: 'desert', name: '沙漠徒步' },
      { id: 'trail', name: '小径徒步' }
    ])
    
    // 格式化时间
    const formatTime = (time) => {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return `${Math.floor(diff / 60000)}分钟前`
      } else if (diff < 86400000) { // 1天内
        return `${Math.floor(diff / 3600000)}小时前`
      } else {
        return `${Math.floor(diff / 86400000)}天前`
      }
    }

    // 获取当前用户信息
    const getCurrentUser = async () => {
      try {
        const userId = localStorage.getItem('userId')
        if (userId) {
          const response = await userApi.getUserById(userId)
          if (response.data.code === 50) {
            currentUser.value = response.data.data
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }

    // 获取帖子列表
    const getPosts = async () => {
      try {
        const response = await postApi.getAllPosts()
        if (response.data.code === 1) {
          posts.value = response.data.data
          // 处理帖子数据，将url字段转换为mediaUrl字段
          posts.value.forEach(post => {
            // 如果帖子有媒体文件，取第一个作为mediaUrl
            if (post.url && post.url.length > 0) {
              post.mediaUrl = post.url[0]
            }
            // 模拟点赞状态检查
            post.isLiked = Math.random() > 0.5 // 随机设置点赞状态
          })
        }
      } catch (error) {
        console.error('获取帖子失败:', error)
        ElMessage.error('获取帖子失败')
      }
    }

    // 选择分类
    const selectCategory = (categoryId) => {
      currentCategory.value = categoryId
    }

    // 过滤帖子
    const filteredPosts = computed(() => {
      // 在实际应用中，这里可以根据分类ID过滤帖子
      // 目前我们只是返回所有帖子
      return posts.value
    })

    // 查看帖子详情
    const viewPostDetail = (post) => {
      router.push(`/post/${post.postId}`)
    }

    // 跳转到个人主页
    const goToProfile = () => {
      if (currentUser.value.userId) {
        router.push(`/profile/${currentUser.value.userId}`)
      }
    }

    // 跳转到AI对话页面
    const goToAiChat = () => {
      router.push('/ai-chat')
    }

    onMounted(() => {
      getCurrentUser()
      getPosts()
    })

    return {
      posts,
      currentUser,
      categories,
      currentCategory,
      filteredPosts,
      formatTime,
      selectCategory,
      viewPostDetail,
      goToProfile,
      goToAiChat,
      ChatRound
    }
  }
}
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-wrapper {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

/* 左侧分类导航 */
.category-sidebar {
  width: 200px;
}

.category-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.category-card h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #2E8B57;
  border-bottom: 2px solid #2E8B57;
  padding-bottom: 10px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-item {
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #333;
}

.category-item:hover {
  background-color: #f0f0f0;
}

.category-item.active {
  background-color: #2E8B57;
  color: white;
}

/* 中间帖子展示区 */
.posts-content {
  flex: 1;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.post-grid-item {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.post-grid-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.post-grid-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.post-grid-media {
  height: 150px;
  overflow: hidden;
}

.post-grid-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-grid-info {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.post-grid-title {
  font-weight: 500;
  margin-bottom: 10px;
  flex: 1;
}

.post-grid-user {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.post-grid-user .avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  margin-right: 8px;
}

.post-grid-stats {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

/* 右侧侧边栏 */
.sidebar {
  width: 250px;
}

.user-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.user-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.user-header .avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-right: 15px;
}

.user-details .username {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 5px;
}

.user-details .user-intro {
  font-size: 14px;
  color: #666;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 5px;
  color: #2E8B57;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* AI对话窗口按钮 */
.ai-chat-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 1000;
}

.ai-chat-btn {
  width: 60px;
  height: 60px;
  font-size: 24px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.ai-chat-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

/* 响应式设计 */
@media (max-width: 992px) {
  .posts-grid {
    grid-template-columns: 1fr;
  }
  
  .category-sidebar {
    width: 150px;
  }
  
  .sidebar {
    width: 200px;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    flex-direction: column;
  }
  
  .category-sidebar,
  .sidebar {
    width: 100%;
  }
  
  .category-card,
  .user-card {
    margin-bottom: 20px;
  }
}
</style>