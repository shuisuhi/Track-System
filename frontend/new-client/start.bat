@echo off
echo 正在启动创意工坊前端应用...
echo.

echo 正在检查Node.js是否已安装...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Node.js，请先安装Node.js
    pause
    exit /b 1
)

echo 正在检查npm是否已安装...
npm --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到npm，请先安装Node.js (包含npm)
    pause
    exit /b 1
)

echo 正在安装项目依赖...
npm install
if %errorlevel% neq 0 (
    echo 错误: 依赖安装失败
    pause
    exit /b 1
)

echo 正在启动开发服务器...
echo 应用将在 http://localhost:3000 上运行
npm run dev