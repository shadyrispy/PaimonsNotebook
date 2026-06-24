package com.lianyi.paimonsnotebook.ui.screen.gacha.provider

import android.content.Intent
import android.net.Uri
import com.lianyi.paimonsnotebook.common.application.PaimonsNotebookApplication
import com.lianyi.paimonsnotebook.common.extension.data_store.editValue
import com.lianyi.paimonsnotebook.common.extension.scope.launchIO
import com.lianyi.paimonsnotebook.common.extension.string.notify
import com.lianyi.paimonsnotebook.common.util.data_store.PreferenceKeys
import com.lianyi.paimonsnotebook.common.util.metadata.genshin.uigf.UIGFHelper
import com.lianyi.paimonsnotebook.ui.screen.gacha.components.GachaRecordAboutUIGFVersionSlot
import com.lianyi.paimonsnotebook.ui.screen.gacha.components.GachaRecordCurrentGameUidSlot
import com.lianyi.paimonsnotebook.ui.screen.gacha.components.GachaRecordExportArrowSlot
import com.lianyi.paimonsnotebook.ui.screen.gacha.components.GachaRecordExportV3SwitchSlot
import com.lianyi.paimonsnotebook.ui.screen.gacha.components.GachaRecordImportResultSlot
import com.lianyi.paimonsnotebook.ui.screen.gacha.components.GachaRecordInputUrlSlot
import com.lianyi.paimonsnotebook.ui.screen.gacha.components.GachaRecordSelectAccountSlot
import com.lianyi.paimonsnotebook.ui.screen.gacha.viewmodel.GachaRecordOptionScreenViewModel
import com.lianyi.paimonsnotebook.ui.screen.home.util.HomeHelper
import com.lianyi.paimonsnotebook.ui.screen.setting.data.OptionListData
import kotlinx.coroutines.launch

/**
 * 祈愿记录设置页的 UI 配置列表 Provider。
 *
 * 将 ViewModel 内嵌的 Composable 配置列表外移，保持 MVVM 分层。
 */
object GachaRecordOptionListProvider {

    /**
     * 祈愿记录设置项
     */
    fun gachaSettings(vm: GachaRecordOptionScreenViewModel) = listOf(
        OptionListData(
            name = "当前账号",
            description = "用于显示指定账号的祈愿记录",
            onClick = {
                if (vm.gachaRecordGameUidList.isNotEmpty()) {
                    vm.expandedCurrentGameUidDropMenu = !vm.expandedCurrentGameUidDropMenu
                } else {
                    "当前祈愿记录为空".notify()
                }
            },
            slot = {
                GachaRecordCurrentGameUidSlot(
                    currentGameUid = vm.currentGameUid,
                    expanded = vm.expandedCurrentGameUidDropMenu,
                    gachaRecordGameUidList = vm.gachaRecordGameUidList,
                    onUidSelected = {
                        vm.expandedCurrentGameUidDropMenu = false
                        vm.scope.launch {
                            PreferenceKeys.GachaRecordCurrentGameUid.editValue(it)
                        }
                    },
                    onDismissRequest = { vm.expandedCurrentGameUidDropMenu = false }
                )
            }
        ),
        OptionListData(
            name = "导出的祈愿记录",
            description = "查看与管理导出的祈愿记录",
            onClick = {
                vm.goGachaRecordExportDataListScreen()
            },
            slot = {
                GachaRecordExportArrowSlot()
            }
        ),
//        OptionListData(
//            name = "删除记录",
//            description = "从本地删除某个账号的祈愿记录",
//            onClick = {
//
//            }
//        ),
//        OptionListData(
//            name = "全量增加",
//            description = "默认关闭,不再对记录进行重复性验证,将所有能够获取的数据保存到本地",
//            onClick = {
//
//            },
//            slot = {
//
//            }
//        )
    )

