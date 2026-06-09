package com.lianyi.paimonsnotebook.common.web.hutao.genshin.conveter

import com.lianyi.paimonsnotebook.common.web.hutao.genshin.intrinsic.AssociationType
import com.lianyi.paimonsnotebook.common.web.static_resources.StaticResourcesApiEndpoint

object AssociationIconConverter {

    //角色所属地区图标
    // 对齐 Snap.Hutao.Remastered 的 AssociationTypeIconConverter (src/Snap.Hutao.Remastered/UI/Xaml/Converter/AssociationTypeIconConverter.cs)
    fun avatarAssociationToUrl(association:Int):String {
        val icon = when(association){
            AssociationType.ASSOC_TYPE_INAZUMA-> "Inazuma"
            AssociationType.ASSOC_TYPE_MONDSTADT-> "Mengde"  // 注意: Snap.Hutao 官方拼法是 Mengde（小写d），不是 MengDe
            AssociationType.ASSOC_TYPE_LIYUE-> "Liyue"
            AssociationType.ASSOC_TYPE_SUMERU-> "Sumeru"
            AssociationType.ASSOC_TYPE_FONTAINE -> "Fontaine"
            AssociationType.ASSOC_TYPE_NATLAN -> "Natlan"
            // 5.x 国家, 预留; CDN 上线后零改动可用
            // ASSOC_TYPE_SNEZHNAYA / OMNI_SCOURGE / NODKRAI_ZIBAI: Snap.Hutao 用 default(null) 跳过, 我们用 GoldenAppleIsles 兜底
            AssociationType.ASSOC_TYPE_NODKRAI -> "Nodkrai"  // 路径: ChapterIcon/UI_ChapterIcon_Nodkrai.png, 当前 2026-06-03 CDN 404
            else -> "GoldenAppleIsles"
        }

        // 对齐 Snap.Hutao.Remastered 的 AssociationTypeIconConverter: 目录 ChapterIcon (不是 Bg/LoadingPic)
        // 完整 URL: https://api.snaphutaorp.org/static/raw/ChapterIcon/UI_ChapterIcon_{name}.png
        return StaticResourcesApiEndpoint.staticRaw("ChapterIcon","UI_ChapterIcon_${icon}.png")
    }
}