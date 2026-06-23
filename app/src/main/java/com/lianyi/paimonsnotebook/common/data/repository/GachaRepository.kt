package com.lianyi.paimonsnotebook.common.data.repository

import androidx.sqlite.db.SupportSQLiteStatement
import com.lianyi.paimonsnotebook.common.database.PaimonsNotebookDatabase
import com.lianyi.paimonsnotebook.common.database.gacha.dao.GachaItemsDao

/*
* 祈愿记录相关数据仓库
* 集中管理祈愿记录相关的 DAO 访问
* */
object GachaRepository {
    private val database by lazy {
        PaimonsNotebookDatabase.database
    }

    val gachaItemsDao: GachaItemsDao by lazy {
        database.gachaItemsDao
    }

    /*
    * 编译 SQL 语句
    * 供需要原生 SQL 操作的 Service 使用
    * */
    fun compileStatement(sql: String): SupportSQLiteStatement {
        return database.compileStatement(sql)
    }
}
