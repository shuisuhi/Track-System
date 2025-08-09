# 创意工坊 - 类似小红书的社交平台

这是一个使用Vue 3和Element Plus构建的类似小红书的社交平台前端应用。

## 功能特性

- 用户注册和登录（无需邮箱验证）
- 发布帖子（支持文字和图片/视频）
- 浏览信息流
- 点赞和评论功能
- 个人资料页面
- 关注用户

## 技术栈

- Vue 3 (Composition API)
- Vue Router
- Element Plus UI组件库
- Axios (HTTP客户端)
- Vite (构建工具)

## 启动项目

### 1. 安装依赖

```bash
npm install
```

如果遇到依赖问题，可以尝试以下命令：

```bash
# 清理依赖
npm cache clean --force
rm -rf node_modules package-lock.json

# 重新安装依赖
npm install
```

### 2. 启动开发服务器

#### 方法一：使用npm命令
```bash
npm run dev
```

#### 方法二：使用启动脚本
- Windows用户：双击 `start.bat` 文件
- Linux/Mac用户：运行 `./start.sh` 命令

默认情况下，应用将在 `http://localhost:3000` 上运行。

### 3. 解决安全漏洞

如果npm audit报告安全漏洞，可以运行以下命令修复：

```bash
npm audit fix --force
```

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产构建

```bash
npm run serve
```

## 项目结构

```
src/
├── assets/                 # 静态资源文件
│   ├── logo.png           # 应用Logo
│   ├── default-avatar.png # 默认用户头像
│   ├── default-post.jpg   # 默认帖子图片
│   ├── profile-banner.jpg # 个人资料页面封面
│   └── styles.css         # 全局样式
├── components/             # Vue组件
│   ├── Login.vue          # 登录页面
│   ├── Register.vue       # 注册页面
│   ├── Home.vue           # 主页/信息流
│   ├── Profile.vue        # 个人资料页面
│   ├── CreatePost.vue     # 发布帖子页面
│   └── PostDetail.vue     # 帖子详情页面
├── utils/                 # 工具函数
│   └── api.js            # API接口封装
├── App.vue               # 根组件
└── main.js               # 应用入口文件
```

## 静态资源说明

项目包含以下静态资源：

- `logo.png`: 应用Logo
- `default-avatar.png`: 默认用户头像
- `default-post.jpg`: 默认帖子图片
- `profile-banner.jpg`: 个人资料页面封面
- `styles.css`: 全局样式文件

## 版本控制

项目包含 `.gitignore` 文件，用于忽略以下内容：

- `node_modules/`: 依赖包目录
- `dist/`: 构建输出目录
- 日志文件
- 操作系统生成的文件（如.DS_Store）
- IDE配置文件
- 环境配置文件
- Vite缓存目录

## API代理配置

项目使用Vite的代理功能将API请求转发到后端服务器。默认配置如下：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8123',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '')
  }
}
```

请确保后端服务在 `http://localhost:8123` 上运行。

## 开发指南

### 添加新功能

1. 在 `src/components/` 目录下创建新的Vue组件
2. 在 `src/main.js` 中添加路由配置
3. 如果需要新的API接口，在 `src/utils/api.js` 中添加相应方法

### 代码检查

项目集成了ESLint用于代码检查：

```bash
# 检查代码
npm run lint

# 自动修复代码问题
npm run lint:fix
```

### API测试

项目包含API测试工具，用于测试后端接口：

```bash
# 在浏览器控制台中运行测试
# 导入测试工具
import { runAllTests } from './src/utils/test-api.js'

# 运行所有测试
runAllTests()
```

测试文件位于 `src/utils/test-api.js`，包含以下测试：
- 用户相关API测试
- 帖子相关API测试
- 评论相关API测试
- 关注相关API测试

### 自定义样式

- 全局样式可以在 `src/App.vue` 中的 `<style>` 标签中定义
- 组件特定样式在各自的Vue文件中的 `<style scoped>` 标签中定义

## 注意事项

1. 用户注册时不需要邮箱验证
2. 帖子可以包含文字和媒体文件（图片/视频）
3. 用户可以关注其他用户并查看关注用户的帖子
4. 所有用户操作都需要登录状态

## 后续可扩展功能

- 消息通知系统
- 搜索功能
- 标签系统
- 收藏功能
- 举报功能
- 更丰富的个人资料设置

## 部署

项目包含完整的部署指南，请查看 [DEPLOYMENT.md](DEPLOYMENT.md) 文件了解详细信息。

支持以下部署方式：
- 静态文件服务器部署
- Nginx部署
- Docker部署

## 变更日志

项目的变更历史记录在 [CHANGELOG.md](CHANGELOG.md) 文件中。

## 待办事项

项目的未来计划记录在 [TODO.md](TODO.md) 文件中。

## 故障排除

如果在开发过程中遇到问题，请查看 [TROUBLESHOOTING.md](TROUBLESHOOTING.md) 文件了解解决方案。