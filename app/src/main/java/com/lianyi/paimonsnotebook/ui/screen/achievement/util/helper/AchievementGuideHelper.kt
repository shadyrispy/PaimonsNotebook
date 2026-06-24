package com.lianyi.paimonsnotebook.ui.screen.achievement.util.helper

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.lianyi.paimonsnotebook.common.application.PaimonsNotebookApplication
import com.lianyi.paimonsnotebook.common.extension.string.errorNotify
import java.net.URLEncoder

object AchievementGuideHelper {

    private const val BILI_WIKI_SEARCH_BASE = "https://wiki.biligame.com/ys/index.php?search="

    private val mainHandler = Handler(Looper.getMainLooper())

    fun openGuide(achievementTitle: String) {
        // 延迟执行，避免在 Compose clickable lambda 中直接调用 startActivity
        // 导致 "measure is called on a deactivated node" 崩溃
        mainHandler.post {
            doOpenGuide(achievementTitle)
        }
    }

    private fun doOpenGuide(achievementTitle: String) {
        val encodedKeyword = URLEncoder.encode(achievementTitle, "UTF-8")
        val searchUrl = BILI_WIKI_SEARCH_BASE + encodedKeyword

        try {
            val context = PaimonsNotebookApplication.context
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(searchUrl)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            "无法打开攻略链接，请检查是否安装浏览器".errorNotify()
        }
    }
}
