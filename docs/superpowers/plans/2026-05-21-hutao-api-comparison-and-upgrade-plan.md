# Snap.Hutao.Remastered API 对比分析与升级计划

> **创建日期:** 2026-05-21  
> **分析对象:** PaimonsNotebook vs Snap.Hutao.Remastered  
> **参考项目:** https://github.com/SnapHutaoRemasteringProject/Snap.Hutao.Remastered  
> **备注:** 仅保留核心 API 更新（不包含 Hutao Cloud 服务，采用本地存储方案）

---

## 1. 项目概述

### 1.1 当前项目 (PaimonsNotebook)
- **平台:** Android (Kotlin + Jetpack Compose)
- **主要功能:** 原神游戏数据查询、实时便笺、抽卡记录等
- **数据存储:** 本地存储
- **API 来源:** 基于 Snap.Hutao 早期版本的 API 定义

### 1.2 参考项目 (Snap.Hutao.Remastered)
- **平台:** Windows (C# + WinUI 3)
- **主要功能:** 原神工具箱
- **最新更新:** 持续更新中，使用最新的 API 版本

---

## 2. API 端点对比分析

### 2.1 米游社/Hoyolab API

#### 2.1.1 API 基础 URL 对比

| 分类 | PaimonsNotebook | Snap.Hutao.Remastered | 差异说明 |
|------|----------------|----------------------|---------|
| **Takumi API** | `api-takumi.mihoyo.com` | 保持一致 | ✅ 无差异 |
| **Takumi Record** | `api-takumi-record.mihoyo.com` | 保持一致 | ✅ 无差异 |
| **BBS API** | `bbs-api.mihoyo.com` / `bbs-api.miyoushe.com` | 保持一致 | ✅ 无差异 |
| **SDK API** | `api-sdk.mihoyo.com` | 保持一致 | ✅ 无差异 |
| **Passport API** | `passport-api.mihoyo.com` | 保持一致 | ✅ 无差异 |
| **HK4E API** | `hk4e-api.mihoyo.com` | 保持一致 | ✅ 无差异 |

#### 2.1.2 游戏记录 API

| API 端点 | PaimonsNotebook | Snap.Hutao.Remastered | 差异说明 |
|---------|----------------|----------------------|---------|
| **角色基本信息** | `roleBasicInfo` | 保持一致 | ✅ 无差异 |
| **角色信息** | `character` | 保持一致 | ✅ 无差异 |
| **角色详情** | `character/detail` | 保持一致 | ✅ 无差异 |
| **角色列表** | `character/list` | 保持一致 | ✅ 无差异 |
| **实时便笺** | `dailyNote` | 保持一致 | ✅ 无差异 |
| **深渊信息** | `spiralAbyss` | 保持一致 | ✅ 无差异 |
| **游戏记录主页** | `index` | 保持一致 | ✅ 无差异 |

#### 2.1.3 Hutao 元数据 API (关键差异)

| API 端点 | PaimonsNotebook | Snap.Hutao.Remastered | 差异说明 |
|---------|----------------|----------------------|---------|
| **Hutao API Root** | `hutao-metadata-pages.snapgenshin.cn` | `api.snaphutaorp.org` | ⚠️ **需升级** |
| **静态资源** | `api.snaphutaorp.org/static/raw` | `api.snaphutaorp.org/static/raw` | ✅ 无差异 |
| **元数据文件** | `Genshin/{locale}/{fileName}` | `metadata/Genshin/{locale}/{fileName}` | ⚠️ **需升级** |
| **元数据模板** | ❌ 无 | `metadata/template` | ⚠️ **需新增** |

---

## 3. 请求头对比分析

### 3.1 标准请求头

| 请求头 | PaimonsNotebook | Snap.Hutao.Remastered | 差异说明 |
|--------|----------------|----------------------|---------|
| **User-Agent** | 自定义 UA | 自定义 UA | ✅ 需同步 |
| **Cookie** | ✅ 支持 | ✅ 支持 | ✅ 无差异 |
| **x-rpc-device_fp** | ✅ 支持 | ✅ 支持 | ✅ 无差异 |

### 3.2 DS 签名相关请求头

| 请求头 | PaimonsNotebook | Snap.Hutao.Remastered | 差异说明 |
|--------|----------------|----------------------|---------|
| **DS** | ✅ 支持 | ✅ 支持 | ✅ 无差异 |
| **Salt 类型** | 基础 Salt | `X4`, `X6`, `LK2`, `K2`, `OSK2`, `OSLK2`, `OSX4`, `OSX6` 等 | ⚠️ **需升级** |
| **签名版本** | Gen1/Gen2 | Gen1/Gen2 | ✅ 无差异 |

### 3.3 验证码相关请求头

| 请求头 | PaimonsNotebook | Snap.Hutao.Remastered | 差异说明 |
|--------|----------------|----------------------|---------|
| **x-rpc-challenge** | ✅ 支持 | ✅ 支持 | ✅ 无差异 |
| **x-rpc-validate** | ✅ 支持 | ✅ 支持 | ✅ 无差异 |
| **x-rpc-seccode** | ✅ 支持 | ✅ 支持 | ✅ 无差异 |
| **x-rpc-verify** | ✅ 支持 | ✅ 支持 | ✅ 无差异 |
| **x-rpc-aigis** | ❌ 无 | ✅ 支持 | ⚠️ **需新增** |
| **x-rpc-signgame** | ❌ 无 | ✅ 支持 (`hk4e`) | ⚠️ **需新增** |
| **x-rpc-tool_verison** | ❌ 无 | ✅ 支持 (`v5.0.1-ys`) | ⚠️ **需新增** |

### 3.4 特定功能请求头

| 请求头 | PaimonsNotebook | Snap.Hutao.Remastered | 差异说明 |
|--------|----------------|----------------------|---------|
| **x-rpc-challenge_game** | ❌ 无 | ✅ 支持 | ⚠️ **需新增** |
| **x-rpc-challenge_path** | ❌ 无 | ✅ 支持 | ⚠️ **需新增** |
| **Referer** | ✅ 支持 | ✅ 支持 | ✅ 无差异 |

---

## 4. 升级计划

### 4.1 升级优先级

#### 🔴 高优先级 (必须升级)

1. **Hutao 元数据 API 端点更新**
2. **新增 DS Salt 类型支持**
3. **新增特定功能请求头**

---

### 4.2 详细升级任务

#### 任务 1: 更新 Hutao 元数据 API 端点

**文件:** `app/src/main/java/com/lianyi/paimonsnotebook/common/web/HutaoEndpoints.kt`

**变更:**
```kotlin
// 当前
private const val HutaoMetadataBaseUrl = "https://hutao-metadata-pages.snapgenshin.cn"
private const val ApiSnapGenshinMetadata = "${HutaoMetadataBaseUrl}/Genshin"

// 修改为
private const val HutaoMetadataBaseUrl = "https://api.snaphutaorp.org"
private const val ApiSnapGenshinMetadata = "${HutaoMetadataBaseUrl}/metadata/Genshin"
```

**步骤:**
1. 更新 `HutaoMetadataBaseUrl` 基础 URL
2. 更新 `ApiSnapGenshinMetadata` 路径前缀为 `/metadata/Genshin`
3. 添加 `metadataTemplate()` 方法
4. 测试元数据下载功能

---

#### 任务 2: 新增 DS Salt 类型支持

**文件:** 新建 `app/src/main/java/com/lianyi/paimonsnotebook/common/web/hoyolab/SaltType.kt`

**新增内容:**
```kotlin
object SaltType {
    const val NONE = ""
    const val PROD = "xV#vTE5jWe*FYfjT"
    const val X4 = "cwvyOXKwAXpg0LtQ"
    const val X6 = "UORvJctSrYqPJAGD"
    const val K2 = "qAJY5LMXYcScZL4b"
    const val LK2 = "h3wmoSM4Ol3mbqgP"
    const val OSK2 = "hQQXYT7kCQC8Kltz"
    const val OSLK2 = "M69xXMu0c0D1t7R6"
    const val OSX4 = "gY1D5M9cO6k3X2qJ"
    const val OSX6 = "rH2kQ8lN5w7X9t4p"
}
```

**使用位置:**
- `GameRecordClient.kt` - 使用 `SaltType.X4` 或 `SaltType.X6`
- `SignInClient.kt` - 使用 `SaltType.LK2`

---

#### 任务 3: 新增特定功能请求头

**文件:** `app/src/main/java/com/lianyi/paimonsnotebook/common/web/hoyolab/RequestHeaders.kt`

**新增内容:**
```kotlin
object RequestHeaders {
    // 签到相关
    const val X_RPC_SIGNGAME = "hk4e"
    
    // 工具版本
    const val X_RPC_TOOL_VERSION = "v5.0.1-ys"
    
    // 验证码相关
    const val X_RPC_CHALLENGE_GAME = "genshin"
    const val X_RPC_AIGIS = ""
}
```

**使用位置:**
- 签到请求添加 `x-rpc-signgame: hk4e`
- 日常便笺请求添加 `x-rpc-tool_verison: v5.0.1-ys`

---

### 4.3 升级检查清单

- [ ] 更新 `HutaoEndpoints.kt` 中的元数据基础 URL
- [ ] 新增 `/metadata/Genshin/` 路径前缀
- [ ] 新增 `metadataTemplate()` 方法
- [ ] 新增 `SaltType` 枚举类
- [ ] 更新 `GameRecordClient` 使用正确的 Salt
- [ ] 新增签到请求头 `x-rpc-signgame`
- [ ] 新增工具版本请求头 `x-rpc-tool_verison`
- [ ] 测试所有 API 端点正常工作
- [ ] 测试 DS 签名正确生成

---

## 5. API 端点完整列表

### 5.1 核心 API 端点

#### Hoyolab API
```
基础 URL: https://api-takumi.mihoyo.com
- /common/goods/...
- /event/e20200928calculate/...
- /binding/api/getUserGameRoles
- /binding/api/getUserGameRolesByCookie
- /binding/api/genAuthKey
- /binding/api/changeGameRoleByDefault
- /game_record/app/genshin/api/roleBasicInfo
- /game_record/app/genshin/api/character
- /game_record/app/genshin/api/character/list
- /game_record/app/genshin/api/character/detail
- /game_record/app/genshin/api/dailyNote
- /game_record/app/genshin/api/index
- /game_record/app/genshin/api/spiralAbyss
- /game_record/app/card/api/getWidgetData
- /game_record/app/card/wapi/createVerification
- /game_record/app/card/wapi/verifyVerification
```

#### Hutao 元数据 API
```
基础 URL: https://api.snaphutaorp.org
- /metadata/Genshin/{locale}/{fileName}
- /metadata/template
- /static/raw/{category}/{fileName}
- /static/zip/{fileName}.zip
- /static/size
```

---

## 6. 请求头完整列表

### 6.1 核心请求头

#### 标准请求头
| 请求头 | 值 | 说明 |
|--------|-----|------|
| User-Agent | 自定义 | 应用标识 |
| Cookie | `cookie_token=...; ltoken=...; stoken=...` | 认证 Cookie |
| Referer | `https://webstatic.mihoyo.com/` | 来源页面 |

#### DS 签名请求头
| 请求头 | 格式 | 说明 |
|--------|------|------|
| DS | `{t},{r},{check}` | 动态签名 |

#### x-rpc 请求头
| 请求头 | 示例值 | 说明 |
|--------|--------|------|
| x-rpc-device_fp | `xxx` | 设备指纹 |
| x-rpc-tool_verison | `v5.0.1-ys` | 工具版本 |
| x-rpc-signgame | `hk4e` | 签到游戏 |
| x-rpc-challenge | `xxx` | 验证挑战 |
| x-rpc-validate | `xxx` | 验证结果 |
| x-rpc-seccode | `xxx\|jordan` | 安全码 |
| x-rpc-verify | `xxx` | 验证状态 |
| x-rpc-aigis | `xxx` | Aigis 验证 |
| x-rpc-challenge_game | `genshin` | 挑战游戏 |
| x-rpc-challenge_path | `/ys/daily/` | 挑战路径 |

---

## 7. 总结

### 7.1 主要差异

1. **API 端点 URL 变化:** Hutao 元数据从 `hutao-metadata-pages.snapgenshin.cn` 迁移到 `api.snaphutaorp.org`
2. **新增请求头:** 新增多个 x-rpc 请求头和 Salt 类型
3. **数据存储:** 保持本地存储，不引入 Hutao Cloud 服务

### 7.2 升级建议

1. **分阶段升级:** 优先完成高优先级任务，确保基础功能正常
2. **保持兼容性:** 新增 API 应向后兼容现有功能
3. **充分测试:** 每个 API 变更后进行全面测试
4. **错误处理:** 增强错误处理和重试机制

---

**文档生成时间:** 2026-05-21  
**分析工具:** DeepWiki + 源码分析  
**备注:** 已移除 Hutao Cloud 服务相关内容，仅保留核心 API 更新
