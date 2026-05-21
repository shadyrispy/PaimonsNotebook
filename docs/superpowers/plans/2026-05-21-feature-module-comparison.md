# 功能模块详细对比分析与升级计划

> **创建日期:** 2026-05-21  
> **分析对象:** PaimonsNotebook vs Snap.Hutao.Remastered  
> **参考项目:** https://github.com/SnapHutaoRemasteringProject/Snap.Hutao.Remastered

---

## 1. 功能模块对比总览

| 功能模块 | PaimonsNotebook | Snap.Hutao.Remastered | 状态 |
|----------|----------------|----------------------|------|
| 实时便笺 | ✅ 支持 | ✅ 支持 | ⚠️ 部分差异 |
| 我的角色 | ✅ 支持 | ✅ 支持 | ⚠️ 部分差异 |
| 深境螺旋 | ✅ 支持 | ✅ 支持 | ⚠️ 部分差异 |
| 游戏记录主页 | ✅ 支持 | ✅ 支持 | ⚠️ 缺失 |
| 角色基础信息 | ❌ 缺失 | ✅ 支持 | ❌ 缺失 |
| 战绩系统 | ❌ 缺失 | ✅ 支持 | ❌ 缺失 |
| 硬挑战 | ❌ 缺失 | ✅ 支持 | ❌ 缺失 |
| 活动日历 | ❌ 缺失 | ✅ 支持 | ❌ 缺失 |

---

## 2. 实时便笺 (DailyNote) 模块对比

### 2.1 请求流程对比

| 步骤 | PaimonsNotebook | Snap.Hutao.Remastered | 差异 |
|------|----------------|----------------------|------|
| 1. 构建请求 | `buildRequest { ... }` | `HttpRequestMessageBuilder.Create()` | API 风格不同 |
| 2. 设置 URL | `url(ApiEndpoints.GameRecordDailyNote())` | `SetRequestUri(apiEndpoints.GameRecordDailyNote())` | ✅ 一致 |
| 3. 设置 Cookie | `setUser(CookieHelper.Type.Ltoken)` | `SetUserCookieAndFpHeader(CookieType.Cookie)` | ⚠️ Cookie 类型不同 |
| 4. 设置 Referer | ❌ 未设置 | `SetReferer(apiEndpoints.WebStaticReferer())` | ❌ 缺失 |
| 5. 设置工具版本 | `setXRpcToolVersion()` | `SetHeader("x-rpc-tool_verison", "v5.0.1-ys")` | ✅ 一致 |
| 6. DS 签名 | `setDynamicSecret(X4, Gen2)` | `SignDataAsync(Gen2, X4, false)` | ✅ 一致 |
| 7. 发送请求 | `getAsJson<DailyNoteData>()` | `SendAsync<Response<DailyNote>>()` | API 风格不同 |
| 8. 验证码重试 | 手动传入 challenge | `RetryIf1034Async()` 自动重试 | ⚠️ 机制不同 |

### 2.2 请求头对比

| 请求头 | PaimonsNotebook | Snap.Hutao.Remastered | 状态 |
|--------|----------------|----------------------|------|
| **Cookie** | Ltoken | Cookie | ⚠️ 需要修改 |
| **x-rpc-device_fp** | ✅ 支持 | ✅ 支持 | ✅ 一致 |
| **x-rpc-tool_verison** | ✅ `v5.0.1-ys` | ✅ `v5.0.1-ys` | ✅ 一致 |
| **Referer** | ❌ 缺失 | ✅ `https://webstatic.mihoyo.com/` | ❌ 缺失 |
| **x-rpc-challenge** | ✅ 支持 | ✅ 支持 | ✅ 一致 |
| **x-rpc-challenge_game** | ✅ `genshin` | ✅ `2` (整数) | ⚠️ 格式不同 |
| **x-rpc-challenge_path** | ✅ `/ys/daily/` | ✅ 完整路径 | ✅ 一致 |

### 2.3 数据处理对比

