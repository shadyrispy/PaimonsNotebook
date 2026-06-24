package com.lianyi.paimonsnotebook.ui.screen.cultivate_project.data

import com.lianyi.paimonsnotebook.common.web.hutao.genshin.item.Material
import com.lianyi.core.ui.theme.Success
import com.lianyi.core.ui.theme.Warning
data class MaterialBaseInfo(
    val material: Material,
    val count: Int,
    val availableCount: Int,
    val lackCount: Int = count - availableCount
) {
    fun getShowContentAndColor() =
        if (lackCount <= 0) {
            "完成" to Success
        } else {
            "$lackCount" to Warning
        }
}
