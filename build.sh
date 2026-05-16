#!/bin/bash
# Android 项目构建脚本
# 用于快速配置环境并构建项目

echo "配置开发环境..."

# 设置 Java 17
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
echo "JAVA_HOME=$JAVA_HOME"

# 设置 Android SDK
export ANDROID_HOME=/root/.android/sdk
export ANDROID_SDK_ROOT=/root/.android/sdk
echo "ANDROID_HOME=$ANDROID_HOME"

# 更新 PATH
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

# 使用 mise 管理的 Gradle
GRADLE_BIN=/root/.local/share/mise/installs/gradle/8.14.4/gradle-8.14.4/bin/gradle

echo "PATH 已更新"
echo "Gradle 路径: $GRADLE_BIN"
echo ""

# 检查 Gradle 版本
echo "检查 Gradle 版本..."
$GRADLE_BIN --version

echo ""
echo "开始构建项目..."
echo ""

# 执行构建命令
if [ $# -eq 0 ]; then
    # 默认为列出任务
    $GRADLE_BIN tasks --no-daemon
else
    # 执行用户提供的命令
    $GRADLE_BIN "$@" --no-daemon
fi
