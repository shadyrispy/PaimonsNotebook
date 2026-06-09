package com.lianyi.paimonsnotebook.common.web

import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment

/*
*
* */
object HutaoEndpoints {
    // 元数据 URL：自建 GitHub 仓 + jsDelivr CDN (2026-06-03 切换)
    // 仓库: https://github.com/shadyrispy/PaimonsNotebookResources/tree/main/metadata
    // 目录结构: metadata/Genshin/{locale}/*.json  (locale=CHS/RU/JP/EN/...)
    // 13 个 .json + Avatar/ 子目录，结构与原 Snap.Metadata 完全一致，调用方零改动
    // 验证可用: Meta.json / Achievement.json / Furniture.json / Avatar/10000002.json ...
    //   curl -I https://cdn.jsdelivr.net/gh/shadyrispy/PaimonsNotebookResources@main/metadata/Genshin/CHS/Achievement.json  -> 200
    //   curl -I https://cdn.jsdelivr.net/gh/shadyrispy/PaimonsNotebookResources@main/metadata/Genshin/CHS/Meta.json        -> 200
    // 注: 整个 metadata 包 < 10MB, jsDelivr 50MB 限制够用, 单文件访问不会被 301 重定向
    private const val HutaoMetadataBaseUrl = "https://cdn.jsdelivr.net/gh/shadyrispy/PaimonsNotebookResources@main/metadata"

    // 静态资源 URL：完全回退到 SnapHutaoRemasteringProject 官方 CDN (2026-06-03 修复)
    // 原因：自建仓 725MB 超过 jsDelivr 50MB 限制, 所有图片请求被 301 重定向到 raw.githubusercontent.com
    //       raw 域名国内被墙/限速 + Coil 不跟随跨域重定向, 导致图片全部失败
    // 路径模式 {Root}/static/raw/{category}/{fileName}  (完全对齐 Snap.Hutao.Remastered 的 StaticResourcesEndpoints)
    // Snap.Hutao 官方仓库 14 个分类齐全, 包括 GachaAvatarIcon/GachaAvatarImg/GachaEquipIcon/LoadingPic (我们硬编但仓里不存在的目录)
    private const val ApiSnapHutao = "https://api.snaphutaorp.org"

    // 元数据 API 路径
    private const val ApiSnapGenshinMetadata = "${HutaoMetadataBaseUrl}/Genshin"

    // 静态资源路径 (官方 CDN 模式: /static/raw/ 前缀)
    const val ApiSnapGenshinStaticRaw = "${ApiSnapHutao}/static/raw"
    // 静态 zip 批量下载
    const val ApiSnapGenshinStaticZip = "${ApiSnapHutao}/static/zip"

    //请求元数据/图片时的header (NetworkImageForMetadata 会用)
    val Headers by lazy {
        okhttp3.Headers.Builder()
            .add("User-Agent", CoreEnvironment.PaimonsNotebookUA)
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
