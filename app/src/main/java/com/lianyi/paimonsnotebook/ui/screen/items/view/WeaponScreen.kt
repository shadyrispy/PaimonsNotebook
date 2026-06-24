package com.lianyi.paimonsnotebook.ui.screen.items.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.common.components.dialog.ConfirmDialog
import com.lianyi.paimonsnotebook.common.components.dialog.LazyColumnDialog
import com.lianyi.paimonsnotebook.common.components.lazy.ContentSpacerLazyColumn
import com.lianyi.paimonsnotebook.common.components.media.FullScreenImage
import com.lianyi.paimonsnotebook.common.components.layout.blur_card.widget.ItemLevelSlider
import com.lianyi.paimonsnotebook.common.components.spacer.StatusBarPaddingSpacer
import com.lianyi.paimonsnotebook.ui.screen.items.components.content.ItemScreenContent
import com.lianyi.paimonsnotebook.ui.screen.items.components.cultivate.WeaponCultivateConfigCard
import com.lianyi.paimonsnotebook.ui.screen.items.components.information.InformationItem
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.base.ItemBaseInfo
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.icon.ItemIconCard
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.material.ItemMaterialContent
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.property.ItemPropertyContent
import com.lianyi.paimonsnotebook.ui.screen.items.components.layout.ItemInformationCardLayout
import com.lianyi.paimonsnotebook.ui.screen.items.components.layout.ItemInformationContentLayout
import com.lianyi.paimonsnotebook.ui.screen.items.components.state.ItemScreenLoadingState
import com.lianyi.paimonsnotebook.ui.screen.items.components.widget.ItemActionButton
import com.lianyi.paimonsnotebook.ui.screen.items.components.widget.ItemScreenTopBar
import com.lianyi.paimonsnotebook.ui.screen.items.components.widget.ItemTabLayout
import com.lianyi.paimonsnotebook.ui.screen.items.data.ItemListCardData
import com.lianyi.paimonsnotebook.ui.screen.items.util.ItemHelper
import com.lianyi.paimonsnotebook.ui.screen.items.viewmodel.screen.WeaponScreenViewModel
import com.lianyi.core.ui.theme.Error
import com.lianyi.paimonsnotebook.ui.theme.PaimonsNotebookTheme
import androidx.compose.material.Text

