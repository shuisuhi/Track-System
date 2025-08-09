// API测试工具
// 用于测试后端API接口是否正常工作

import { userApi, postApi, commentApi, followApi } from './api'

// 测试用户相关API
export const testUserApi = async () => {
  console.log('开始测试用户相关API...')
  
  try {
    // 测试用户登录
    console.log('测试用户登录...')
    const loginResponse = await userApi.login({
      username: 'testuser',
      password: 'testpassword'
    })
    console.log('登录响应:', loginResponse)
    
    // 测试用户注册
    console.log('测试用户注册...')
    const registerResponse = await userApi.register({
      username: 'newuser',
      password: 'newpassword'
    })
    console.log('注册响应:', registerResponse)
    
    // 测试获取用户信息
    console.log('测试获取用户信息...')
    const userResponse = await userApi.getUserById(1)
    console.log('用户信息响应:', userResponse)
    
    console.log('用户相关API测试完成')
  } catch (error) {
    console.error('用户相关API测试失败:', error)
  }
}

// 测试帖子相关API
export const testPostApi = async () => {
  console.log('开始测试帖子相关API...')
  
  try {
    // 测试创建帖子
    console.log('测试创建帖子...')
    const createPostResponse = await postApi.createPost({
      title: '测试帖子',
      content: '这是一个测试帖子的内容',
      userId: 1
    })
    console.log('创建帖子响应:', createPostResponse)
    
    // 测试获取所有帖子
    console.log('测试获取所有帖子...')
    const allPostsResponse = await postApi.getAllPosts()
    console.log('所有帖子响应:', allPostsResponse)
    
    // 测试获取帖子详情
    console.log('测试获取帖子详情...')
    const postDetailResponse = await postApi.getPostById(1)
    console.log('帖子详情响应:', postDetailResponse)
    
    console.log('帖子相关API测试完成')
  } catch (error) {
    console.error('帖子相关API测试失败:', error)
  }
}

// 测试评论相关API
export const testCommentApi = async () => {
  console.log('开始测试评论相关API...')
  
  try {
    // 测试创建评论
    console.log('测试创建评论...')
    const createCommentResponse = await commentApi.createComment({
      postId: 1,
      userId: 1,
      content: '这是一条测试评论'
    })
    console.log('创建评论响应:', createCommentResponse)
    
    // 测试获取评论
    console.log('测试获取评论...')
    const commentsResponse = await commentApi.getCommentsByPostId(1, 1, 10)
    console.log('评论列表响应:', commentsResponse)
    
    console.log('评论相关API测试完成')
  } catch (error) {
    console.error('评论相关API测试失败:', error)
  }
}

// 测试关注相关API
export const testFollowApi = async () => {
  console.log('开始测试关注相关API...')
  
  try {
    // 测试关注用户
    console.log('测试关注用户...')
    const followResponse = await followApi.followUser(1, 2)
    console.log('关注用户响应:', followResponse)
    
    // 测试检查关注状态
    console.log('测试检查关注状态...')
    const followStatusResponse = await followApi.isFollowing(1, 2)
    console.log('关注状态响应:', followStatusResponse)
    
    console.log('关注相关API测试完成')
  } catch (error) {
    console.error('关注相关API测试失败:', error)
  }
}

// 运行所有测试
export const runAllTests = async () => {
  console.log('开始运行所有API测试...')
  
  await testUserApi()
  await testPostApi()
  await testCommentApi()
  await testFollowApi()
  
  console.log('所有API测试完成')
}

export default {
  testUserApi,
  testPostApi,
  testCommentApi,
  testFollowApi,
  runAllTests
}