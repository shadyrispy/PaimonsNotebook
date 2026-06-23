package com.lianyi.paimonsnotebook.ui.screen.items.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.common.components.widget.InputTextFiled
import com.lianyi.paimonsnotebook.common.util.enums.ListLayoutStyle
import com.lianyi.paimonsnotebook.ui.screen.items.viewmodel.filter.ItemFilterViewModel
import com.lianyi.paimonsnotebook.ui.theme.BlurCardBackgroundColor

@Composable
internal fun ItemListTopBar(
    itemFilterViewModel: ItemFilterViewModel<*>,
    focusManager: FocusManager = LocalFocusManager.current,
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        InputTextFiled(
            value = itemFilterViewModel.inputNameValue,
            onValueChange = itemFilterViewModel::onInputTextNameValueChange,
            backgroundColor = BlurCardBackgroundColor,
            borderRadius = 2.dp,
            modifier = Modifier
                .height(36.dp)
                .weight(1f),
            contentAlignment = Alignment.CenterStart,
            placeholder = "按名称筛选"
        )

        if (itemFilterViewModel.showClearFilter) {
            IconButton(
                onClick = { itemFilterViewModel.resetFilter() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dismiss_circle_full),
                    contentDescription = "清除筛选",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (itemFilterViewModel.showLayoutToggle) {
            IconButton(
                onClick = {
                    val newStyle = if (itemFilterViewModel.itemListLayoutStyle == ListLayoutStyle.ListVertical)
                        ListLayoutStyle.GridVertical else ListLayoutStyle.ListVertical
                    itemFilterViewModel.setListLayoutStyle(newStyle)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (itemFilterViewModel.itemListLayoutStyle == ListLayoutStyle.ListVertical)
                            R.drawable.ic_grid else R.drawable.ic_list
                    ),
                    contentDescription = "切换布局",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        IconButton(
            onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
                itemFilterViewModel.toggleSortFilterPanel()
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "排序筛选",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
