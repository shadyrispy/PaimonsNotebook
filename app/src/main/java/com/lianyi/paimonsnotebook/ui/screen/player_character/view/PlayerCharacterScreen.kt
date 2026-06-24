package com.lianyi.paimonsnotebook.ui.screen.player_character.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.lianyi.core.ui.components.text.PrimaryText
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.common.components.layout.column.TopSlotColumnLayout
import com.lianyi.paimonsnotebook.common.components.lazy.ContentSpacerLazyColumn
import com.lianyi.paimonsnotebook.common.components.loading.ContentLoadingLayout
import com.lianyi.paimonsnotebook.common.components.loading.ContentLoadingPlaceholder
import com.lianyi.paimonsnotebook.common.components.placeholder.ErrorPlaceholder
import com.lianyi.paimonsnotebook.common.components.widget.InputTextFiled
import com.lianyi.paimonsnotebook.common.core.base.BaseActivity
import com.lianyi.paimonsnotebook.common.extension.modifier.radius.radius
import com.lianyi.paimonsnotebook.ui.screen.account.components.dialog.UserGameRolesDialog
import com.lianyi.paimonsnotebook.ui.screen.items.components.search.ItemSortFilterSheet
import com.lianyi.paimonsnotebook.ui.screen.player_character.components.card.PlayerCharacterListCard
import com.lianyi.paimonsnotebook.ui.screen.player_character.viewmodel.PlayerCharacterScreenViewModel
import com.lianyi.core.ui.theme.BlurCardBackgroundColor
import com.lianyi.paimonsnotebook.ui.theme.PaimonsNotebookTheme
import com.lianyi.paimonsnotebook.ui.theme.RadiusMedium

class PlayerCharacterScreen : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this)[PlayerCharacterScreenViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaimonsNotebookTheme(this) {

                ContentLoadingLayout(loadingState = viewModel.loadingState,
                    errorContent = {
                        ErrorPlaceholder("加载失败,请稍后再试")
                    },
                    loadingContent = {
                        ContentLoadingPlaceholder()
                    }
                ) {
                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current

                    ItemSortFilterSheet(viewModel.itemFilterViewModel) {
                    TopSlotColumnLayout(
                        topSlot = {
                            Row(
                                Modifier.padding(12.dp, 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 左: 账号切换
                                Row(
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .radius(RadiusMedium)
                                        .clickable {
                                            viewModel.showChooseGameRoleDialog()
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_user_outline),
                                        contentDescription = "切换账号",
                                        modifier = Modifier.size(24.dp)
                                    )

                                    PrimaryText(
                                        text = viewModel.currentGameRole?.nickname ?: "旅行者"
                                    )
                                }

                                // 右: 搜索框 + 筛选按钮
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    InputTextFiled(
                                        value = viewModel.itemFilterViewModel.inputNameValue,
                                        onValueChange = viewModel.itemFilterViewModel::onInputTextNameValueChange,
                                        backgroundColor = BlurCardBackgroundColor,
                                        borderRadius = 2.dp,
                                        modifier = Modifier
                                            .height(36.dp)
                                            .weight(1f),
                                        contentAlignment = Alignment.CenterStart,
                                        placeholder = "搜索角色"
                                    )

                                    if (viewModel.itemFilterViewModel.showClearFilter) {
                                        IconButton(
                                            onClick = { viewModel.itemFilterViewModel.resetFilter() },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_dismiss_circle_full),
                                                contentDescription = "清除",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                            viewModel.itemFilterViewModel.toggleSortFilterPanel()
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_filter),
                                            contentDescription = "筛选",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        ContentSpacerLazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(12.dp, 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            statusBarPaddingEnabled = false
                        ) {

                            items(viewModel.itemFilterViewModel.itemList, key = { it.id }) { avatarData ->
                                val characterData = viewModel.getCharacterListDataById(avatarData.id)
                                if (characterData != null) {
                                    PlayerCharacterListCard(
                                        characterData = characterData,
                                        getAvatarDataById = viewModel::getAvatarDataById,
                                        getWeaponDataById = viewModel::getWeaponDataById,
                                        getWeaponFightPropertyFormatList = viewModel::getWeaponFightPropertyFormatList,
                                        onClick = viewModel::onClickListItem
                                    )
                                }
                            }
                        }
                    }
                }
                }

                if (viewModel.showGameRoleDialog) {
                    UserGameRolesDialog(
                        onButtonClick = viewModel::onUserGameRoleDialogButtonClick,
                        onDismissRequest = viewModel::onUserGameRoleDialogDismissRequest,
                        onSelectRole = viewModel::onSelectedGameRole
                    )
                }
            }
        }
    }
}
