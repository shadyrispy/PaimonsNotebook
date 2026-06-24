package com.lianyi.paimonsnotebook.ui.screen.player_character.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.common.components.lazy.ContentSpacerLazyColumn
import com.lianyi.paimonsnotebook.common.components.popup.IconTitleInformationPopupWindow
import com.lianyi.paimonsnotebook.common.components.spacer.StatusBarPaddingSpacer
import com.lianyi.paimonsnotebook.common.core.base.BaseActivity
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.intrinsic.ElementType
import com.lianyi.paimonsnotebook.ui.screen.items.components.information.InformationItem
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.base.ItemBaseInfo
import com.lianyi.paimonsnotebook.ui.screen.items.components.layout.ItemInformationCardLayout
import com.lianyi.paimonsnotebook.ui.screen.items.components.layout.ItemInformationContentLayout
import com.lianyi.paimonsnotebook.ui.screen.items.components.state.ItemScreenLoadingState
import com.lianyi.paimonsnotebook.ui.screen.items.components.widget.ItemScreenTopBar
import com.lianyi.paimonsnotebook.ui.screen.player_character.components.card.PlayerCharacterPropertyCard
import com.lianyi.paimonsnotebook.ui.screen.player_character.components.card.PlayerCharacterRelicCard
import com.lianyi.paimonsnotebook.ui.screen.player_character.components.card.item.PlayerCharacterDetailSkillCard
import com.lianyi.paimonsnotebook.ui.screen.player_character.components.card.item.PlayerCharacterDetailTalentCard
import com.lianyi.paimonsnotebook.ui.screen.player_character.components.card.item.PlayerCharacterDetailWeaponCard
import com.lianyi.paimonsnotebook.ui.screen.player_character.viewmodel.PlayerCharacterDetailScreenViewModel
import com.lianyi.core.ui.theme.Error
import com.lianyi.paimonsnotebook.ui.theme.FetterColor
import com.lianyi.paimonsnotebook.ui.theme.PaimonsNotebookTheme
import com.lianyi.core.ui.theme.White
import com.lianyi.core.ui.theme.White_40
class PlayerCharacterDetailScreen : BaseActivity() {

    companion object {
        //当前用户与uid
        const val PARAM_USER_AND_UID_JSON = "user_and_uid_json"

        //玩家角色的列表集合
        const val PARAM_CHARACTER_LIST_JSON = "character_list_json"

        const val PARAM_SELECTED_CHARACTER_ID = "selected_character_id"
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[PlayerCharacterDetailScreenViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init(intent) {
            finish()
        }

        setContent {
            PaimonsNotebookTheme(this) {

                ItemScreenLoadingState(loadingState = viewModel.loadingState) {

                    ItemInformationContentLayout(
                        imgUrl = viewModel.currentItem!!.gachaAvatarImg,
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
                                ItemInformationCardLayout {
                                    ItemBaseInfo(
                                        name = viewModel.currentItem!!.name,
                                        starCount = viewModel.currentItem!!.starCount,
                                        iconUrl = viewModel.currentItem!!.fetterInfo.associationIconUrl,
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Spacer(modifier = Modifier.width(1.dp))

                                            InformationItem(
                                                text = "Lv.${viewModel.currentCharacterDetail?.base?.level}",
                                                backgroundColor = White_40,
                                                textSize = 14.sp,
                                                paddingValues = PaddingValues(
                                                    6.dp,
                                                    2.dp
                                                )
                                            )

                                            InformationItem(
                                                text = "${viewModel.currentCharacterDetail?.base?.fetter}",
                                                iconResId = R.drawable.icon_fetter,
                                                backgroundColor = White_40,
                                                textSize = 14.sp,
                                                paddingValues = PaddingValues(
                                                    6.dp,
                                                    2.dp
                                                ),
                                                tint = FetterColor,
                                                textColor = FetterColor
                                            )
                                        }
                                    }

                                    DetailCardContent()
                                }
                            }
                        }

                        ItemScreenTopBar(
                            onClickListButton = { finish() },
                            iconResId = R.drawable.ic_arrow_left,
                            lazyListState = lazyListState,
                            text = "返回",
                            showAddButton = false,
                        )
                    }

                    if(viewModel.showReliquarySetInfoPopupWindow){
                        IconTitleInformationPopupWindow(
                            data = viewModel.reliquarySetInfoDataSet,
                            popupProvider = viewModel.reliquarySetInfoPopupWindowProvider,
                            onDismissRequest = viewModel::onPopupWindowDismissRequest
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun DetailCardContent() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            InformationItem(
                iconResId = ElementType.getElementResourceIdByName(viewModel.currentItem!!.fetterInfo.VisionBefore),
                textColor = White,
                iconSize = 20.dp,
                textSize = 14.sp,
                text = viewModel.currentItem!!.fetterInfo.VisionBefore,
                backgroundColor = ElementType.getElementColorByName(viewModel.currentItem!!.fetterInfo.VisionBefore),
            )

            InformationItem(
                iconUrl = viewModel.currentItem!!.weaponIconUrl,
                textColor = White,
                iconSize = 20.dp,
                textSize = 14.sp,
                text = viewModel.currentItem!!.weaponTypeName,
                backgroundColor = ElementType.getElementColorByName(viewModel.currentItem!!.fetterInfo.VisionBefore),
            )
        }

        val character by remember(viewModel.currentCharacterDetail?.base?.id) {
            mutableStateOf(viewModel.currentCharacterDetail)
        }
        val avatarData = viewModel.getAvatarDataById(character?.base?.id ?: -1)

        if (character != null && avatarData != null) {
            PlayerCharacterDetailSkillCard(
                skillDepot = avatarData.skillDepot,
                elementTypeName = character!!.base.element,
                skillLevelMap = character!!.skills.associate {
                    it.skill_id to it.level
                },
                backgroundColor = White_40,
            )

            PlayerCharacterDetailTalentCard(
                talents = avatarData.skillDepot.Talents,
                elementTypeName = character!!.base.element,
                activateCount = character!!.base.actived_constellation_num,
                backgroundColor = White_40,
                clickable = true
            )
        }

        PlayerCharacterPropertyCard(
            propertyList = viewModel.currentCharacterDetail?.selected_properties
                ?: listOf(),
            extraPropertyList = viewModel.currentCharacterDetail?.let {
                it.base_properties + it.extra_properties + it.element_properties
            } ?: listOf()
        )

        val weapon = viewModel.currentCharacterDetail?.weapon
        val weaponData = viewModel.getWeaponDataById(weapon?.id ?: -1)

        if (weaponData != null && weapon != null) {
            val list = remember(weapon.id, weapon.level) {
                viewModel.getWeaponFightPropertyFormatList(
                    weaponData = weaponData,
                    level = weapon.level,
                    promoted = true
                )
            }

            PlayerCharacterDetailWeaponCard(
                weaponData = weaponData,
                level = weapon.level,
                affixLevel = weapon.affix_level,
                weaponFightPropertyFormatList = list,
                backgroundColor = White_40,
                clickable = true
            )
        }

        val relics = viewModel.currentCharacterDetail?.relics
        val recommendRelicProperty =
            viewModel.currentCharacterDetail?.recommend_relic_property

        if (!relics.isNullOrEmpty() && recommendRelicProperty != null) {
            PlayerCharacterRelicCard(
                relicList = relics,
                getRelicById = viewModel::getRelicById,
                recommendRelicProperty = recommendRelicProperty,
                onClickRelicIcon = viewModel::onClickRelicIcon
            )
        }
    }
}