| 数据项 | PaimonsNotebook | Snap.Hutao.Remastered | 状态 |
|--------|----------------|----------------------|------|
| 树脂恢复时间 | ✅ 字符串解析 | ✅ 类型化时间处理 | ⚠️ 实现不同 |
| 派遣状态 | ✅ 基础支持 | ✅ 详细状态枚举 | ⚠️ 实现不同 |
| 参量质变仪 | ✅ 基础支持 | ✅ 详细状态处理 | ⚠️ 实现不同 |
| 每日任务 | ✅ 基础支持 | ✅ 本地化处理 | ⚠️ 实现不同 |
| 好感度奖励 | ✅ 基础支持 | ✅ 状态枚举 | ⚠️ 实现不同 |

---

## 3. 我的角色 (Character) 模块对比

### 3.1 请求流程对比

| 步骤 | PaimonsNotebook | Snap.Hutao.Remastered | 差异 |
|------|----------------|----------------------|------|
| 1. 请求方式 | POST | POST | ✅ 一致 |
| 2. 设置 URL | `gameRecordCharacterList` | `GameRecordCharacterList()` | ✅ 一致 |
| 3. 设置 Cookie | `CookieHelper.Type.Cookie` | `CookieType.Cookie` | ✅ 一致 |
| 4. 设置 ClientType | `EnvironmentClientType.WEB` | 通过 HttpClient 配置 | ✅ 一致 |
| 5. 设置工具版本 | `setXRpcToolVersion()` | ❌ 未设置 | ⚠️ 差异 |
| 6. 设置 Referer | ❌ 缺失 | `SetReferer()` | ❌ 缺失 |
| 7. DS 签名 | `X4 + Gen2` | `X4 + Gen2` | ✅ 一致 |
| 8. 请求体 | `server, role_id, sort_type` | `CharacterData(Uid)` | ⚠️ 字段不同 |

### 3.2 请求体对比

| 字段 | PaimonsNotebook | Snap.Hutao.Remastered | 状态 |
|------|----------------|----------------------|------|
| **server** | `playerUid.region` | ✅ 包含 | ✅ 一致 |
| **role_id** | `playerUid.value` | ✅ 包含 | ✅ 一致 |
| **sort_type** | `1` (硬编码) | ❌ 无此字段 | ⚠️ 额外字段 |

### 3.3 角色详情请求对比

| 步骤 | PaimonsNotebook | Snap.Hutao.Remastered | 差异 |
|------|----------------|----------------------|------|
| 请求体 | `role_id, server, character_ids` | `CharacterData(Uid, characterIds)` | ✅ 一致 |
| 请求头 | `x-rpc-client_type: WEB` | ✅ 一致 | ✅ 一致 |
| DS 签名 | `X4 + Gen2` | `X4 + Gen2` | ✅ 一致 |

---

## 4. 深境螺旋 (SpiralAbyss) 模块对比

### 4.1 请求流程对比

| 步骤 | PaimonsNotebook | Snap.Hutao.Remastered | 差异 |
|------|----------------|----------------------|------|
| 1. 请求方式 | GET | GET | ✅ 一致 |
| 2. 设置 URL | `gameRecordSpiralAbyss()` | `GameRecordSpiralAbyss()` | ✅ 一致 |
| 3. 设置 Cookie | `CookieHelper.Type.Cookie` | `CookieType.Cookie` | ✅ 一致 |
| 4. 设置工具版本 | `setXRpcToolVersion()` | ❌ 未设置 | ⚠️ 差异 |
| 5. 设置 Referer | ❌ 缺失 | `SetReferer()` | ❌ 缺失 |
| 6. DS 签名 | `X4 + Gen2` | `X4 + Gen2` | ✅ 一致 |
| 7. 验证码重试 | ❌ 缺失 | `RetryIf1034Async()` | ❌ 缺失 |

### 4.2 请求参数对比

| 参数 | PaimonsNotebook | Snap.Hutao.Remastered | 状态 |
|------|----------------|----------------------|------|
| **schedule_type** | `1` 或 `2` | `ScheduleType` 枚举 | ✅ 一致 |
| **uid** | 路径参数 | 路径参数 | ✅ 一致 |

---

## 5. 核心差异总结

### 5.1 请求头差异