    /**
     * 导入设置项
     */
    fun importSettings(vm: GachaRecordOptionScreenViewModel) = listOf(
        OptionListData(
            name = "通过已登录的账号获取祈愿记录(推荐)",
            description = "使用登录在派蒙笔记本中的账号可以随时随地的获取祈愿记录",
            onClick = {
                vm.showSelectAccountGameRoleDialog = !vm.showSelectAccountGameRoleDialog
            },
            slot = {
                if (vm.showSelectAccountGameRoleDialog) {
                    GachaRecordSelectAccountSlot(
                        onButtonClick = {
                            vm.showSelectAccountGameRoleDialog = false
                        },
                        onDismissRequest = {
                            vm.showSelectAccountGameRoleDialog = false
                        },
                        onSelectRole = { user, role ->
                            vm.showSelectAccountGameRoleDialog = false
                            vm.generateAuthKeyByAccount(
                                user = user,
                                roleData = role
                            )
                        }
                    )
                }
            }
        ),
        OptionListData(
            name = "通过Url获取祈愿数据",
            description = "通过输入祈愿Url来进行祈愿数据的获取",
            onClick = {
                vm.showInputUrlDialog = !vm.showInputUrlDialog
            },
            slot = {
                if (vm.showInputUrlDialog) {
                    GachaRecordInputUrlSlot(
                        inputValue = vm.inputDialogValue,
                        onValueChange = { vm.inputDialogValue = it },
                        onConfirm = {
                            vm.showLoadingDialog = true
                            vm.getGaLogFromUrl()
                        },
                        onDismissRequest = { vm.showInputUrlDialog = false }
                    )
                }
            }
        ),
        OptionListData(
            name = "从UIGF Json导入",
            description = "从UIGF Json中导入祈愿数据",
            onClick = {
                vm.launchSelectJsonActivity()
            },
            slot = {
                if (vm.showImportUIGFJsonResultDialog) {
                    GachaRecordImportResultSlot(
                        properties = vm.importUIGFJsonPropertyList,
                        onConfirm = { vm.saveGachaLogToDB() },
                        onDismissRequest = { vm.showImportUIGFJsonResultDialog = false }
                    )
                }
            }
        )
    )

    /**
     * 导出设置项
     */
    fun exportSettings(vm: GachaRecordOptionScreenViewModel) = listOf(
        OptionListData(
            name = "UIGF Json导出",
            description = "将当前用户的祈愿记录从本地导出为UIGF Json",
            onClick = {
                vm.onClickExportUIGFJson()
            }
        ),
        OptionListData(
            name = "获取祈愿记录URL",
            description = "选择一个角色,将对应角色的祈愿记录URL复制到剪切板",
            onClick = {
                vm.showGameRoleDialog()
            }
        ),
        OptionListData(
            name = "启用UIGF V3标准导出",
            description = "默认关闭,开启后,程序导出的Json将为UIGF V3标准,以兼容未支持高版本UIGF标准的程序",
            onClick = {
                vm.scope.launchIO {
                    vm.gachaRecordExportToUIGFV3 = !vm.gachaRecordExportToUIGFV3
                    PreferenceKeys.GachaRecordExportToUIGFV3.editValue(vm.gachaRecordExportToUIGFV3)
                }
            },
            slot = {
                GachaRecordExportV3SwitchSlot(
                    checked = vm.gachaRecordExportToUIGFV3
                )
            }
        ),
    )

    /**
     * 关于设置项
     */
    fun aboutSettings(vm: GachaRecordOptionScreenViewModel) = listOf(
        OptionListData(
            name = "当前UIGF版本",
            description = "派蒙笔记本当前的UIGF版本",
            onClick = {
                "${PaimonsNotebookApplication.name}当前的UIGF版本是${UIGFHelper.UIGF_VERSION}".notify()
            },
            slot = {
                GachaRecordAboutUIGFVersionSlot(version = UIGFHelper.UIGF_VERSION)
            }
        ),
        OptionListData(
            name = "关于UIGF",
            description = "点击以查看UIGF的介绍文档以及支持UIGF的相关软件",
            onClick = {
                HomeHelper.goActivityByIntentNewTask {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(UIGFHelper.UIGF_HOME_PAGE)
                }
            }
        )
    )
}
