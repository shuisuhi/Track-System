<template>
  <div class="profile-container">
    <div class="profile-header">
      <div class="profile-banner">
        <img src="../assets/profile-banner.jpg" alt="个人封面" />
      </div>
      <div class="profile-info">
        <div class="avatar-container">
          <img :src="user.avatarUrl || '../assets/default-avatar.png'" alt="头像" class="avatar" />
          <el-button 
            v-if="isCurrentUser" 
            class="edit-avatar-btn" 
            circle 
            @click="changeAvatar"
          >
            <el-icon><Camera /></el-icon>
          </el-button>
        </div>
        <div class="user-details">
          <h1>{{ user.username }}</h1>
          <p class="user-intro">{{ user.introduction || '暂无简介' }}</p>
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-number">{{ userPosts.length }}</span>
              <span class="stat-label">帖子</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ followersCount }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ followingCount }}</span>
              <span class="stat-label">关注</span>
            </div>
          </div>
          <div class="user-actions">
            <el-button 
              v-if="!isCurrentUser" 
              :type="isFollowing ? 'default' : 'primary'" 
              @click="toggleFollow"
            >
              {{ isFollowing ? '已关注' : '关注' }}
            </el-button>
            <el-button v-if="isCurrentUser" @click="editProfile">编辑资料</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="profile-content">
      <div class="profile-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="帖子" name="posts">
            <div class="posts-grid">
              <div 
                v-for="post in userPosts" 
                :key="post.postId" 
                class="post-item"
                @click="viewPostDetail(post)"
              >
                <div class="post-media">
                  <img :src="post.mediaUrl || '../assets/default-post.jpg'" alt="帖子图片" />
                </div>
                <div class="post-overlay">
                  <div class="post-stats">
                    <div class="stat">
                      <el-icon><Star /></el-icon>
                      <span>{{ post.likesCount || 0 }}</span>
                    </div>
                    <div class="stat">
                      <el-icon><ChatLineRound /></el-icon>
                      <span>{{ post.commentsCount || 0 }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="收藏" name="collections">
            <div class="collections-empty">
              <el-icon size="48"><Star /></el-icon>
              <p>暂无收藏内容</p>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑资料" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input 
            v-model="editForm.introduction" 
            type="textarea" 
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 更换头像对话框 -->
    <el-dialog v-model="avatarDialogVisible" title="更换头像" width="500px">
      <div class="avatar-upload">
        <el-upload
          class="avatar-uploader"
          :auto-upload="false"
          :on-change="handleAvatarChange"
          :show-file-list="false"
        >
          <img v-if="tempAvatar" :src="tempAvatar" class="avatar-preview" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="avatarDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAvatar" :disabled="!tempAvatar">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi, postApi, followApi } from '../utils/api'

