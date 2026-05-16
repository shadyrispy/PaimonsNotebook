# Learnings

Corrections, insights, and knowledge gaps captured during development.

**Categories**: correction | insight | knowledge_gap | best_practice

---

## [LRN-20260516-001] best_practice

**Logged**: 2026-05-16T00:54:00Z
**Priority**: high
**Status**: pending
**Area**: infra

### Summary
Android 项目 Gradle 构建需要同时配置 Java、Gradle 和 Maven 镜像源

### Details
在配置 Android 项目开发环境时，需要确保：
1. Java 版本匹配（项目要求 17）
2. Gradle 版本满足要求（项目要求 8.9+，实际安装了 8.14.4）
3. Maven 依赖仓库配置国内镜像加速（阿里云镜像）
4. Android SDK 路径配置

### Suggested Action
对于 Android 项目，使用 mise 管理 Java 和 Gradle 版本，在 settings.gradle 中配置阿里云镜像源

### Metadata
- Source: error
- Related Files: settings.gradle, build.gradle, .tool-versions
- Tags: android, gradle, mise, mirror
- Pattern-Key: android.gradle.setup
- Recurrence-Count: 1
- First-Seen: 2026-05-16
- Last-Seen: 2026-05-16

---

## [LRN-20260516-002] best_practice

**Logged**: 2026-05-16T00:54:00Z
**Priority**: medium
**Status**: pending
**Area**: infra

### Summary
Gradle wrapper 配置中的 distributionUrl 为空会导致构建失败

### Details
在移除 gradle-wrapper.properties 中的 distributionUrl 后，直接使用系统安装的 Gradle 时，
如果配置不当可能导致构建失败。应该确保 gradlew 脚本正确调用系统 Gradle。

### Suggested Action
对于使用 mise 等工具管理 Gradle 的项目，可以：
1. 保留 gradle-wrapper.properties 但注释掉 distributionUrl
2. 或直接使用系统 Gradle 而不使用 gradlew

### Metadata
- Source: error
- Related Files: gradle-wrapper.properties
- Tags: gradle, wrapper
- Pattern-Key: gradle.wrapper.config
- Recurrence-Count: 1
- First-Seen: 2026-05-16
- Last-Seen: 2026-05-16

---

## [LRN-20260516-003] insight

**Logged**: 2026-05-16T00:54:00Z
**Priority**: high
**Status**: pending
**Area**: infra

### Summary
mise 工具可以有效管理 Java 和 Gradle 多版本环境

### Details
mise 是一个现代化的运行时管理器，可以：
- 管理多个版本的 Java、Gradle 等工具
- 通过 .tool-versions 文件锁定项目依赖版本
- 提供便捷的版本切换功能

### Suggested Action
对于需要管理多个项目不同版本依赖的场景，优先使用 mise 而非手动安装

### Metadata
- Source: conversation
- Related Files: .tool-versions
- Tags: mise, java, gradle, version-management
- Pattern-Key: mise.tool.management
- Recurrence-Count: 1
- First-Seen: 2026-05-16
- Last-Seen: 2026-05-16

---

## [LRN-20260516-004] knowledge_gap

**Logged**: 2026-05-16T00:54:00Z
**Priority**: high
**Status**: pending
**Area**: infra

### Summary
Android 项目构建需要 Android SDK，单纯配置 Java 和 Gradle 不足够

### Details
Android 项目除了需要 Java 和 Gradle 之外，还需要 Android SDK 来编译。
Android SDK 包含：
- build-tools（编译工具）
- platform-tools（平台工具）
- platforms（Android 平台版本，如 android-34）

### Suggested Action
配置 Android 开发环境时，必须安装 Android SDK：
- 推荐使用 Android Studio
- 或使用 command line tools: https://developer.android.com/studio#command-line-tools-only
- 设置 ANDROID_HOME 环境变量指向 SDK 目录

### Metadata
- Source: error
- Related Files: local.properties
- Tags: android, sdk, build
- Pattern-Key: android.sdk.required
- Recurrence-Count: 1
- First-Seen: 2026-05-16
- Last-Seen: 2026-05-16

---
