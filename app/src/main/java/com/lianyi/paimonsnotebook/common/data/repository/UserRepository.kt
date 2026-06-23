package com.lianyi.paimonsnotebook.common.data.repository

import com.lianyi.paimonsnotebook.common.database.PaimonsNotebookDatabase
import com.lianyi.paimonsnotebook.common.database.user.dao.UserDao

/*
* 用户相关数据仓库
* 集中管理用户相关的 DAO 访问
* */
object UserRepository {
    private val database by lazy {
        PaimonsNotebookDatabase.database
    }

    val userDao: UserDao by lazy {
        database.userDao
    }
}
