package com.lianyi.paimonsnotebook.common.data.repository

import com.lianyi.paimonsnotebook.common.database.PaimonsNotebookDatabase
import com.lianyi.paimonsnotebook.common.database.disk_cache.dao.DiskCacheDao

/*
* 磁盘缓存相关数据仓库
* 集中管理磁盘缓存相关的 DAO 访问
* */
object DiskCacheRepository {
    private val database by lazy {
        PaimonsNotebookDatabase.database
    }

    val diskCacheDao: DiskCacheDao by lazy {
        database.diskCacheDao
    }
}
