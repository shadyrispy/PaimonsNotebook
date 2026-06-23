package com.lianyi.paimonsnotebook.common.data.repository

import androidx.sqlite.db.SupportSQLiteStatement
import com.lianyi.paimonsnotebook.common.database.PaimonsNotebookDatabase
import com.lianyi.paimonsnotebook.common.database.achievement.dao.AchievementUserDao
import com.lianyi.paimonsnotebook.common.database.achievement.dao.AchievementsDao

/*
* 成就相关数据仓库
* 集中管理成就相关的 DAO 访问
* */
object AchievementRepository {
    private val database by lazy {
        PaimonsNotebookDatabase.database
    }

    val achievementUserDao: AchievementUserDao by lazy {
        database.achievementUserDao
    }

    val achievementsDao: AchievementsDao by lazy {
        database.achievementsDao
    }

    /*
    * 编译 SQL 语句
    * 供需要原生 SQL 操作的 Service 使用
    * */
    fun compileStatement(sql: String): SupportSQLiteStatement {
        return database.compileStatement(sql)
    }
}
