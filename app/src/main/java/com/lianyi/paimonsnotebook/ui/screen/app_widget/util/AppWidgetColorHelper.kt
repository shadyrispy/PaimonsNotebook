package com.lianyi.paimonsnotebook.ui.screen.app_widget.util

import android.graphics.PointF
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.lianyi.paimonsnotebook.ui.screen.app_widget.data.AppWidgetConfigurationData
import com.lianyi.paimonsnotebook.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppWidgetColorHelper(
    private val configuration: AppWidgetConfigurationData,
    private val showColorPickerPopup: () -> Unit,
    private val dismissColorPickerPopup: () -> Unit,
) {
    var textColorSelectedIndex by mutableIntStateOf(1)
        private set

    var imageTintColorSelectedIndex by mutableIntStateOf(1)
        private set

    var backgroundColorSelectedIndex by mutableIntStateOf(1)
        private set

    private var colorPickerType = ColorPickerType.None

    fun updateTextColorSelectedIndex(index: Int) {
        textColorSelectedIndex = index
    }

    fun updateImageTintColorSelectedIndex(index: Int) {
        imageTintColorSelectedIndex = index
    }

    fun changeBackgroundColor(color: Color, index: Int, scope: CoroutineScope) {
        if (index == 0) {
            showColorPickerPopup()
            colorPickerType = ColorPickerType.Background
        }

        backgroundColorSelectedIndex = index

        scope.launch {
            configuration.setBackgroundColor(color)
        }
    }

    fun changeTextColor(color: Color, index: Int, scope: CoroutineScope) {
        if (index == 0) {
            showColorPickerPopup()
            colorPickerType = ColorPickerType.Text
        }
        textColorSelectedIndex = index

        scope.launch {
            configuration.setTextColor(color)
        }
    }

    fun changeImageTintColor(color: Color, index: Int, scope: CoroutineScope) {
        if (index == 0) {
            showColorPickerPopup()
            colorPickerType = ColorPickerType.Image
        }

        imageTintColorSelectedIndex = index

        scope.launch {
            configuration.setImageTintColor(color)
        }
    }

    fun changeBackgroundRadius(float: Float, scope: CoroutineScope) {
        scope.launch {
            configuration.setBackgroundRadius(float)
        }
    }

    fun onColorPickerSelectedColor(color: Color, pointF: PointF, scope: CoroutineScope) {
        when (colorPickerType) {
            ColorPickerType.Image -> {
                changeImageTintColor(color, 0, scope)
                configuration.customImageTintColor = color
            }

            ColorPickerType.Text -> {
                changeTextColor(color, 0, scope)
                configuration.customTextColor = color
            }

            ColorPickerType.Background -> {
                changeBackgroundColor(color, 0, scope)
                configuration.customBackgroundColor = color
            }

            else -> {}
        }
        dismissColorPickerPopup()
    }

    fun getColorPickerPopupInitialColor(): Color =
        when (colorPickerType) {
            ColorPickerType.Image -> {
                configuration.customImageTintColor
            }

            ColorPickerType.Text -> {
                configuration.customTextColor
            }

            ColorPickerType.Background -> {
                configuration.customBackgroundColor
            }

            else -> {
                White
            }
        }
}