| 请求头 | PaimonsNotebook | Hutao | 升级建议 |
|--------|----------------|-------|----------|
| **Cookie 类型** | Ltoken | Cookie | 需要修改为 Cookie |
| **Referer** | ❌ 缺失 | ✅ 支持 | 需要添加 |
| **x-rpc-challenge_game** | `genshin` (字符串) | `2` (整数) | 需要修改格式 |

### 5.2 重试机制差异

| 特性 | PaimonsNotebook | Hutao | 升级建议 |
|------|----------------|-------|----------|
| 1034 错误处理 | 手动传入 challenge | 自动重试 | 需要实现自动重试 |
| 验证码服务 | 基础支持 | `IGeetestService` | 需要完善 |
| 重试次数 | 不支持 | 支持一次重试 | 需要实现 |

### 5.3 缺失功能

| 功能 | 状态 | 优先级 |
|------|------|--------|
| 游戏记录主页 | ❌ 缺失 | 中 |
| 角色基础信息 | ❌ 缺失 | 低 |
| 战绩系统 | ❌ 缺失 | 低 |
| 硬挑战 | ❌ 缺失 | 低 |
| 活动日历 | ❌ 缺失 | 低 |

---

## 6. 升级计划

### 6.1 高优先级任务

#### 任务 1: 统一 Cookie 类型为 Cookie

