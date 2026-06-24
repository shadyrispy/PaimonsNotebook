package com.lianyi.paimonsnotebook.ui.screen.items.components.content

import androidx.compose.animation.Crossfade
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.lianyi.paimonsnotebook.common.components.spacer.StatusBarPaddingSpacer
import com.lianyi.paimonsnotebook.common.util.enums.ListLayoutStyle
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.list_card.ItemGridListCard
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.list_card.ItemListCard
import com.lianyi.paimonsnotebook.ui.screen.items.components.search.ItemFilterListContent
import com.lianyi.paimonsnotebook.ui.screen.items.components.search.ItemListTopBar
import com.lianyi.paimonsnotebook.ui.screen.items.components.search.ItemSortFilterSheet
import com.lianyi.paimonsnotebook.ui.screen.items.data.ItemListCardData
import com.lianyi.paimonsnotebook.ui.screen.items.util.ItemFilterType
import com.lianyi.paimonsnotebook.ui.screen.items.viewmodel.filter.ItemFilterViewModel
import com.lianyi.core.ui.theme.BackGroundColor
/*
* 物品界面内容
* 支持双视图模式: LIST(列表+搜索+筛选) / DETAIL(详情)
*
* LIST 视图参数直接传入，DETAIL 视图内容通过 detailContent lambda 延迟求值，
* 避免 LIST 视图下访问 currentItem 导致 NPE。
* */
@Composable
internal fun <T> ItemScreenContent(
    itemFilterViewModel: ItemFilterViewModel<T>,
    listButtonText: String,
    getListItemDataContent: (T, ItemFilterType, Boolean) -> String,
    getItemListCardData: (T) -> ItemListCardData,
    onClickListItemCard: (T) -> Unit,
    listVerticalStartInformationContentSlot: @Composable (T) -> Unit = {},
    listVerticalEndInformationContentSlot: @Composable (T) -> Unit = {},
    getItemName: (T) -> String = { "" },

    verticalListCardContent: @Composable (T, String) -> Unit = { data, dataContent ->
        ItemListCard(
            data = data,
            dataContent = dataContent,
            onClick = onClickListItemCard,
            itemListCardData = getItemListCardData.invoke(data),
            startInformationContentSlot = {
                listVerticalStartInformationContentSlot.invoke(data)
            },
            endInformationContentSlot = {
                listVerticalEndInformationContentSlot.invoke(data)
            }
        )
    },
    horizontalListCardContent: @Composable (T, String) -> Unit = { data, dataContent ->
        ItemGridListCard(
            data = data,
            dataContent = dataContent,
            onClick = onClickListItemCard,
            itemListCardData = getItemListCardData.invoke(data),
        )
    },

    // DETAIL 视图返回回调，默认切回 LIST；传入则覆盖（如 finish 关闭 Activity）
    onDetailBack: (() -> Unit)? = null,

    // DETAIL 视图整体内容，延迟求值，只在 DETAIL 状态时执行
    detailContent: @Composable () -> Unit
) {
    ItemSortFilterSheet(itemFilterViewModel) {
        // 详情视图时拦截返回手势
        BackHandler(
            enabled = itemFilterViewModel.viewState == ItemFilterViewModel.ViewState.DETAIL
        ) {
            onDetailBack?.invoke() ?: itemFilterViewModel.showListView()
        }

        Crossfade(
            targetState = itemFilterViewModel.viewState,
            label = "itemViewState"
        ) { state ->

            when (state) {

                ItemFilterViewModel.ViewState.LIST -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackGroundColor)
                    ) {
                        StatusBarPaddingSpacer()
                        ItemListTopBar(itemFilterViewModel)
                        ItemFilterListContent(
                            itemFilterViewModel = itemFilterViewModel,
                            itemSlot = { data, layoutStyle, type ->
                                val dataContent = remember(data) {
                                    getListItemDataContent.invoke(
                                        data, type,
                                        layoutStyle == ListLayoutStyle.ListVertical
                                    )
                                }
                                when (layoutStyle) {
                                    ListLayoutStyle.ListVertical -> {
                                        verticalListCardContent.invoke(data, dataContent)
                                    }
                                    ListLayoutStyle.GridVertical -> {
                                        // 网格模式强制用名称
                                        horizontalListCardContent.invoke(data, getItemName(data))
                                    }
                                    else -> {}
                                }
                            }
                        )
                    }
                }

                ItemFilterViewModel.ViewState.DETAIL -> {
                    detailContent()
                }
            }
        }
    }
}
