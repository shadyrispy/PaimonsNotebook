package com.lianyi.paimonsnotebook.ui.screen.app_widget.util

import com.lianyi.paimonsnotebook.common.extension.value.dpToPx
import com.lianyi.paimonsnotebook.ui.screen.app_widget.data.edit.AppWidgetEditData
import com.lianyi.paimonsnotebook.ui.screen.app_widget.data.edit.AppWidgetTransformData
import com.lianyi.paimonsnotebook.ui.screen.app_widget.util.enums.ComponentAlignType

object AppWidgetAlignmentHelper {

    private const val BASE_DENSITY = 720f

    fun align(components: List<AppWidgetEditData.Component>, type: ComponentAlignType) {
        val transform = getComponentsTransformData(components)

        val sortedXComponents = components.sortedBy { it.baseComponent.x }
        val sortedYComponents = components.sortedBy { it.baseComponent.y }

        when (type) {
            //取最小的x来对齐
            ComponentAlignType.Start -> {
                components.forEach { it.baseComponent.x = transform.minX }
            }

            //取最大的x与最小的x求平均值来获取中心值
            ComponentAlignType.CenterHorizontal -> {
                val lastBase = sortedXComponents.last().baseComponent
                val center = (transform.minX + transform.maxX + lastBase.width.localDpToPx()) / 2

                components.forEach {
                    val halfWidth = it.baseComponent.width.localDpToPx() / 2
                    it.baseComponent.x = center - halfWidth
                }
            }

            ComponentAlignType.CenterVertical -> {
                val lastBase = sortedYComponents.last().baseComponent
                val center = (transform.minY + transform.maxY + lastBase.height.localDpToPx()) / 2

                components.forEach {
                    val halfHeight = it.baseComponent.height.localDpToPx() / 2
                    it.baseComponent.y = center - halfHeight
                }
            }

            ComponentAlignType.End -> {
                val lastBase = sortedXComponents.last().baseComponent

                val endX = lastBase.x + lastBase.width.localDpToPx()

                components.forEach {
                    it.baseComponent.x = endX - it.baseComponent.width.localDpToPx()
                }
            }

            ComponentAlignType.Top -> {
                components.forEach { it.baseComponent.y = transform.minY }
            }

            ComponentAlignType.Bottom -> {
                val lastBase = sortedYComponents.last().baseComponent

                val endY = lastBase.y + lastBase.height.localDpToPx()

                components.forEach {
                    it.baseComponent.y = endY - it.baseComponent.height.localDpToPx()
                }
            }

            ComponentAlignType.BothHorizontal -> {
                val count = sortedXComponents.size - 1

                val lastBase = sortedXComponents.last().baseComponent

                val totalWidth = transform.maxX + lastBase.width.localDpToPx() - transform.minX
                val spacer = (totalWidth - transform.sumWidth.localDpToPx()) / count

                var offsetX = transform.minX

                sortedXComponents.forEach {
                    val base = it.baseComponent
                    base.x = offsetX
                    offsetX += spacer + base.width.localDpToPx()
                }
            }

            ComponentAlignType.BothVertical -> {
                val count = sortedYComponents.size - 1

                val lastBase = sortedYComponents.last().baseComponent

                val totalWidth = transform.maxY + lastBase.height.localDpToPx() - transform.minY
                val spacer = (totalWidth - transform.sumHeight.localDpToPx()) / count

                var offsetX = transform.minY

                sortedYComponents.forEach {
                    val base = it.baseComponent
                    base.y = offsetX
                    offsetX += spacer + base.height.localDpToPx()
                }
            }
        }
    }

    fun getComponentsTransformData(
        components: List<AppWidgetEditData.Component>
    ): AppWidgetTransformData {

        if (components.isEmpty()) return AppWidgetTransformData(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        val firstBase = components.first().baseComponent

        var maxX = firstBase.x
        var minX = firstBase.x
        var maxY = firstBase.y
        var minY = firstBase.y

        var maxWidth = firstBase.width
        var maxHeight = firstBase.height

        var sumWidth = 0f
        var sumHeight = 0f

        components.forEach { component ->
            val base = component.baseComponent

            if (maxX < base.x) {
                maxX = base.x
            }

            if (minX > base.x) {
                minX = base.x
            }

            if (maxY < base.y) {
                maxY = base.y
            }

            if (minY > base.y) {
                minY = base.y
            }

            if (maxWidth < base.width) {
                maxWidth = base.width
            }

            if (maxHeight < base.height) {
                maxHeight = base.height
            }

            sumWidth += base.width
            sumHeight += base.height
        }

        return AppWidgetTransformData(
            maxX = maxX,
            minX = minX,
            maxY = maxY,
            minY = minY,
            maxWidth = maxWidth,
            maxHeight = maxHeight,
            sumWidth = sumWidth,
            sumHeight = sumHeight
        )
    }

    private fun Float.localDpToPx() = this.dpToPx(BASE_DENSITY)
}
