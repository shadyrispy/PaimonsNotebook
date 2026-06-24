package com.lianyi.paimonsnotebook.ui.screen.cultivate_project.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.lianyi.paimonsnotebook.common.components.dialog.ConfirmDialog
import com.lianyi.paimonsnotebook.common.components.dialog.LazyColumnDialog
import com.lianyi.paimonsnotebook.common.components.lazy.ContentSpacerLazyColumn
import com.lianyi.paimonsnotebook.common.core.base.BaseActivity
import com.lianyi.paimonsnotebook.common.extension.modifier.radius.radius
import com.lianyi.paimonsnotebook.ui.screen.cultivate_project.viewmodel.CultivateProjectOptionScreenViewModel
import com.lianyi.paimonsnotebook.ui.screen.setting.components.SettingOptionGroup
import com.lianyi.core.ui.theme.BackGroundColor
import com.lianyi.paimonsnotebook.ui.theme.PaimonsNotebookTheme
import com.lianyi.paimonsnotebook.ui.theme.RadiusMedium

class CultivateProjectOptionScreen : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this)[CultivateProjectOptionScreenViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init(intent)
        viewModel.load()

        setContent {
            PaimonsNotebookTheme(this) {
                ContentSpacerLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackGroundColor)
                        .padding(12.dp, 8.dp)
                ) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SettingOptionGroup(
                                groupName = "养成计划",
                                list = viewModel.cultivateProjectSettings
                            )
                            SettingOptionGroup(
                                groupName = "界面",
                                list = viewModel.cultivateProjectLayout
                            )
                            SettingOptionGroup(
                                groupName = "养成计划操作",
                                list = viewModel.cultivateProjectActions
                            )
                        }
                    }
                }

                if (viewModel.showChooseCultivateProjectDialog) {
                    LazyColumnDialog(
                        title = "选择一个养成计划",
                        onDismissRequest = viewModel::dismissChooseCultivateProjectDialog,
                        content = {
                            items(viewModel.cultivateProjectList, key = { it.projectId }) {
                                Text(text = it.projectName, modifier = Modifier
                                    .radius(RadiusMedium)
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onSelectedProject(it)
                                    }
                                    .padding(8.dp)
                                )
                            }
                        }, onClickButton = viewModel::onClickChooseCultivateProjectDialogButton
                    )
                }

                if (viewModel.showConfirmDeleteCultivateProjectDialog) {
                    ConfirmDialog(
                        title = "删除养成计划",
                        content = "你确定要删除养成计划[${viewModel.dialogSelectedCultivateProject?.projectName}]吗?",
                        onConfirm = viewModel::confirmDeleteChooseCultivateProject,
                        onCancel = viewModel::dismissConfirmDeleteCultivateProjectDialog
                    )
                }

                if (viewModel.showConfirmDeleteCultivateProjectSuccessEntityDialog) {
                    ConfirmDialog(
                        title = "删除完成的养成项",
                        content = "你确定要从当前养成计划[${viewModel.currentSelectedProject?.projectName}]删除所有材料收集完毕的养成项吗?",
                        onConfirm = viewModel::confirmDeleteCultivateProjectSuccessEntity,
                        onCancel = viewModel::dismissConfirmDeleteCultivateProjectSuccessEntityDialog
                    )
                }
            }
        }
    }
}