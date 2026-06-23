package com.lianyi.paimonsnotebook.common.data.repository

import com.lianyi.paimonsnotebook.common.database.PaimonsNotebookDatabase
import com.lianyi.paimonsnotebook.common.database.daily_note.dao.DailyNoteDao
import com.lianyi.paimonsnotebook.common.database.daily_note.dao.DailyNoteWidgetDao

/*
* 实时便笺相关数据仓库
* 集中管理实时便笺相关的 DAO 访问
* */
object DailyNoteRepository {
    private val database by lazy {
        PaimonsNotebookDatabase.database
    }

    val dailyNoteDao: DailyNoteDao by lazy {
        database.dailyNoteDao
    }

    val dailyNoteWidgetDao: DailyNoteWidgetDao by lazy {
        database.dailyNoteWidgetDao
    }
}
