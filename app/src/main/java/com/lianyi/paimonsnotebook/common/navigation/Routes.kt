package com.lianyi.paimonsnotebook.common.navigation

/**
 * 统一的 Intent extra key 常量表。
 *
 * 消除散落在各文件中的硬编码字符串 key，避免写读双方改名不一致导致静默失效。
 */
object Routes {
    /** WebView 页面 URL */
    const val EXTRA_URL = "url"

    /** 视频播放列表（JSON 序列化） */
    const val EXTRA_VIDEO_LIST_JSON = "video_list"

    /** 米游社用户 mid（用于 HoyolabWebActivity / Widget / Abyss 跳转） */
    const val EXTRA_MID = "mid"

    /** 成就分类概览数据（JSON 序列化） */
    const val EXTRA_GOAL_JSON = "goal"

    /** 成就目标 ID */
    const val EXTRA_TARGET_ID = "target_id"

    /** 成就列表数据（JSON 序列化） */
    const val EXTRA_LIST_JSON = "list"

    /** 养成计划选项页 — 以添加模式打开 */
    const val EXTRA_ADD_FLAG = "add"
}
