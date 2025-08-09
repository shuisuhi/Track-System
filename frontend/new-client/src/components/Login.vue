<template>
  <div class="login-container">
    <div class="login-form">
      <h2>登录</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="用户名" 
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码" 
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            :loading="loading"
            class="login-button"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '../utils/api'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const loginFormRef = ref(null)
    const loading = ref(false)

    // 登录表单数据
    const loginForm = reactive({
      username: '',
      password: ''
    })

    // 表单验证规则
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ]
    }

    // 处理登录
    const handleLogin = async () => {
      try {
        // 表单验证
        await loginFormRef.value.validate()
        
        // 显示加载状态
        loading.value = true
        
        // 调用登录API
        const response = await userApi.login(loginForm)
        
        if (response.data.code === 20) {
          // 登录成功
          const { token, userId } = response.data.data
          
          // 保存token和用户ID到localStorage
          localStorage.setItem('token', token)
          localStorage.setItem('userId', userId)
          
          ElMessage.success('登录成功')
          
          // 跳转到首页
          router.push('/')
        } else {
          // 登录失败
          ElMessage.error(response.data.message || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error('登录失败，请稍后重试')
      } finally {
        // 隐藏加载状态
        loading.value = false
      }
    }

    return {
      loginForm,
      rules,
      loginFormRef,
      loading,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 120px);
  background-color: #f5f5f5;
}

.login-form {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-button {
  width: 100%;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.register-link a {
  color: #ff6b6b;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>