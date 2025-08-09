<template>
  <div class="post-detail-container">
    <div class="post-content">
      <!-- 帖子头部 -->
      <div class="post-header">
        <div class="author-info">
          <img :src="post.avatarUrl || '../assets/default-avatar.png'" alt="头像" class="avatar" />
          <div class="author-details">
            <div class="author-name">{{ post.username }}</div>
            <div class="post-time">{{ formatTime(post.createdAt) }}</div>
          </div>
        </div>
        <div class="post-actions">
          <el-dropdown @command="handlePostAction">
            <el-button size="small" circle>
              <el-icon><MoreFilled /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :command="{action: 'collect', post: post}">收藏</el-dropdown-item>
                <el-dropdown-item :command="{action: 'report', post: post}">举报</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 帖子标题 -->
      <h1 class="post-title">{{ post.title }}</h1>

      <!-- 帖子内容 -->
      <div class="post-text">
        <p>{{ post.content }}</p>
      </div>

      <!-- 帖子媒体 -->
      <div v-if="post.mediaUrl" class="post-media">
        <img :src="post.mediaUrl" alt="帖子图片" />
      </div>

      <!-- 帖子统计数据 -->
      <div class="post-stats">
        <div class="stat-item">
          <el-icon><Star /></el-icon>
          <span>{{ post.likesCount || 0 }} 点赞</span>
        </div>
        <div class="stat-item">
          <el-icon><ChatLineRound /></el-icon>
          <span>{{ post.commentsCount || 0 }} 评论</span>
        </div>
        <div class="stat-item">
          <el-icon><Share /></el-icon>
          <span>分享</span>
        </div>
      </div>

      <!-- 帖子操作按钮 -->
      <div class="post-actions-bar">
        <el-button 
          type="primary" 
          link 
          @click="likePost"
          :class="{ liked: isLiked }"
        >
          <el-icon><Star /></el-icon>
          {{ isLiked ? '已点赞' : '点赞' }}
        </el-button>
        <el-button type="primary" link @click="focusCommentInput">
          <el-icon><ChatLineRound /></el-icon>
          评论
        </el-button>
        <el-button type="primary" link>
          <el-icon><Share /></el-icon>
          分享
        </el-button>
      </div>
    </div>

    <!-- 评论区域 -->
    <div class="comments-section">
      <h2>评论 ({{ comments.length }})</h2>
      
      <!-- 添加评论 -->
      <div class="add-comment">
        <div class="comment-input-container">
          <img :src="currentUser.avatarUrl || '../assets/default-avatar.png'" alt="头像" class="avatar" />
          <div class="comment-input-wrapper">
            <el-input 
              v-model="commentContent" 
              placeholder="添加评论..." 
              type="textarea" 
              :rows="2"
              ref="commentInputRef"
            />
            <div class="comment-actions">
              <el-button 
                type="primary" 
                @click="submitComment" 
                :disabled="!commentContent.trim()"
                size="small"
              >
                发布
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
          <div class="comment-header">
            <img :src="comment.avatarUrl || '../assets/default-avatar.png'" alt="头像" class="avatar" />
            <div class="comment-author">{{ comment.username }}</div>
            <div class="comment-time">{{ formatTime(comment.createdAt) }}</div>
          </div>
          <div class="comment-content">
            <p>{{ comment.content }}</p>
          </div>
          <div class="comment-actions">
            <el-button 
              type="primary" 
              link 
              @click="likeComment(comment)"
              :class="{ liked: comment.isLiked }"
              size="small"
            >
              <el-icon><Star /></el-icon>
              {{ comment.likesCount || 0 }}
            </el-button>
            <el-button type="primary" link size="small" @click="replyToComment(comment)">
              回复
            </el-button>
          </div>

          <!-- 回复评论 -->
          <div v-if="comment.showReplyInput" class="reply-input">
            <el-input 
              v-model="comment.replyContent" 
              placeholder="添加回复..." 
              size="small"
              ref="replyInputRef"
            />
            <div class="reply-actions">
              <el-button size="small" @click="comment.showReplyInput = false">取消</el-button>
              <el-button 
                type="primary" 
                size="small" 
                @click="submitReply(comment)"
                :disabled="!comment.replyContent.trim()"
              >
                发布
              </el-button>
            </div>
          </div>

          <!-- 回复列表 -->
          <div class="replies-list">
            <div v-for="reply in comment.replies" :key="reply.replyId" class="reply-item">
              <div class="reply-header">
                <span class="reply-author">{{ reply.username }}</span>
                <span class="reply-time">{{ formatTime(reply.createdAt) }}</span>
              </div>
              <div class="reply-content">
                <p>{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postApi, commentApi, replyApi, userApi } from '../utils/api'

