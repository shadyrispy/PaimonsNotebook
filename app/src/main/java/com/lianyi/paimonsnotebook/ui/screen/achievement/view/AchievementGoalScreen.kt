package com.lianyi.paimonsnotebook.ui.screen.achievement.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.common.components.dialog.ConfirmDialog
import com.lianyi.paimonsnotebook.common.components.layout.column.TopSlotColumnLayout
import com.lianyi.paimonsnotebook.common.components.lazy.ContentSpacerLazyColumn
import com.lianyi.paimonsnotebook.common.components.media.NetworkImage
import com.lianyi.paimonsnotebook.common.core.base.BaseActivity
import com.lianyi.paimonsnotebook.common.extension.modifier.radius.radius
import com.lianyi.paimonsnotebook.common.extension.scope.launchIO
import com.lianyi.paimonsnotebook.common.util.enums.ViewModelAction
import com.lianyi.paimonsnotebook.common.util.time.TimeHelper
import com.lianyi.paimonsnotebook.ui.screen.achievement.components.achievement.AchievementActionButton
import com.lianyi.paimonsnotebook.ui.screen.achievement.util.enums.AchievementEditActionType
import com.lianyi.paimonsnotebook.ui.screen.achievement.viewmodel.AchievementGoalScreenViewModel
import com.lianyi.core.ui.theme.BackGroundColor
import com.lianyi.core.ui.theme.Font_Normal
import com.lianyi.core.ui.theme.CardBackGroundColor_Light_1
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.lianyi.paimonsnotebook.ui.theme.RadiusSmall
import com.lianyi.paimonsnotebook.ui.theme.PaimonsNotebookTheme
import com.lianyi.core.ui.theme.Success
import com.lianyi.paimonsnotebook.ui.theme.RadiusLarge
class AchievementGoalScreen : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this)[AchievementGoalScreenViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //此处接收来自viewModel的操作
        lifecycleScope.launchIO {
            viewModel.viewModelActionFlow.collect {
                if (it == ViewModelAction.Finish) {
                    finish()
                }
            }
        }

        viewModel.init(intent)

        setContent {
            PaimonsNotebookTheme(this) {
                val state = rememberLazyListState()

                LaunchedEffect(viewModel.targetItemIndex) {
                    if (viewModel.targetItemIndex != -1) {
                        state.animateScrollToItem(index = viewModel.targetItemIndex)
                    }
                }

                TopSlotColumnLayout(
                    topSlot = {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            //顶端成就进度条的动画值
                            val progressValueAnim by animateFloatAsState(
                                targetValue = viewModel.goalFinishCount.toFloat() / viewModel.goalTotal,
                                label = "",
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Row {
                                    NetworkImage(
                                        url = viewModel.goalOverviewData?.goal?.iconUrl ?: "",
                                        modifier = Modifier.size(36.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        Text(text = "${viewModel.goalOverviewData?.goal?.name}")

                                        Spacer(modifier = Modifier.height(2.dp))

                                        com.lianyi.core.ui.components.text.InfoText(text = "${viewModel.goalFinishCount}/${viewModel.goalOverviewData?.total}")
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    //TODO 此处的编辑按钮应该放在更合适的位置
//                                    AchievementActionButton(resId = R.drawable.ic_checkmark_circle) {
//                                        viewModel.onAchievementActionInvoke(
//                                            AchievementEditActionType.AddAll
//                                        )
//                                    }
//
//                                    AchievementActionButton(resId = R.drawable.ic_dismiss_circle_outline) {
//                                        viewModel.onAchievementActionInvoke(
//                                            AchievementEditActionType.RemoveAll
//                                        )
//                                    }

                                    AchievementActionButton(
                                        resId = R.drawable.ic_arrow_sort,
                                        onClick = viewModel::switchSortType
                                    )


                                    AchievementActionButton(resId = R.drawable.ic_lock_outline) {
                                        viewModel.onAchievementActionInvoke(
                                            AchievementEditActionType.EnableInteraction
                                        )
                                    }

//                                    AchievementActionButton(resId = R.drawable.ic_dismiss) {
//                                        viewModel.onAchievementActionInvoke(
//                                            AchievementEditActionType.DoNotSave
//                                        )
//                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = progressValueAnim,
                                strokeCap = StrokeCap.Round,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_gemstone),
                                    contentDescription = "原石",
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${viewModel.goalEarnedPrimogems} / ${viewModel.goalTotalPrimogems}",
                                    fontSize = 11.sp,
                                    color = Font_Normal
                                )
                            }
                        }
                    }
                ) {
                    ContentSpacerLazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackGroundColor),
                        state = state,
                        statusBarPaddingEnabled = false,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {

                        items(viewModel.achievementList, key = { it.id }) { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .radius(RadiusLarge)
                                    .fillMaxWidth()
                                    .background(CardBackGroundColor_Light_1)
                                    .clickable {
                                        viewModel.onClickItem(item)
                                    }
                                    .padding(10.dp)
                            ) {
                                // 完成状态 ✓
                                if (viewModel.getItemStatus(item)) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_checkmark_circle_full),
                                        contentDescription = "已完成",
                                        modifier = Modifier.size(22.dp),
                                        tint = Success
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_circle_empty),
                                        contentDescription = "未完成",
                                        modifier = Modifier.size(22.dp),
                                        tint = Font_Normal
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // 第一行：原石 + 标题+版本 + 完成时间
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // 原石
                                        Image(
                                            painter = painterResource(id = R.drawable.icon_gemstone),
                                            contentDescription = "原石",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${item.finishReward.Count}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // 标题 + 版本号
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = item.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Text(
                                                text = "v${item.version}",
                                                fontSize = 9.sp,
                                                color = Font_Normal
                                            )
                                        }

                                        // 完成时间（仅完成时）
                                        if (viewModel.getItemStatus(item)) {
                                            val entity = remember {
                                                viewModel.getAchievementEntity(item.id)
                                            }
                                            Text(
                                                text = TimeHelper.getTime(entity?.timestamp ?: 0),
                                                fontSize = 10.sp,
                                                color = Font_Normal
                                            )
                                        }
                                    }

                                    // 第二行：描述
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 6.dp)
                                    ) {
                                    // 成就描述
                                    Text(
                                        text = item.description,
                                        fontSize = 10.sp,
                                        color = Font_Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                }
                            }
                        }
                    }
                }

                if (viewModel.showActionConfirmDialog) {
                    ConfirmDialog(
                        title = "成就操作",
                        content = viewModel.getConfirmAchievementShowContent(),
                        onConfirm = viewModel::onConfirmAchievementAction,
                        onCancel = viewModel::onAchievementDialogDismissRequest
                    )
                }
            }
        }
    }
}