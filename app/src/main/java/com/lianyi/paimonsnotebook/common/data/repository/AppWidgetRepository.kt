package com.lianyi.paimonsnotebook.common.data.repository

import com.lianyi.paimonsnotebook.common.database.PaimonsNotebookDatabase
import com.lianyi.paimonsnotebook.common.database.app_widget_binding.dao.AppWidgetBindingDao

/*
* 桌面组件绑定相关数据仓库
* 集中管理桌面组件绑定相关的 DAO 访问
* */
object AppWidgetRepository {
    private val database by lazy {
        PaimonsNotebookDatabase.database
    }

    val appWidgetBindingDao: AppWidgetBindingDao by lazy {
        database.appWidgetBindingDao
    }
}
