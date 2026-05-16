# 开发环境配置说明

## 已通过 mise 管理的工具

### 1. Java 17
- 路径: `/root/.local/share/mise/installs/java/17.0.2
- 版本: OpenJDK 17.0.2

### 2. Gradle
- 路径: `/root/.local/share/mise/installs/gradle/8.14.4/gradle-8.14.4/bin/gradle
- 版本: Gradle 8.14.4 (满足项目要求的 8.9+)

## 手动安装的 Android SDK
- Android SDK 路径: `/root/.android/sdk
- Android SDK Command-line Tools 已安装
- 已安装的组件：
  - platform-tools
  - platforms;android-34
  - build-tools;34.0.0

## 项目配置文件

### .tool-versions
指定项目使用的工具版本：
```
java 17.0.2
gradle 8.14.4
```

### settings.gradle
已配置阿里云 Maven 镜像加速：
- `https://maven.aliyun.com/repository/google
- `https://maven.aliyun.com/repository/public
- `https://maven.aliyun.com/repository/gradle-plugin

### local.properties
包含 Android SDK 路径和 AppCenter 密钥（本地构建为空）：
```properties
sdk.dir=/root/.android/sdk
appcenter.secret=
```

## 构建命令

使用 mise 管理的环境构建项目：
```bash
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export ANDROID_HOME=/root/.android/sdk
export ANDROID_SDK_ROOT=/root/.android/sdk
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH
/root/.local/share/mise/installs/gradle/8.14.4/gradle-8.14.4/bin/gradle build
```

或使用项目自带的 gradlew：
```bash
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export ANDROID_HOME=/root/.android/sdk
export ANDROID_SDK_ROOT=/root/.android/sdk
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH
./gradlew build
```

## 关于 mise-android-sdk 插件
虽然安装了插件，但由于 yq 版本兼容性问题暂时无法使用，未来可以尝试：
- 使用更新版本的 yq
- 或者继续使用当前手动安装的 Android SDK

## 注意事项
1. **网络连接**: 已配置阿里云镜像，如遇超时可尝试其他镜像源
2. **Gradle 版本**: 使用 8.14.4 比项目要求的 8.9 更新，应该兼容
3. **Android SDK**: 当前环境已安装完整 Android SDK，可以支持项目构建
