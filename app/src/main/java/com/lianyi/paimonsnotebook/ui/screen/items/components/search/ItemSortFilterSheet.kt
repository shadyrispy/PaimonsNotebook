package com.lianyi.paimonsnotebook.ui.screen.items.components.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lianyi.paimonsnotebook.ui.screen.items.util.ItemFilterType
import com.lianyi.paimonsnotebook.ui.screen.items.viewmodel.filter.ItemFilterViewModel
import com.lianyi.paimonsnotebook.ui.theme.BackGroundColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ItemSortFilterSheet(
    itemFilterViewModel: ItemFilterViewModel<*>,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded }
    )

    LaunchedEffect(itemFilterViewModel.showSortFilterPanel) {
        if (itemFilterViewModel.showSortFilterPanel) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    // 弹窗关闭时同步状态
    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible && itemFilterViewModel.showSortFilterPanel) {
            itemFilterViewModel.dismissSortFilterPanel()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    itemFilterViewModel.searchOptionList.filter { pair ->
                        pair.second.first().sortBy != ItemFilterType.ListLayout
                    },
                    key = { it.first }
                ) { pair ->
                    SearchOptionGroup(
                        name = pair.first,
                        options = pair.second,
                        getOptionSelectState = itemFilterViewModel::getOptionSelectState,
                        onSelectedItem = itemFilterViewModel::onSelectOption
                    )
                }
            }
        },
        sheetBackgroundColor = BackGroundColor,
        content = content
    )
}
