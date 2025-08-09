import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api', // 使用Vite代理
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 从localStorage获取token并添加到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // token过期或无效，清除本地存储并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 用户相关API
export const userApi = {
  // 用户登录
  login: (userData) => {
    return api.post('/user/login', userData)
  },

  // 用户注册
  register: (userData) => {
    return api.post('/user/signup', userData)
  },

  // 根据用户ID获取用户信息
  getUserById: (userId) => {
    return api.get(`/user/user?userId=${userId}`)
  },

  // 更新用户信息
  updateUser: (userData) => {
    return api.post('/user/update', userData)
  },

  // 上传用户头像
  uploadAvatar: (userId, avatarUrl) => {
    return api.post('/user/user/avatar', { userId, avatarUrl })
  }
}

// 帖子相关API
export const postApi = {
  // 创建帖子
  createPost: (postData) => {
    return api.post('/user/post', postData)
  },

  // 更新帖子
  updatePost: (postData) => {
    return api.post('/user/post/update', postData)
  },

  // 获取所有帖子
  getAllPosts: () => {
    return api.get('/user/getallpost')
  },

  // 根据帖子ID获取帖子
  getPostById: (postId) => {
    return api.get(`/user/getpost?postId=${postId}`)
  },

  // 根据用户ID获取帖子
  getPostsByUserId: (userId, page, pageSize) => {
    return api.get(`/user/getposts?userId=${userId}&page=${page}&pageSize=${pageSize}`)
  },

  // 获取用户作品
  getUserWorks: (userId, audi) => {
    return api.get(`/user/works?userId=${userId}&audi=${audi}`)
  },

  // 帖子点赞
  likePost: (postId, userId) => {
    return api.post('/user/postlike', { postId, userId })
  },

  // 检查帖子是否已点赞
  isPostLiked: (postId, userId) => {
    return api.post('/user/ispostliked', { postId, userId })
  }
}

// 评论相关API
export const commentApi = {
  // 创建评论
  createComment: (commentData) => {
    return api.post('/user/comment', commentData)
  },

  // 根据帖子ID获取评论
  getCommentsByPostId: (postId, page, pageSize) => {
    return api.get(`/user/comment?postId=${postId}&page=${page}&pageSize=${pageSize}`)
  },

  // 评论点赞
  likeComment: (commentId, userId, commentLiked) => {
    return api.post('/user/commentlike', { commentId, userId, commentLiked })
  },

  // 检查评论是否已点赞
  isCommentLiked: (commentId, userId) => {
    return api.get(`/user/iscommentliked?commentId=${commentId}&userId=${userId}`)
  }
}

// 回复相关API
export const replyApi = {
  // 创建回复
  createReply: (replyData) => {
    return api.post('/user/reply', replyData)
  },

  // 根据评论ID获取回复
  getRepliesByCommentId: (commentId) => {
    return api.get(`/user/reply?commentId=${commentId}`)
  }
}

// 关注相关API
export const followApi = {
  // 关注用户
  followUser: (userId, beFollowedId) => {
    return api.post('/user/follow', { userId, beFollowedId })
  },

  // 检查是否已关注
  isFollowing: (userId, beFollowedId) => {
    return api.post('/user/isfollowed', { userId, beFollowedId })
  },

  // 获取关注列表
  getFollows: (userId) => {
    return api.post('/user/getfollows', { userId })
  },

  // 获取粉丝列表
  getFollowers: (userId, page, pageSize) => {
    return api.post('/user/getfollowers', { userId, page, pageSize })
  }
}

// 文件上传相关API
export const uploadApi = {
  // 上传文件
  uploadFile: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/user/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export default {
  userApi,
  postApi,
  commentApi,
  replyApi,
  followApi,
  uploadApi
}