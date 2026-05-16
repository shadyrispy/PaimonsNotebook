# 开发环境配置说明

## 已通过 mise 管理的工具

### 1. Java 17
- 路径: `/root/.local/share/mise/installs/java/17.0.2`
- 版本: OpenJDK 17.0.2

### 2. Gradle
- 路径: `/root/.local/share/mise/installs/gradle/8.14.4/gradle-8.14.4/bin/gradle`
- 版本: Gradle 8.14.4 (满足项目要求的 8.9+)

## 项目配置文件

### .tool-versions
指定项目使用的工具版本：
```
java 17.0.2
gradle 8.14.4
```

### settings.gradle
已配置阿里云 Maven 镜像加速：
- `https://maven.aliyun.com/repository/google`
- `https://maven.aliyun.com/repository/public`
- `https://maven.aliyun.com/repository/gradle-plugin`

### local.properties
包含基本配置和 AppCenter 密钥（本地构建为空）

## 构建命令

使用 mise 管理的环境构建项目：
```bash
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
/root/.local/share/mise/installs/gradle/8.14.4/gradle-8.14.4/bin/gradle build
```

或使用项目自带的 gradlew（已移除远程下载依赖）：
```bash
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
./gradlew build
```

## 注意事项

1. **Android SDK**: 当前环境尚未安装 Android SDK，需要额外安装才能完成构建
2. **网络连接**: 已配置阿里云镜像，如遇超时可尝试其他镜像源
3. **Gradle 版本**: 使用 8.14.4 比项目要求的 8.9 更新，应该兼容

## 后续步骤

如需完成构建，请安装 Android SDK：
- 推荐使用 Android Studio
- 或使用 command line tools: https://developer.android.com/studio#command-line-tools-only
