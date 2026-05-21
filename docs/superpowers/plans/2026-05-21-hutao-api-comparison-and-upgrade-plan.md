# Snap.Hutao.Remastered API 对比分析与升级计划

> **创建日期:** 2026-05-21  
> **分析对象:** PaimonsNotebook vs Snap.Hutao.Remastered  
> **参考项目:** https://github.com/SnapHutaoRemasteringProject/Snap.Hutao.Remastered

---

## 1. 项目概述

### 1.1 当前项目 (PaimonsNotebook)
- **平台:** Android (Kotlin + Jetpack Compose)
- **主要功能:** 原神游戏数据查询、实时便笺、抽卡记录等
- **API 来源:** 基于 Snap.Hutao 早期版本的 API 定义

### 1.2 参考项目 (Snap.Hutao.Remastered)
- **平台:** Windows (C# + WinUI 3)
- **主要功能:** 原神工具箱，包含游戏启动器、数据同步、祈愿记录等
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

#### 2.1.3 Hutao 云服务 API (关键差异)

| API 端点 | PaimonsNotebook | Snap.Hutao.Remastered | 差异说明 |
|---------|----------------|----------------------|---------|
| **Hutao API Root** | `hutao-metadata-pages.snapgenshin.cn` | `api.snaphutaorp.org` | ⚠️ **需升级** |
| **静态资源** | `api.snaphutaorp.org/static/raw` | `api.snaphutaorp.org/static/raw` | ✅ 无差异 |
| **元数据文件** | `Genshin/{locale}/{fileName}` | `metadata/Genshin/{locale}/{fileName}` | ⚠️ **需升级** |
| **元数据模板** | ❌ 无 | `metadata/template` | ⚠️ **需新增** |

#### 2.1.4 Hutao 后端服务 API

| API 端点 | PaimonsNotebook | Snap.Hutao.Remastered | 差异说明 |
|---------|----------------|----------------------|---------|
| **祈愿记录-获取** | ❌ 无 | `GachaLog/Retrieve` | ⚠️ **需新增** |
| **祈愿记录-上传** | ❌ 无 | `GachaLog/Upload` | ⚠️ **需新增** |
| **祈愿记录-列表** | ❌ 无 | `GachaLog/Entries` | ⚠️ **需新增** |
| **祈愿记录-删除** | ❌ 无 | `GachaLog/Delete` | ⚠️ **需新增** |
| **深渊记录-检查** | ❌ 无 | `Record/Check` | ⚠️ **需新增** |
| **深渊记录-排行** | ❌ 无 | `Record/Rank` | ⚠️ **需新增** |
| **深渊记录-上传** | ❌ 无 | `Record/Upload` | ⚠️ **需新增** |
| **统计-总览** | ❌ 无 | `Statistics/Overview` | ⚠️ **需新增** |
| **统计-角色出场率** | ❌ 无 | `Statistics/Avatar/AttendanceRate` | ⚠️ **需新增** |
| **统计-角色使用率** | ❌ 无 | `Statistics/Avatar/UtilizationRate` | ⚠️ **需新增** |
| **统计-角色搭配** | ❌ 无 | `Statistics/Avatar/AvatarCollocation` | ⚠️ **需新增** |
| **统计-角色持有率** | ❌ 无 | `Statistics/Avatar/HoldingRate` | ⚠️ **需新增** |
| **统计-武器搭配** | ❌ 无 | `Statistics/Weapon/WeaponCollocation` | ⚠️ **需新增** |
| **统计-队伍组合** | ❌ 无 | `Statistics/Team/Combination` | ⚠️ **需新增** |
| **Passport-验证** | ❌ 无 | `Passport/v2/Verify` | ⚠️ **需新增** |
| **Passport-登录** | ❌ 无 | `Passport/v2/Login` | ⚠️ **需新增** |
| **Passport-用户信息** | ❌ 无 | `Passport/v2/UserInfo` | ⚠️ **需新增** |

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

## 4. API 架构差异

### 4.1 项目结构对比

#### PaimonsNotebook 结构
```
common/web/
├── ApiEndpoints.kt       # 米游社 API 端点
├── HutaoEndpoints.kt     # Hutao 元数据端点
└── UIGFApiEndpoints.kt   # UIGF API 端点
```

#### Snap.Hutao.Remastered 结构
```
Web/
├── Endpoint/
│   ├── Hoyolab/
│   │   ├── IApiEndpoints.cs              # API 端点接口
│   │   ├── ApiEndpointsForChinese.cs     # 国服实现
│   │   ├── ApiEndpointsForOversea.cs     # 海外实现
│   │   ├── ApiEndpointsKind.cs           # API 类型枚举
│   │   └── ApiEndpointsFactory.cs        # 工厂类
│   └── Hutao/
│       ├── IHutaoEndpoints.cs            # Hutao 端点接口
│       ├── IHomaEndpoints.cs             # Homa 端点接口
│       ├── IHomaGachaLogEndpoints.cs     # 祈愿记录端点
│       ├── IHomaSpiralAbyssEndpoints.cs  # 深渊统计端点
│       ├── IHomaPassportEndpoints.cs     # Passport 端点
│       ├── IHomaServiceEndpoints.cs      # 服务端点
│       ├── IHomaRoleCombatEndpoints.cs    # 战绩端点
│       ├── IHomaRedeemCodeEndpoints.cs   # 兑换码端点
│       └── IInfrastructure*.cs           # 基础设施端点
├── Request/
│   ├── Builder/
│   │   ├── HttpRequestMessageBuilder.cs
│   │   └── HttpHeadersBuilderExtension.cs
│   └── Abstraction/
│       └── IHttpHeadersBuilder.cs
├── Hoyolab/
│   ├── DataSigning/
│   │   ├── DataSignAlgorithm.cs          # DS 签名算法
│   │   ├── SaltType.cs                   # Salt 类型枚举
│   │   └── DataSignOptions.cs
│   └── Takumi/GameRecord/
│       ├── GameRecordClient.cs
│       └── CardClient.cs
└── Hutao/
    ├── GachaLog/
    └── Passport/
```

### 4.2 核心差异

| 特性 | PaimonsNotebook | Snap.Hutao.Remastered |
|------|----------------|----------------------|
| **语言** | Kotlin | C# |
| **API 端点组织** | 单文件 object | 接口+实现分离 |
| **工厂模式** | ❌ 无 | ✅ 完整实现 |
| **依赖注入** | ❌ 基础 | ✅ 完整 DI |
| **请求构建器** | 基础 OkHttp | 完整的 Builder 模式 |
| **DS 签名** | 基础实现 | 多 Salt 类型支持 |
| **Hutao Cloud** | ❌ 无 | ✅ 完整支持 |

---

## 5. 升级计划

### 5.1 升级优先级

#### 🔴 高优先级 (必须升级)

1. **Hutao 元数据 API 端点更新**
2. **新增 DS Salt 类型支持**
3. **新增特定功能请求头**

#### 🟡 中优先级 (建议升级)

4. **新增 Hutao Cloud API 端点**
5. **新增 Passport 认证端点**
6. **新增统计 API 端点**

#### 🟢 低优先级 (可选升级)

7. **祈愿记录云同步功能**
8. **深渊数据云同步功能**

---

### 5.2 详细升级任务

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

#### 任务 4: 新增 Hutao Cloud API 端点

**文件:** 新建 `app/src/main/java/com/lianyi/paimonsnotebook/common/web/hutao/HutaoCloudEndpoints.kt`

**新增内容:**
```kotlin
object HutaoCloudEndpoints {
    private const val ROOT = "https://homa.snaphutaorp.org"
    private const val INFRA_ROOT = "https://api.snaphutaorp.org"
    
    // 祈愿记录
    fun gachaLogRetrieve() = "$ROOT/GachaLog/Retrieve"
    fun gachaLogUpload() = "$ROOT/GachaLog/Upload"
    fun gachaLogEntries() = "$ROOT/GachaLog/Entries"
    fun gachaLogDelete(uid: String) = "$ROOT/GachaLog/Delete?Uid=$uid"
    
    // 深渊记录
    fun recordCheck(uid: String) = "$ROOT/Record/Check?Uid=$uid"
    fun recordRank(uid: String) = "$ROOT/Record/Rank?Uid=$uid"
    fun recordUpload() = "$ROOT/Record/Upload"
    
    // 统计
    fun statisticsOverview(last: Boolean = false) = "$ROOT/Statistics/Overview?Last=$last"
    fun avatarAttendanceRate(last: Boolean = false) = "$ROOT/Statistics/Avatar/AttendanceRate?Last=$last"
    fun avatarUtilizationRate(last: Boolean = false) = "$ROOT/Statistics/Avatar/UtilizationRate?Last=$last"
    
    // Passport
    fun passportVerify() = "$ROOT/Passport/v2/Verify"
    fun passportLogin() = "$ROOT/Passport/v2/Login"
    fun passportUserInfo() = "$ROOT/Passport/v2/UserInfo"
    
    // 元数据
    fun metadata(locale: String, fileName: String) = "$INFRA_ROOT/metadata/Genshin/$locale/$fileName"
    fun metadataTemplate() = "$INFRA_ROOT/metadata/template"
}
```

---

#### 任务 5: 新增 Passport 认证端点

**文件:** 新建 `app/src/main/java/com/lianyi/paimonsnotebook/common/web/hoyolab/PassportClient.kt`

**功能:**
- 用户验证
- 用户登录
- 用户信息获取
- Token 刷新

---

#### 任务 6: 新增统计 API 客户端

**文件:** 新建 `app/src/main/java/com/lianyi/paimonsnotebook/common/web/hutao/StatisticsClient.kt`

**功能:**
- 获取深渊统计总览
- 获取角色出场率
- 获取角色使用率
- 获取角色搭配
- 获取武器搭配
- 获取队伍组合

---

### 5.3 升级检查清单

- [ ] 更新 `HutaoEndpoints.kt` 中的元数据基础 URL
- [ ] 新增 `/metadata/Genshin/` 路径前缀
- [ ] 新增 `metadataTemplate()` 方法
- [ ] 新增 `SaltType` 枚举类
- [ ] 更新 `GameRecordClient` 使用正确的 Salt
- [ ] 新增签到请求头 `x-rpc-signgame`
- [ ] 新增工具版本请求头 `x-rpc-tool_verison`
- [ ] 新增 Hutao Cloud API 端点类
- [ ] 新增 Passport 认证客户端
- [ ] 新增统计 API 客户端
- [ ] 测试所有 API 端点正常工作
- [ ] 测试 DS 签名正确生成

---

## 6. API 端点完整列表

### 6.1 Snap.Hutao.Remastered 完整端点

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

#### Hutao API
```
基础 URL: https://homa.snaphutaorp.org
- /GachaLog/Retrieve
- /GachaLog/Upload
- /GachaLog/Entries
- /GachaLog/Delete
- /GachaLog/Statistics/CurrentEventStatistics
- /GachaLog/Statistics/Distribution/{type}
- /Record/Check
- /Record/Rank
- /Record/Upload
- /Statistics/Overview
- /Statistics/Avatar/AttendanceRate
- /Statistics/Avatar/UtilizationRate
- /Statistics/Avatar/AvatarCollocation
- /Statistics/Avatar/HoldingRate
- /Statistics/Weapon/WeaponCollocation
- /Statistics/Team/Combination
- /Passport/v2/Verify
- /Passport/v2/Register
- /Passport/v2/Login
- /Passport/v2/UserInfo
- /Passport/v2/RefreshToken
- /Service/Announcement/List
- /Service/GachaLog/Compensation
- /Service/GachaLog/Designation
```

#### 基础设施 API
```
基础 URL: https://api.snaphutaorp.org
- /metadata/Genshin/{locale}/{fileName}
- /metadata/template
- /static/raw/{category}/{fileName}
- /static/zip/{fileName}.zip
- /static/size
- /ip
- /ips
```

---

## 7. 请求头完整列表

### 7.1 Snap.Hutao.Remastered 完整请求头

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

## 8. 总结

### 8.1 主要差异

1. **API 端点 URL 变化:** Hutao 元数据从 `hutao-metadata-pages.snapgenshin.cn` 迁移到 `api.snaphutaorp.org`
2. **新增大量 Hutao Cloud API:** 包括祈愿记录、深渊统计、Passport 等
3. **请求头增强:** 新增多个 x-rpc 请求头和 Salt 类型
4. **架构优化:** 从单文件 object 模式转向接口+实现分离模式

### 8.2 升级建议

1. **分阶段升级:** 优先完成高优先级任务，确保基础功能正常
2. **保持兼容性:** 新增 API 应向后兼容现有功能
3. **充分测试:** 每个 API 变更后进行全面测试
4. **错误处理:** 增强错误处理和重试机制

---

**文档生成时间:** 2026-05-21
**分析工具:** DeepWiki + 源码分析
