package com.lianyi.paimonsnotebook.ui.screen.app_widget.util

import com.lianyi.paimonsnotebook.ui.screen.app_widget.data.edit.AppWidgetEditData

class AppWidgetEditHistoryManager {

    private var currentHistoryIndex = 0
    private val historyList = mutableListOf<AppWidgetEditData>()

    val canNext: Boolean get() = currentHistoryIndex < historyList.size - 1
    val canPrev: Boolean get() = currentHistoryIndex > 0

    fun record(data: AppWidgetEditData) {
        historyList += data.clone()
        currentHistoryIndex = historyList.size - 1
    }

    fun next(): AppWidgetEditData? {
        val index = currentHistoryIndex + 1
        if (index >= historyList.size) return null
        currentHistoryIndex = index
        return historyList[index]
    }

    fun prev(): AppWidgetEditData? {
        val index = currentHistoryIndex - 1
        if (index < 0) return null
        currentHistoryIndex = index
        return historyList[index]
    }
}