export default {
  name: 'PostDetail',
  props: {
    id: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const route = useRoute()
    const router = useRouter()
    const post = ref({})
    const comments = ref([])
    const commentContent = ref('')
    const commentInputRef = ref(null)
    const currentUser = ref({})
    const isLiked = ref(false)

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
        const year = date.getFullYear()
        const month = date.getMonth() + 1
        const day = date.getDate()
        return `${year}-${month}-${day}`
      }
    }

    // 获取帖子详情
    const getPostDetail = async () => {
      try {
        const response = await postApi.getPostById(props.id)
        if (response.data.code === 1) {
          post.value = response.data.data
          // 检查是否已点赞
          checkPostLikeStatus()
        }
      } catch (error) {
        console.error('获取帖子详情失败:', error)
        ElMessage.error('获取帖子详情失败')
      }
    }

    // 获取评论列表
    const getComments = async () => {
      try {
        const response = await commentApi.getCommentsByPostId(props.id, 1, 100)
        if (response.data.code === 1) {
          comments.value = response.data.data.list || []
          // 为每个评论添加回复相关属性
          comments.value.forEach(comment => {
            comment.replyContent = ''
            comment.showReplyInput = false
            comment.replies = []
          })
          // 获取回复
          getRepliesForComments()
        }
      } catch (error) {
        console.error('获取评论失败:', error)
        ElMessage.error('获取评论失败')
      }
    }

    // 获取回复
    const getRepliesForComments = async () => {
      for (const comment of comments.value) {
        try {
          const response = await replyApi.getRepliesByCommentId(comment.commentId)
          if (response.data.code === 1) {
            comment.replies = response.data.data || []
          }
        } catch (error) {
          console.error('获取回复失败:', error)
        }
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

    // 检查帖子点赞状态
    const checkPostLikeStatus = async () => {
      const userId = localStorage.getItem('userId')
      if (userId) {
        try {
          const response = await postApi.isPostLiked(props.id, userId)
          isLiked.value = response.data.code === 60
        } catch (error) {
          console.error('检查点赞状态失败:', error)
        }
      }
    }

    // 点赞帖子
    const likePost = async () => {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      try {
        const response = await postApi.likePost(props.id, userId)
        if (response.data.code === 1) {
          isLiked.value = !isLiked.value
          post.value.likesCount = isLiked.value ? (post.value.likesCount || 0) + 1 : (post.value.likesCount || 1) - 1
          ElMessage.success(isLiked.value ? '点赞成功' : '取消点赞')
        } else {
          ElMessage.error('操作失败')
        }
      } catch (error) {
        console.error('点赞失败:', error)
        ElMessage.error('操作失败')
      }
    }

    // 提交评论
    const submitComment = async () => {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      if (!commentContent.value.trim()) {
        ElMessage.warning('请输入评论内容')
        return
      }

      try {
        const commentData = {
          postId: props.id,
          userId: parseInt(userId),
          content: commentContent.value,
          createdAt: new Date().toISOString()
        }

        const response = await commentApi.createComment(commentData)
        if (response.data.code === 1) {
          ElMessage.success('评论成功')
          commentContent.value = ''
          // 重新加载评论
          getComments()
        } else {
          ElMessage.error(response.data.message || '评论失败')
        }
      } catch (error) {
        console.error('评论失败:', error)
        ElMessage.error('评论失败')
      }
    }

    // 回复评论
    const replyToComment = (comment) => {
      comment.showReplyInput = true
      // 等待DOM更新后聚焦输入框
      nextTick(() => {
        // 这里可以获取回复输入框并聚焦，但为了简化示例，我们省略这一步
      })
    }

    // 提交回复
    const submitReply = async (comment) => {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      if (!comment.replyContent.trim()) {
        ElMessage.warning('请输入回复内容')
        return
      }

      try {
        const replyData = {
          commentId: comment.commentId,
          userId: parseInt(userId),
          content: comment.replyContent,
          createdAt: new Date().toISOString(),
          postId: props.id
        }

        const response = await replyApi.createReply(replyData)
        if (response.data.code === 1) {
          ElMessage.success('回复成功')
          comment.replyContent = ''
          comment.showReplyInput = false
          // 重新加载回复
          getRepliesForComments()
        } else {
          ElMessage.error(response.data.message || '回复失败')
        }
      } catch (error) {
        console.error('回复失败:', error)
        ElMessage.error('回复失败')
      }
    }

    // 评论点赞
    const likeComment = async (comment) => {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      try {
        // 检查是否已点赞
        const checkResponse = await commentApi.isCommentLiked(comment.commentId, userId)
        const isCommentLiked = checkResponse.data.code === 60

        // 点赞或取消点赞
        const response = await commentApi.likeComment(
          comment.commentId, 
          userId, 
          isCommentLiked
        )

        if (response.data.code === 1) {
          // 切换点赞状态
          comment.isLiked = !isCommentLiked
          comment.likesCount = comment.isLiked ? (comment.likesCount || 0) + 1 : (comment.likesCount || 1) - 1
          ElMessage.success(comment.isLiked ? '点赞成功' : '取消点赞')
        } else {
          ElMessage.error('操作失败')
        }
      } catch (error) {
        console.error('评论点赞失败:', error)
        ElMessage.error('操作失败')
      }
    }

    // 聚焦评论输入框
    const focusCommentInput = () => {
      if (commentInputRef.value) {
        commentInputRef.value.focus()
      }
    }

    // 处理帖子操作
    const handlePostAction = (command) => {
      const { action, post } = command
      switch (action) {
        case 'collect':
          ElMessage.info('收藏功能开发中')
          break
        case 'report':
          ElMessage.info('举报功能开发中')
          break
      }
    }

    onMounted(() => {
      getPostDetail()
      getComments()
      getCurrentUser()
    })

    return {
      post,
      comments,
      commentContent,
      commentInputRef,
      currentUser,
      isLiked,
      formatTime,
      likePost,
      submitComment,
      replyToComment,
      submitReply,
      likeComment,
      focusCommentInput,
      handlePostAction
    }
  }
}
</script>

