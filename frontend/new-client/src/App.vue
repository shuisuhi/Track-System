<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="container">
        <div class="logo">
          <img src="./assets/logo.png" alt="Track" />
                    <span>Track</span>
        </div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/create">发布</router-link>
        </nav>
        <div class="user-actions">
          <template v-if="isLoggedIn">
            <el-dropdown @command="handleUserCommand">
              <span class="el-dropdown-link">
                <img :src="user.avatar || './assets/default-avatar.png'" alt="头像" class="avatar" />
                {{ user.username }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="main">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="container">
        <p>&copy; 2025 Track. 保留所有权利.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from './utils/api'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const isLoggedIn = ref(false)
    const user = ref({})

    // 检查用户是否已登录
    const checkLoginStatus = async () => {
      const token = localStorage.getItem('token')
      if (token) {
        try {
          // 这里可以调用API验证token有效性
          isLoggedIn.value = true
          // 获取用户信息
          const userId = localStorage.getItem('userId')
          if (userId) {
            const response = await api.getUserById(userId)
            user.value = response.data
          }
        } catch (error) {
          console.error('验证token失败:', error)
          logout()
        }
      }
    }

    // 用户下拉菜单命令处理
    const handleUserCommand = (command) => {
      switch (command) {
        case 'profile':
          router.push(`/profile/${user.value.userId}`)
          break
        case 'logout':
          logout()
          break
      }
    }

    // 退出登录
    const logout = () => {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      isLoggedIn.value = false
      user.value = {}
      router.push('/login')
      ElMessage.success('已退出登录')
    }

    onMounted(() => {
      checkLoginStatus()
    })

    return {
      isLoggedIn,
      user,
      handleUserCommand
    }
  }
}
</script>

<style scoped>
.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  background: linear-gradient(to right, #2E8B57, #8B4513);
  color: white;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo img {
  height: 30px;
  margin-right: 10px;
}

.logo span {
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.nav a {
  margin: 0 15px;
  text-decoration: none;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  padding: 5px 10px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.nav a:hover {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.user-actions {
  display: flex;
  align-items: center;
}

.user-actions a {
  margin: 0 10px;
  text-decoration: none;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  padding: 5px 10px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.user-actions a:hover {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 8px;
  border: 2px solid white;
}

.main {
  min-height: calc(100vh - 120px);
  padding: 20px 0;
}

.footer {
  background-color: #2E8B57;
  color: white;
  padding: 20px 0;
  text-align: center;
  border-top: 1px solid #e9ecef;
}
</style>