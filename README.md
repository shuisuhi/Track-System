# Track-System

Track-System 是一个徒步路线分享平台，同时具备基于多Agent架构的AI助手系统，为徒步旅行爱好者提供分享和推荐服务。

## 🏗️ 多智能体架构

```mermaid
graph TD
    A[用户请求] --> B[总协调Agent]
    B --> C[意图分析]
    C --> D[Agent选择]
    D --> E[徒步专家Agent]
    D --> F[天气查询Agent]
    D --> G[路线规划Agent]
    D --> H[装备推荐Agent]
    E --> I[响应整合]
    F --> I
    G --> I
    H --> I
    I --> J[结构化响应]
    J --> K[返回用户]
```

## 🚀 功能模块

### 1. 路线帖子
包括帖子的发布、评论、点赞、收藏等功能，可以实现基于关键词的检索。

### 2. 多agent系统
具备用户意图分析与多agent协同功能

## 🖼️ 界面展示

### 首页
![首页](source/首页.png)

### 对话界面
![对话](source/对话.gif)

### 发布页面
![发布](source/发布.png)