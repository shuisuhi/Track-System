<template>
  <div class="create-post-container">
    <div class="create-post-header">
      <h2>发布帖子</h2>
      <el-button 
        type="primary" 
        @click="submitPost" 
        :loading="submitting"
        :disabled="!isFormValid"
      >
        发布
      </el-button>
    </div>

    <div class="create-post-content">
      <div class="post-form">
        <el-form :model="postForm" :rules="rules" ref="postFormRef">
          <el-form-item prop="title">
            <el-input 
              v-model="postForm.title" 
              placeholder="输入标题..." 
              size="large"
              clearable
            />
          </el-form-item>
          
          <el-form-item prop="content">
            <el-input 
              v-model="postForm.content" 
              type="textarea" 
              :rows="8" 
              placeholder="分享你的想法..." 
              size="large"
            />
          </el-form-item>
        </el-form>
      </div>

      <div class="media-upload-section">
        <h3>添加图片/视频</h3>
        <div class="media-preview" v-if="mediaPreview">
          <img :src="mediaPreview" alt="预览图片" />
          <el-button 
            type="danger" 
            circle 
            @click="removeMedia"
            class="remove-media-btn"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
        <el-upload
          v-else
          class="upload-area"
          drag
          :auto-upload="false"
          :on-change="handleMediaChange"
          :show-file-list="false"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              jpg/png/mp4文件，且不超过10MB
            </div>
          </template>
        </el-upload>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postApi, uploadApi } from '../utils/api'

export default {
  name: 'CreatePost',
  setup() {
    const router = useRouter()
    const postFormRef = ref(null)
    const submitting = ref(false)
    const selectedMedia = ref(null)
    const mediaPreview = ref('')

    // 帖子表单数据
    const postForm = reactive({
      title: '',
      content: ''
    })

    // 表单验证规则
    const rules = {
      title: [
        { required: true, message: '请输入标题', trigger: 'blur' },
        { max: 50, message: '标题长度不能超过50个字符', trigger: 'blur' }
      ],
      content: [
        { required: true, message: '请输入内容', trigger: 'blur' },
        { min: 10, message: '内容长度至少10个字符', trigger: 'blur' }
      ]
    }

    // 表单是否有效
    const isFormValid = computed(() => {
      return postForm.title.trim() && postForm.content.trim().length >= 10
    })

    // 处理媒体文件选择
    const handleMediaChange = (file) => {
      // 验证文件类型
      const validTypes = ['image/jpeg', 'image/png', 'video/mp4']
      if (!validTypes.includes(file.raw.type)) {
        ElMessage.error('只支持jpg、png、mp4格式的文件')
        return
      }

      // 验证文件大小
      if (file.raw.size > 10 * 1024 * 1024) {
        ElMessage.error('文件大小不能超过10MB')
        return
      }

      selectedMedia.value = file.raw
      mediaPreview.value = URL.createObjectURL(file.raw)
    }

    // 移除媒体文件
    const removeMedia = () => {
      selectedMedia.value = null
      mediaPreview.value = ''
    }

    // 提交帖子
    const submitPost = async () => {
      // 表单验证
      try {
        await postFormRef.value.validate()
      } catch (error) {
        ElMessage.warning('请检查表单填写是否正确')
        return
      }

      // 检查是否已登录
      const userId = localStorage.getItem('userId')
      if (!userId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      try {
        submitting.value = true

        // 准备帖子数据
        const postData = {
          title: postForm.title,
          content: postForm.content,
          userId: parseInt(userId),
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          audi: 1 // 默认审核状态
        }

        // 如果有媒体文件，先上传
        if (selectedMedia.value) {
          try {
            const uploadResponse = await uploadApi.uploadFile(selectedMedia.value)
            if (uploadResponse.data.code === 1) {
              // 将上传的URL存储在url字段中，并确保它是一个数组
              postData.url = [uploadResponse.data.data]
            } else {
              throw new Error('文件上传失败')
            }
          } catch (uploadError) {
            console.error('文件上传失败:', uploadError)
            ElMessage.error('文件上传失败')
            return
          }
        } else {
          // 如果没有媒体文件，确保url字段是一个空数组而不是null
          postData.url = []
        }

        // 提交帖子
        const response = await postApi.createPost(postData)
        if (response.data.code === 40) {
          ElMessage.success('发布成功')
          // 清空表单
          postForm.title = ''
          postForm.content = ''
          removeMedia()
          // 跳转到首页
          router.push('/')
        } else {
          ElMessage.error(response.data.message || '发布失败')
        }
      } catch (error) {
        console.error('发布失败:', error)
        ElMessage.error('发布失败，请稍后重试')
      } finally {
        submitting.value = false
      }
    }

    return {
      postForm,
      rules,
      postFormRef,
      submitting,
      mediaPreview,
      isFormValid,
      handleMediaChange,
      removeMedia,
      submitPost
    }
  }
}
</script>

<style scoped>
.create-post-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.create-post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.create-post-header h2 {
  margin: 0;
}

.create-post-content {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.post-form {
  margin-bottom: 30px;
}

.media-upload-section h3 {
  margin-top: 0;
  margin-bottom: 20px;
}

.upload-area {
  width: 100%;
}

.media-preview {
  position: relative;
  display: inline-block;
}

.media-preview img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
}

.remove-media-btn {
  position: absolute;
  top: 10px;
  right: 10px;
}
</style>