package com.lianyi.paimonsnotebook.common.web.hutao.genshin.item

import java.time.LocalDateTime

object Materials {
    /// 周一/周四
    private val MondayThursdayItems by lazy {
        listOf(
            104301, 104302, 104303, // 「自由」
            104310, 104311, 104312, // 「繁荣」
            104320, 104321, 104322, // 「浮世」
            104329, 104330, 104331, // 「诤言」
            104338, 104339, 104340, // 「公平」
            104347, 104348, 104349, // 「角逐」
            104356, 104357, 104358, // 「月光」
            114001, 114002, 114003, 114004, // 高塔孤王
            114013, 114014, 114015, 114016, // 孤云寒林
            114025, 114026, 114027, 114028, // 远海夷地
            114037, 114038, 114039, 114040, // 谧林涓露
            114049, 114050, 114051, 114052, // 悠古弦音
            114061, 114062, 114063, 114064, // 贡祭炽心
            114073, 114074, 114075, 114076, // 奇巧秘器
        )
    }
    private val MondayThursdaySimpleItems by lazy {
        listOf(
            104303, // 「自由」
            104312, // 「繁荣」
            104322, // 「浮世」
            104331, // 「诤言」
            104340, // 「公平」
            104349, // 「角逐」
            104358, // 「月光」
            114004, // 高塔孤王
            114016, // 孤云寒林
            114028, // 远海夷地
            114040, // 谧林涓露
            114052, // 悠古弦音
            114064, // 贡祭炽心
            114076, // 奇巧秘器
        )
    }

    /// 周二/周五
    private val TuesdayFridayItems by lazy {
        listOf(
            104304, 104305, 104306, // 「抗争」
            104313, 104314, 104315, // 「勤劳」
            104323, 104324, 104325, // 「风雅」
            104332, 104333, 104334, // 「巧思」
            104341, 104342, 104343, // 「正义」
            104350, 104351, 104352, // 「焚燔」
            104359, 104360, 104361, // 「乐园」
            114005, 114006, 114007, 114008, // 凛风奔狼
            114017, 114018, 114019, 114020, // 雾海云间
            114029, 114030, 114031, 114032, // 鸣神御灵
            114041, 114042, 114043, 114044, // 绿洲花园
            114053, 114054, 114055, 114056, // 纯圣露滴
            114065, 114066, 114067, 114068, // 谵妄圣主
            114077, 114078, 114079, 114080, // 长夜燧火
        )
    }
    private val TuesdayFridaySimpleItems by lazy {
        listOf(
            104306, // 「抗争」
            104315, // 「勤劳」
            104325, // 「风雅」
            104334, // 「巧思」
            104343, // 「正义」
            104352, // 「焚燔」
            104361, // 「乐园」
            114008, // 凛风奔狼
            114020, // 雾海云间
            114032, // 鸣神御灵
            114044, // 绿洲花园
            114056, // 纯圣露滴
            114068, // 谵妄圣主
            114080, // 长夜燧火
        )
    }

    /// 周三/周六
    private val WednesdaySaturdayItems by lazy {
        listOf(
            104307, 104308, 104309, // 「诗文」
            104316, 104317, 104318, // 「黄金」
            104326, 104327, 104328, // 「天光」
            104335, 104336, 104337, // 「笃行」
            104344, 104345, 104346, // 「秩序」
            104353, 104354, 104355, // 「纷争」
            104362, 104363, 104364, // 「浪迹」
            114009, 114010, 114011, 114012, // 狮牙斗士
            114021, 114022, 114023, 114024, // 漆黑陨铁
            114033, 114034, 114035, 114036, // 今昔剧画
            114045, 114046, 114047, 114048, // 谧林涓露
            114057, 114058, 114059, 114060, // 无垢之海
            114069, 114070, 114071, 114072, // 神合秘烟
            114081, 114082, 114083, 114084, // 终北遗嗣
        )
    }
    private val WednesdaySaturdaySimpleItems by lazy {
        listOf(
            104309, // 「诗文」
            104318, // 「黄金」
            104328, // 「天光」
            104337, // 「笃行」
            104346, // 「秩序」
            104355, // 「纷争」
            104364, // 「浪迹」
            114012, // 狮牙斗士
            114024, // 漆黑陨铁
            114036, // 今昔剧画
            114048, // 谧林涓露
            114060, // 无垢之海
            114072, // 神合秘烟
            114084, // 终北遗嗣
        )
    }

    //角色提升宝石
    val AvatarPromotionGemSimpleItems = setOf(
        104104,
        104114,
        104124,
        104134,
        104144,
        104154,
        104164,
        104174
    )

    /*
    * 获取材料id列表
    * */
    fun getMaterialsIdByWeek(
        week: Int,
        ignoreHour: Boolean = false,
        simpleMode: Boolean = false
    ): Pair<List<Int>, Int> {
        val hour = LocalDateTime.now().hour + 1

        val realWeek = if (!ignoreHour && hour < 4) {
            (week - 1).let { value ->
                if (value == 0) {
                    7
                } else {
                    value
                }
            }
        } else {
            week
        }
        return if (simpleMode) {
            when (realWeek) {
                1, 4 -> MondayThursdaySimpleItems
                2, 5 -> TuesdayFridaySimpleItems
                3, 6 -> WednesdaySaturdaySimpleItems
                else -> {
                    MondayThursdaySimpleItems + TuesdayFridaySimpleItems + WednesdaySaturdaySimpleItems
                }
            }
        } else {
            when (realWeek) {
                1, 4 -> MondayThursdayItems
                2, 5 -> TuesdayFridayItems
                3, 6 -> WednesdaySaturdayItems
                else -> {
                    MondayThursdayItems + TuesdayFridayItems + WednesdaySaturdayItems
                }
            }
        } to realWeek
    }
}