class WeaponScreen : ComponentActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this)[WeaponScreenViewModel::class.java]
    }

    // 是否从外部直接进入详情（如养成材料页），此时返回应关闭 Activity
    private val isDeepLink by lazy {
        intent?.getIntExtra(ItemHelper.PARAM_INT_ITEM_ID, -1) != -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init(intent)

        setContent {
            PaimonsNotebookTheme(this, lightStatusBar = false) {
                ItemScreenLoadingState(loadingState = viewModel.loadingState) {

                    ItemScreenContent(
                        listButtonText = "武器列表",
                        itemFilterViewModel = viewModel.itemFilterViewModel,
                        getListItemDataContent = viewModel::getItemDataContent,
                        listVerticalEndInformationContentSlot = { weapon ->
                            InformationItem(
                                text = weapon.weaponTypeName,
                                iconUrl = weapon.weaponIconUrl,
                                paddingValues = PaddingValues(2.dp)
                            )
                        },
                        getItemListCardData = ItemListCardData::fromWeapon,
                        getItemName = { it.name },
                        onClickListItemCard = viewModel::onClickItem,
                        onDetailBack = if (isDeepLink) ({ finish() }) else null,
                    ) {
                        // detailContent — 只在 DETAIL 视图时执行，此时 currentItem 一定非 null
                        var showFullScreenImg by remember { mutableStateOf(false) }

                        ItemInformationContentLayout(
                            imgUrl = viewModel.currentItem!!.gachaEquipImg,
                            enabledItemShadow = true,
                            itemBackgroundResId = viewModel.currentItem!!.weaponGachaTypeBgResId,
                            itemImageContentScale = ContentScale.FillHeight,
                        ) {
                            val lazyListState = rememberLazyListState()

                            ContentSpacerLazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = lazyListState,
                                statusBarPaddingEnabled = false
                            ) {
                                item {
                                    Column {
                                        StatusBarPaddingSpacer()
                                        Spacer(
                                            modifier = Modifier
                                                .height(this@ItemInformationContentLayout.maxHeight * .6f)
                                                .background(Error)
                                        )
                                    }
                                }

                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp, 0.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Spacer(modifier = Modifier.width(1.dp))
                                        ItemActionButton(
                                            iconResId = R.drawable.ic_arrow_expand
                                        ) {
                                            showFullScreenImg = true
                                        }
                                    }
                                }

                                item {
                                    ItemInformationCardLayout {
                                        ItemBaseInfo(
                                            name = viewModel.currentItem!!.name,
                                            starCount = viewModel.currentItem!!.rankLevel,
                                            iconUrl = viewModel.currentItem!!.weaponIconUrl,
                                        )

                                        var currentTabIndex by remember {
                                            mutableIntStateOf(0)
                                        }

                                        ItemTabLayout(
                                            tabs = viewModel.tabs,
                                            currentIndex = currentTabIndex,
                                            onClick = { currentTabIndex = it }
                                        )

                                        CardContent(currentTabIndex)
                                    }
                                }
                            }

                            ItemScreenTopBar(
                                onClickListButton = if (isDeepLink) ({ finish() }) else viewModel::showListView,
                                iconResId = R.drawable.ic_arrow_left,
                                lazyListState = lazyListState,
                                text = if (isDeepLink) "返回" else "武器列表",
                                onClickAddButton = viewModel::addCurrentItemToCultivateProject,
                                added = viewModel.itemAddedToCurrentCultivateProject,
                            )

                            if (showFullScreenImg) {
                                FullScreenImage(url = viewModel.currentItem!!.gachaEquipImg) {
                                    showFullScreenImg = false
                                }
                            }
                        }
                    }
                }

                if (viewModel.showItemConfigDialog && viewModel.currentItem != null) {
                    LazyColumnDialog(
                        title = if (viewModel.itemAddedToCurrentCultivateProject) "更新当前养成计划" else "添加到当前养成计划",
                        buttons = viewModel.itemConfigDialogButtons,
                        onDismissRequest = viewModel::showItemConfigDialogRequestDismiss,
                        onClickButton = viewModel::onClickItemConfigDialogButton
                    ) {
                        item {
                            WeaponCultivateConfigCard(
                                weapon = viewModel.currentItem!!,
                                list = viewModel.cultivateConfigList
                            )
                        }
                    }
                }

                if (viewModel.showNoCultivateProjectNoticeDialog) {
                    ConfirmDialog(
                        title = "养成计划",
                        content = "没有找到养成计划,点击确定跳转养成计划设置页面进行添加",
                        onConfirm = viewModel::goCultivateProjectOptionScreen,
                        onCancel = viewModel::dismissNoCultivateProjectNoticeDialog
                    )
                }
            }
        }
    }

    @Composable
    fun CardContent(index: Int) {
        Crossfade(targetState = index, label = "") {
            when (it) {
                0 -> ItemPropertyContent(
                    iconUrl = viewModel.currentItem!!.iconUrl,
                    name = viewModel.currentItem!!.name,
                    maxLevel = viewModel.currentItem!!.maxLevel,
                    compareIconUrl = viewModel.compareItem?.iconUrl ?: "",
                    propertyList = viewModel.propertyList,
                    compareItemPropertyList = viewModel.compareItemPropertyList,
                    showPromotedButton = false,
                    onClickCompareItem = viewModel::onClickCompareItem,
                    onLevelChange = viewModel::onChangeItemLevel,
                    onPromotedChange = viewModel::onPromotedChange,
                    informationSlot = {
                        InformationItem(
                            iconUrl = viewModel.currentItem!!.weaponIconUrl,
                            iconSize = 20.dp,
                            textSize = 14.sp,
                            text = viewModel.currentItem!!.weaponTypeName
                        )
                    }
                )

                1 -> {
                    if (viewModel.weaponAffixFormat != null) {
                        Column {
                            ItemInformationCardLayout(margin = 0.dp, contentSpacer = 8.dp) {
                                Text(
                                    viewModel.currentItem!!.affix?.Name ?: "",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                com.lianyi.core.ui.components.text.RichText(text = viewModel.weaponAffixFormat!!.affixText)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            ItemLevelSlider(
                                value = viewModel.weaponAffixFormat!!.sliderValue,
                                level = viewModel.weaponAffixFormat!!.currentLevel,
                                onValueChange = viewModel.weaponAffixFormat!!::onSliderValueChange,
                                range = (1f..viewModel.weaponAffixFormat!!.maxLevel.toFloat()),
                            )
                        }
                    }
                }

                2 -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ItemIconCard(
                                url = viewModel.currentItem!!.iconUrl,
                                star = viewModel.currentItem!!.rankLevel,
                                borderRadius = 4.dp
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = viewModel.currentItem!!.description,
                                fontSize = 12.sp
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            ItemIconCard(
                                url = viewModel.currentItem!!.awakenIconUrl,
                                star = viewModel.currentItem!!.rankLevel,
                                borderRadius = 4.dp
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "${viewModel.currentItem!!.name} 突破后",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                3 -> {
                    ItemMaterialContent(list = viewModel.materialList)
                }
            }
        }
    }
}
