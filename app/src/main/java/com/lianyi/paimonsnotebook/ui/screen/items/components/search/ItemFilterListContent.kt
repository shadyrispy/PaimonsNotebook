package com.lianyi.paimonsnotebook.ui.screen.items.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lianyi.paimonsnotebook.common.components.lazy.ContentSpacerLazyColumn
import com.lianyi.paimonsnotebook.common.components.lazy.ContentSpacerLazyVerticalGrid
import com.lianyi.paimonsnotebook.common.util.enums.ListLayoutStyle
import com.lianyi.paimonsnotebook.ui.screen.items.util.ItemFilterType
import com.lianyi.paimonsnotebook.ui.screen.items.viewmodel.filter.ItemFilterViewModel

@Composable
internal fun <T> ItemFilterListContent(
    itemFilterViewModel: ItemFilterViewModel<T>,
    itemSlot: @Composable (data: T, layoutStyle: ListLayoutStyle, type: ItemFilterType) -> Unit
) {
    when (itemFilterViewModel.itemListLayoutStyle) {
        ListLayoutStyle.ListVertical -> {
            ContentSpacerLazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp),
                state = itemFilterViewModel.lazyListState,
                statusBarPaddingEnabled = false
            ) {
                items(
                    itemFilterViewModel.itemList,
                    key = { System.identityHashCode(it) }
                ) {
                    itemSlot.invoke(
                        it,
                        itemFilterViewModel.itemListLayoutStyle,
                        itemFilterViewModel.currentOrderByKeyType
                    )
                }
            }
        }

        ListLayoutStyle.GridVertical -> {
            ContentSpacerLazyVerticalGrid(
                columns = GridCells.Adaptive(60.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize(),
                state = itemFilterViewModel.lazyGridState,
                statusBarPaddingEnabled = false
            ) {
                items(
                    itemFilterViewModel.itemList,
                    key = { System.identityHashCode(it) }
                ) {
                    itemSlot.invoke(
                        it,
                        itemFilterViewModel.itemListLayoutStyle,
                        itemFilterViewModel.currentOrderByKeyType
                    )
                }
            }
        }

        else -> {}
    }
}
