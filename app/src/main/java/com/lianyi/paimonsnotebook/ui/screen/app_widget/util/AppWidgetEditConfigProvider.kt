package com.lianyi.paimonsnotebook.ui.screen.app_widget.util

import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.common.database.app_widget_binding.util.AppWidgetComponentType
import com.lianyi.paimonsnotebook.common.extension.string.errorNotify
import com.lianyi.paimonsnotebook.ui.screen.app_widget.data.edit.AppWidgetEditData
import com.lianyi.paimonsnotebook.ui.screen.app_widget.data.edit.config.AppWidgetEditValueConfigData
import com.lianyi.paimonsnotebook.ui.screen.app_widget.util.enums.ComponentAlignType
import com.lianyi.core.ui.theme.Error
import com.lianyi.paimonsnotebook.ui.theme.Primary
import com.lianyi.paimonsnotebook.ui.theme.Primary_3

object AppWidgetEditConfigProvider {

    const val invalidValue = -999f
    const val invalidStringValue = "-"

    val transformValueConfigMap = mapOf(
        "X" to createEditValueConfigData(
            min = 0f,
            max = 9999f,
            setProperty = { component -> component.baseComponent::x::set },
            getProperty = { it.baseComponent.x }),
        "Y" to createEditValueConfigData(
            min = 0f,
            max = 9999f,
            setProperty = { component -> component.baseComponent::y::set },
            getProperty = { it.baseComponent.y }),
        "W" to createEditValueConfigData(
            min = .5f,
            max = 9999f,
            setProperty = { component -> component.baseComponent::width::set },
            getProperty = { it.baseComponent.width }),
        "H" to createEditValueConfigData(
            min = .5f,
            max = 9999f,
            setProperty = { component -> component.baseComponent::height::set },
            getProperty = { it.baseComponent.height }),
        "R" to createEditValueConfigData(
            min = -360f,
            max = 360f,
            setProperty = { component -> component.baseComponent::rotate::set },
            getProperty = { it.baseComponent.rotate }),
    )

    val textValueConfigMap = mapOf(
        "字号" to createEditValueConfigData(
            min = 0f,
            max = 100f,
            setProperty = { component ->
                component.text?.let { it::textSize::set }
                    ?: { _: Float -> "对象异常,数值设置失败".errorNotify(false) }
            },
            getProperty = { it.text?.textSize ?: invalidValue }),
        "间距" to AppWidgetEditValueConfigData(
            min = 0f,
            max = 100f,
            onValueChange = { component ->
                component.text?.let { it::textSpacer::set }
                    ?: { _: Float -> "对象异常,数值设置失败".errorNotify(false) }
            },
            getProperty = { it.text?.textSpacer ?: invalidValue })
    )

    val historyActionButtons = listOf(
        "撤销" to R.drawable.ic_arrow_undo_1,
        "复原" to R.drawable.ic_arrow_redo_1
    )

    val componentActionButtons = listOf(
        Triple("添加", R.drawable.ic_add, Primary),
        Triple("复制", R.drawable.ic_copy_outline, Primary_3),
        Triple("删除", R.drawable.ic_delete, Error)
    )

    val addableComponent = listOf(
        "文本" to AppWidgetComponentType.Text,
        "图片" to AppWidgetComponentType.Image,
        "进度条" to AppWidgetComponentType.ProgressBar,
        "线条" to AppWidgetComponentType.Line,
        "矩形" to AppWidgetComponentType.Rectangle,
        "三角形" to AppWidgetComponentType.Triangle,
        "圆形" to AppWidgetComponentType.Circle,
    )

    val alignButtons = listOf(
        R.drawable.ic_align_top_outline to ComponentAlignType.Top,
        R.drawable.ic_align_center_vertical_outline to ComponentAlignType.CenterVertical,
        R.drawable.ic_align_bottom_outline to ComponentAlignType.Bottom,
        R.drawable.ic_align_left_outline to ComponentAlignType.Start,
        R.drawable.ic_align_center_horizontal_outline to ComponentAlignType.CenterHorizontal,
        R.drawable.ic_align_right_outline to ComponentAlignType.End,
        R.drawable.ic_align_both_horizontal_outline to ComponentAlignType.BothHorizontal,
        R.drawable.ic_align_both_vertical_outline to ComponentAlignType.BothVertical
    )

    val textStyleButtons = listOf(
        R.drawable.ic_text_bold,
        R.drawable.ic_text_italic_1,
        R.drawable.ic_text_underline_1,
        R.drawable.ic_text_strike_through_1
    )

    private fun createEditValueConfigData(
        min: Float,
        max: Float,
        getProperty: (AppWidgetEditData.Component) -> Float,
        setProperty: (AppWidgetEditData.Component) -> (Float) -> Unit
    ) = AppWidgetEditValueConfigData(
        min = min,
        max = max,
        onValueChange = setProperty,
        getProperty = getProperty
    )
}