**修改文件:** [GameRecordClient.kt](file:///workspace/app/src/main/java/com/lianyi/paimonsnotebook/common/web/hoyolab/takumi/game_record/GameRecordClient.kt)

**变更:**
```kotlin
// 当前
setUser(user = user, cookieType = CookieHelper.Type.Ltoken)

// 修改为
setUser(user = user, cookieType = CookieHelper.Type.Cookie)
```

#### 任务 2: 添加 Referer 请求头

**修改文件:** [GameRecordClient.kt](file:///workspace/app/src/main/java/com/lianyi/paimonsnotebook/common/web/hoyolab/takumi/game_record/GameRecordClient.kt)

**变更:**
```kotlin
// 在所有请求中添加
setReferer("https://webstatic.mihoyo.com/")
```

**需要新增扩展方法:**
```kotlin
// SetReferer.kt
fun Request.Builder.setReferer(value: String) = this.header("Referer", value)
```

#### 任务 3: 修复 challenge_game 格式

**修改文件:** [SetXRPC.kt](file:///workspace/app/src/main/java/com/lianyi/paimonsnotebook/common/extension/request/SetXRPC.kt)

**变更:**
```kotlin
// 当前
fun Request.Builder.setXRpcChallengeGame(value:String = "genshin") = 
    this.header("x-rpc-challenge_game",value)

// 修改为
fun Request.Builder.setXRpcChallengeGame(value:Int = 2) = 
    this.header("x-rpc-challenge_game", value.toString())
```

---

### 6.2 中优先级任务

#### 任务 4: 实现自动重试机制

**修改文件:** [GameRecordClient.kt](file:///workspace/app/src/main/java/com/lianyi/paimonsnotebook/common/web/hoyolab/takumi/game_record/GameRecordClient.kt)

**新增重试方法:**
```kotlin
private suspend fun <T> retryIf1034(
    user: UserEntity,
    response: ResultData<T>,
    headersFactory: () -> ChallengeHeaders,
    requestBuilder: Request.Builder.() -> Unit
): ResultData<T> {
    if (response.code == 1034) {
        // 尝试获取验证码并重试
        val challenge = getChallenge(user, headersFactory())
        if (challenge.isNotBlank()) {
            return buildRequest {
                requestBuilder()
                setXRpcChallenge(challenge)
                setXRpcChallengeGame()
                setXRpcChallengePath(headersFactory().path)
            }.getAsJson()
        }
    }
    return response
}
```

#### 任务 5: 添加缺失的 API 端点

**修改文件:** [ApiEndpoints.kt](file:///workspace/app/src/main/java/com/lianyi/paimonsnotebook/common/web/ApiEndpoints.kt)

**新增端点:**
```kotlin
// 游戏记录主页
fun gameRecordIndex(uid: PlayerUid) = 
    "${ApiTakumiRecordApi}/index?server=${uid.region}&role_id=${uid.value}"

// 角色基础信息
fun gameRecordRoleBasicInfo(uid: PlayerUid) = 
    "${ApiTakumiRecordApi}/roleBasicInfo?server=${uid.region}&role_id=${uid.value}"

// 战绩系统
fun gameRecordRoleCombat(uid: PlayerUid) = 
    "${ApiTakumiRecordApi}/roleCombat?server=${uid.region}&role_id=${uid.value}"

// 硬挑战
fun gameRecordHardChallenge(uid: PlayerUid) = 
    "${ApiTakumiRecordApi}/hardChallenge?server=${uid.region}&role_id=${uid.value}"

// 活动日历
fun gameRecordActCalendar() = "${ApiTakumiRecordApi}/actCalendar"
```

---

### 6.3 低优先级任务

#### 任务 6: 添加新 API 客户端方法

**修改文件:** [GameRecordClient.kt](file:///workspace/app/src/main/java/com/lianyi/paimonsnotebook/common/web/hoyolab/takumi/game_record/GameRecordClient.kt)

**新增方法:**
```kotlin
suspend fun getPlayerInfo(userAndUid: UserAndUid) = buildRequest {
    url(ApiEndpoints.gameRecordIndex(userAndUid.playerUid))
    setUser(userAndUid.userEntity, CookieHelper.Type.Cookie)
    setReferer("https://webstatic.mihoyo.com/")
    setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
}.getAsJson<PlayerInfoData>()

suspend fun getRoleBasicInfo(userAndUid: UserAndUid) = buildRequest {
    url(ApiEndpoints.gameRecordRoleBasicInfo(userAndUid.playerUid))
    setUser(userAndUid.userEntity, CookieHelper.Type.Cookie)
    setReferer("https://webstatic.mihoyo.com/")
    setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
}.getAsJson<BasicRoleInfoData>()
```

---

## 7. 升级检查清单

### 7.1 实时便笺
- [ ] Cookie 类型从 Ltoken 改为 Cookie
- [ ] 添加 Referer 请求头
- [ ] 修复 challenge_game 格式为整数
- [ ] 实现自动重试机制

### 7.2 我的角色
- [ ] 添加 Referer 请求头
- [ ] 移除不必要的 sort_type 参数
- [ ] 实现自动重试机制

### 7.3 深境螺旋
- [ ] 添加 Referer 请求头
- [ ] 实现自动重试机制

### 7.4 新增功能
- [ ] 添加游戏记录主页 API
- [ ] 添加角色基础信息 API
- [ ] 添加战绩系统 API
- [ ] 添加硬挑战 API
- [ ] 添加活动日历 API

---

## 8. 代码优化建议

### 8.1 请求构建器重构

参考 Hutao 的 `HttpRequestMessageBuilder` 模式，建议重构请求构建逻辑：

```kotlin
// 优化后的请求构建
buildRequest {
    url(apiEndpoints.gameRecordDailyNote(playerUid))
    setUserCookie(user, CookieType.Cookie)
    setReferer(apiEndpoints.webStaticReferer())
    setToolVersion()
    signData(SaltType.X4, Version.Gen2)
}.getAsJson<DailyNoteData>()
```

### 8.2 统一响应处理

参考 Hutao 的 `Response<T>` 模式，建议统一响应处理：

```kotlin
data class Response<T>(
    val retcode: Int,
    val message: String,
    val data: T?
) {
    fun isSuccess() = retcode == 0
    fun isVerificationRequired() = retcode == 1034
}
```

---

## 9. 总结

### 9.1 关键差异

1. **Cookie 类型**: PaimonsNotebook 使用 Ltoken，Hutao 使用 Cookie
2. **请求头**: PaimonsNotebook 缺少 Referer 请求头
3. **重试机制**: Hutao 有完整的自动重试机制，PaimonsNotebook 需要手动处理
4. **功能完整性**: Hutao 支持更多 API 端点

### 9.2 升级优先级

1. **高优先级**: Cookie 类型、Referer、challenge_game 格式
2. **中优先级**: 自动重试机制、缺失的 API 端点
3. **低优先级**: 新增功能模块

### 9.3 预期收益

完成升级后，API 请求将与 Hutao 保持一致，提高请求成功率，减少验证码触发频率。

---

**文档生成时间:** 2026-05-21  
**分析工具:** 源码对比分析
