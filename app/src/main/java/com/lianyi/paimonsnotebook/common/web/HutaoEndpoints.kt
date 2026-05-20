package com.lianyi.paimonsnotebook.common.web

import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment

/*
*
* */
object HutaoEndpoints {
    // 更新：使用新的元数据基础 URL
    private const val HutaoMetadataBaseUrl = "https://hutao-metadata-pages.snapgenshin.cn"
    
    // 静态资源 URL（根据您的说明和示例）
    // 示例: https://api.snaphutaorp.org/static/raw/AvatarIcon/UI_AvatarIcon_Side_None.png
    private const val ApiSnapHutao = "https://api.snaphutaorp.org"
    
    // 元数据 API 路径
    private const val ApiSnapGenshinMetadata = "${HutaoMetadataBaseUrl}/Genshin"
    
    // 静态资源路径（使用 api.snaphutaorp.org）
    const val ApiSnapGenshinStaticRaw = "${ApiSnapHutao}/static/raw"
    const val ApiSnapGenshinStaticZip = "${ApiSnapHutao}/static/zip"
    
    private const val Host = "hutao-metadata-pages.snapgenshin.cn"

    //请求元数据时的header
    val Headers by lazy {
        okhttp3.Headers.Builder()
            .add("User-Agent", CoreEnvironment.PaimonsNotebookUA)
            .add("Host", Host)
            .build()
    }

    /// <summary>
    /// 胡桃元数据2文件
    /// </summary>
    /// <param name="locale">语言</param>
    /// <param name="fileName">文件名称</param>
    /// <returns>路径</returns>
    fun metadata(locale: String, fileName: String): String =
        "${ApiSnapGenshinMetadata}/${locale}/${fileName}"

    /// <summary>
    /// 图片资源
    /// </summary>
    /// <param name="category">分类</param>
    /// <param name="fileName">文件名称 包括后缀</param>
    /// <returns>路径</returns>
    fun staticRaw(category: String, fileName: String): String =
        "${ApiSnapGenshinStaticRaw}/${category}/${fileName}"

    fun staticZip(fileName: String): String = "${ApiSnapGenshinStaticZip}/${fileName}.zip"
}