export default {
  name: 'Profile',
  props: {
    id: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const route = useRoute()
    const router = useRouter()
    const user = ref({})
    const userPosts = ref([])
    const followersCount = ref(0)
    const followingCount = ref(0)
    const isFollowing = ref(false)
    const isCurrentUser = ref(false)
    const activeTab = ref('posts')
    const editDialogVisible = ref(false)
    const avatarDialogVisible = ref(false)
    const tempAvatar = ref('')
    const selectedAvatarFile = ref(null)

    // 编辑表单数据
    const editForm = reactive({
      username: '',
      introduction: ''
    })

    // 获取用户信息
    const getUserInfo = async () => {
      try {
        const response = await userApi.getUserById(props.id)
        if (response.data.code === 50) {
          user.value = response.data.data
          editForm.username = user.value.username
          editForm.introduction = user.value.introduction
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败')
      }
    }

    // 获取用户帖子
    const getUserPosts = async () => {
      try {
        const response = await postApi.getUserWorks(props.id, 1)
        if (response.data.code === 1) {
          userPosts.value = response.data.data || []
        }
      } catch (error) {
        console.error('获取用户帖子失败:', error)
        ElMessage.error('获取用户帖子失败')
      }
    }

    // 检查是否已关注
    const checkFollowStatus = async () => {
      const currentUserId = localStorage.getItem('userId')
      if (currentUserId && currentUserId !== props.id) {
        try {
          const response = await followApi.isFollowing(currentUserId, props.id)
          isFollowing.value = response.data.code === 1
        } catch (error) {
          console.error('检查关注状态失败:', error)
        }
      }
    }

    // 获取关注和粉丝数量
    const getFollowCounts = async () => {
      try {
        // 获取粉丝数量
        const followersResponse = await followApi.getFollowers(props.id, 1, 1)
        if (followersResponse.data.code === 1) {
          followersCount.value = followersResponse.data.data.total || 0
        }

        // 获取关注数量
        const followsResponse = await followApi.getFollows(props.id)
        if (followsResponse.data.code === 1) {
          followingCount.value = followsResponse.data.data.length || 0
        }
      } catch (error) {
        console.error('获取关注数据失败:', error)
      }
    }

    // 切换关注状态
    const toggleFollow = async () => {
      const currentUserId = localStorage.getItem('userId')
      if (!currentUserId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      try {
        const response = await followApi.followUser(currentUserId, props.id)
        if (response.data.code === 1) {
          isFollowing.value = !isFollowing.value
          // 更新粉丝数量
          if (isFollowing.value) {
            followersCount.value++
          } else {
            followersCount.value = Math.max(0, followersCount.value - 1)
          }
          ElMessage.success(isFollowing.value ? '关注成功' : '取消关注成功')
        } else {
          ElMessage.error('操作失败')
        }
      } catch (error) {
        console.error('关注操作失败:', error)
        ElMessage.error('操作失败')
      }
    }

    // 编辑资料
    const editProfile = () => {
      editDialogVisible.value = true
    }

    // 保存资料
    const saveProfile = async () => {
      try {
        const userData = {
          userId: user.value.userId,
          username: editForm.username,
          introduction: editForm.introduction
        }

        const response = await userApi.updateUser(userData)
        if (response.data.code === 1) {
          ElMessage.success('保存成功')
          editDialogVisible.value = false
          // 更新用户信息
          user.value.username = editForm.username
          user.value.introduction = editForm.introduction
        } else {
          ElMessage.error(response.data.message || '保存失败')
        }
      } catch (error) {
        console.error('保存资料失败:', error)
        ElMessage.error('保存失败')
      }
    }

    // 更换头像
    const changeAvatar = () => {
      avatarDialogVisible.value = true
    }

    // 处理头像文件选择
    const handleAvatarChange = (file) => {
      selectedAvatarFile.value = file.raw
      tempAvatar.value = URL.createObjectURL(file.raw)
    }

    // 保存头像
    const saveAvatar = async () => {
      if (!selectedAvatarFile.value) {
        ElMessage.warning('请选择头像图片')
        return
      }

      try {
        // 在实际应用中，这里应该上传文件到服务器
        // 为了简化示例，我们直接使用临时URL
        const avatarUrl = tempAvatar.value
        
        const response = await userApi.uploadAvatar(user.value.userId, avatarUrl)
        if (response.data.code === 1) {
          ElMessage.success('头像更新成功')
          avatarDialogVisible.value = false
          user.value.avatarUrl = avatarUrl
          tempAvatar.value = ''
          selectedAvatarFile.value = null
        } else {
          ElMessage.error(response.data.message || '头像更新失败')
        }
      } catch (error) {
        console.error('头像更新失败:', error)
        ElMessage.error('头像更新失败')
      }
    }

    // 查看帖子详情
    const viewPostDetail = (post) => {
      router.push(`/post/${post.postId}`)
    }

    onMounted(() => {
      const currentUserId = localStorage.getItem('userId')
      isCurrentUser.value = currentUserId === props.id
      
      getUserInfo()
      getUserPosts()
      getFollowCounts()
      
      if (!isCurrentUser.value) {
        checkFollowStatus()
      }
    })

    return {
      user,
      userPosts,
      followersCount,
      followingCount,
      isFollowing,
      isCurrentUser,
      activeTab,
      editDialogVisible,
      avatarDialogVisible,
      tempAvatar,
      editForm,
      toggleFollow,
      editProfile,
      saveProfile,
      changeAvatar,
      handleAvatarChange,
      saveAvatar,
      viewPostDetail
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.profile-header {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
}

.profile-banner img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.profile-info {
  display: flex;
  padding: 20px;
}

.avatar-container {
  position: relative;
  margin-right: 30px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 4px solid #fff;
  margin-top: -50px;
  background: #fff;
}

.edit-avatar-btn {
  position: absolute;
  bottom: 10px;
  right: 0;
  background: rgba(255, 255, 255, 0.8);
}

.user-details {
  flex: 1;
}

.user-details h1 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.user-intro {
  color: #666;
  margin-bottom: 20px;
}

.user-stats {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 18px;
  font-weight: 500;
  display: block;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.profile-content {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.post-item {
  position: relative;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
}

.post-media img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
}

.post-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.post-item:hover .post-overlay {
  opacity: 1;
}

.post-stats {
  display: flex;
  gap: 20px;
  color: #fff;
}

.stat {
  display: flex;
  align-items: center;
  gap: 5px;
}

.collections-empty {
  text-align: center;
  padding: 50px 0;
  color: #999;
}

.avatar-uploader .avatar-preview {
  width: 178px;
  height: 178px;
  display: block;
  border-radius: 50%;
}

.avatar-uploader .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
}
</style>