<style scoped>
.post-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.post-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 20px;
  border: 1px solid #e0e0e0;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.author-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 15px;
  border: 2px solid #2E8B57;
}

.author-details .author-name {
  font-weight: 600;
  font-size: 18px;
  color: #2E8B57;
}

.author-details .post-time {
  font-size: 14px;
  color: #888;
}

.post-title {
  font-size: 28px;
  margin: 20px 0;
  color: #333;
  font-weight: 600;
}

.post-text p {
  line-height: 1.8;
  margin: 15px 0;
  font-size: 16px;
  color: #444;
}

.post-media {
  margin: 20px 0;
  border-radius: 12px;
  overflow: hidden;
}

.post-media img {
  width: 100%;
  border-radius: 12px;
}

.post-stats {
  display: flex;
  gap: 30px;
  padding: 20px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  margin: 20px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #2E8B57;
  font-weight: 500;
}

.post-actions-bar {
  display: flex;
  gap: 30px;
  padding: 20px 0;
}

.post-actions-bar .el-button {
  color: #666;
  font-weight: 500;
}

.post-actions-bar .el-button:hover {
  color: #2E8B57;
}

.post-actions-bar .liked {
  color: #2E8B57;
}

.comments-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  border: 1px solid #e0e0e0;
}

.comments-section h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #2E8B57;
  font-size: 22px;
  border-bottom: 2px solid #2E8B57;
  padding-bottom: 10px;
}

.add-comment {
  margin-bottom: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 12px;
}

.comment-input-container {
  display: flex;
  gap: 15px;
}

.comment-input-wrapper {
  flex: 1;
}

.comment-input-wrapper .el-textarea {
  margin-bottom: 10px;
}

.comment-actions {
  text-align: right;
}

.comment-item {
  border-bottom: 1px solid #eee;
  padding: 20px 0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.comment-header .avatar {
  width: 40px;
  height: 40px;
  margin-right: 10px;
  border: 1px solid #2E8B57;
}

.comment-author {
  font-weight: 600;
  margin-right: 10px;
  color: #2E8B57;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  margin: 10px 0;
  font-size: 15px;
  line-height: 1.6;
}

.comment-actions {
  display: flex;
  gap: 15px;
}

.comment-actions .el-button {
  color: #666;
  font-size: 14px;
}

.comment-actions .el-button:hover {
  color: #2E8B57;
}

.comment-actions .liked {
  color: #2E8B57;
}

.reply-input {
  margin-top: 15px;
  padding: 15px;
  background: #f0f0f0;
  border-radius: 8px;
}

.reply-input .el-input {
  margin-bottom: 10px;
}

.reply-actions {
  text-align: right;
}

.reply-item {
  margin-top: 15px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.reply-author {
  font-weight: 500;
  font-size: 14px;
  color: #2E8B57;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  font-size: 14px;
}
</style>