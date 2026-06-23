package com.lianyi.paimonsnotebook.common.data.repository

import com.lianyi.paimonsnotebook.common.database.PaimonsNotebookDatabase
import com.lianyi.paimonsnotebook.common.database.cultivate.dao.CultivateEntityDao
import com.lianyi.paimonsnotebook.common.database.cultivate.dao.CultivateItemMaterialsDao
import com.lianyi.paimonsnotebook.common.database.cultivate.dao.CultivateItemsDao
import com.lianyi.paimonsnotebook.common.database.cultivate.dao.CultivateProjectDao

/*
* 养成计划相关数据仓库
* 集中管理养成计划相关的 DAO 访问
* */
object CultivateRepository {
    private val database by lazy {
        PaimonsNotebookDatabase.database
    }

    val cultivateProjectDao: CultivateProjectDao by lazy {
        database.cultivateProjectDao
    }

    val cultivateEntityDao: CultivateEntityDao by lazy {
        database.cultivateEntityDao
    }

    val cultivateItemsDao: CultivateItemsDao by lazy {
        database.cultivateItemsDao
    }

    val cultivateItemMaterialsDao: CultivateItemMaterialsDao by lazy {
        database.cultivateItemMaterialsDao
    }
}
