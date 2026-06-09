package com.lianyi.paimonsnotebook.common.web.hutao.genshin.intrinsic

/*
* 地区
* 对齐 Snap.Hutao.Remastered 的 AssociationType (src/Snap.Hutao.Remastered/Model/Intrinsic/AssociationType.cs)
* 新增 5.x 后的国家: ASSOC_TYPE_SNEZHNAYA, ASSOC_TYPE_OMNI_SCOURGE, ASSOC_TYPE_NODKRAI, ASSOC_TYPE_NODKRAI_ZIBAI
* */
object AssociationType {

    fun getAssociationNameByType(type: Int) =
        when (type) {
            ASSOC_TYPE_MONDSTADT -> "蒙德"
            ASSOC_TYPE_LIYUE -> "璃月"
            ASSOC_TYPE_INAZUMA -> "稻妻"
            ASSOC_TYPE_SUMERU -> "须弥"
            ASSOC_TYPE_FONTAINE -> "枫丹"
            ASSOC_TYPE_NATLAN -> "纳塔"
            ASSOC_TYPE_SNEZHNAYA -> "至冬"
            ASSOC_TYPE_NODKRAI -> "挪德卡莱"
            ASSOC_TYPE_FATUI-> "愚人众"
            else -> ""
        }

    //无
    const val ASSOC_TYPE_NONE = 0

    //蒙德
    const val ASSOC_TYPE_MONDSTADT = 1

    //璃月
    const val ASSOC_TYPE_LIYUE = 2

    //主角
    const val ASSOC_TYPE_MAINACTOR = 3

    //愚人众
    const val ASSOC_TYPE_FATUI = 4

    //稻妻
    const val ASSOC_TYPE_INAZUMA = 5

    //游侠
    const val ASSOC_TYPE_RANGER = 6

    //须弥
    const val ASSOC_TYPE_SUMERU = 7

    //枫丹
    const val ASSOC_TYPE_FONTAINE = 8

    //纳塔
    const val ASSOC_TYPE_NATLAN = 9

    //至冬 (5.x 国家)
    const val ASSOC_TYPE_SNEZHNAYA = 10

    // 降临者/深渊 (5.x 之后的国家, Snap.Hutao 当前无对应图标, 显示 GoldenAppleIsles 兜底)
    const val ASSOC_TYPE_OMNI_SCOURGE = 11

    // 挪德卡莱 (5.x 最新国家, Snap.Hutao 已支持枚举, 等待 api.snaphutaorp.org CDN 上传图标)
    // 路径: https://api.snaphutaorp.org/static/raw/ChapterIcon/UI_ChapterIcon_Nodkrai.png
    // 状态: 2026-06-03 实测 404 (CDN 还没更新), 但代码预留好, CDN 上传后零改动可用
    const val ASSOC_TYPE_NODKRAI = 12

    // 挪德卡莱·至白 (Nodkrai 子区域, 5.x 之后)
    const val ASSOC_TYPE_NODKRAI_ZIBAI = 13
}