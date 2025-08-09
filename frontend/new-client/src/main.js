import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/styles.css'
import App from './App.vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import Home from './components/Home.vue'
import Profile from './components/Profile.vue'
import CreatePost from './components/CreatePost.vue'
import PostDetail from './components/PostDetail.vue'
import AiChat from './components/AiChat.vue'

// 定义路由
const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/profile/:id', component: Profile, props: true },
  { path: '/create', component: CreatePost },
  { path: '/post/:id', component: PostDetail, props: true },
  { path: '/ai-chat', component: AiChat }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 创建Vue应用实例
const app = createApp(App)

// 使用Element Plus
app.use(ElementPlus)

// 使用路由
app.use(router)

// 挂载应用
app.mount('#app')