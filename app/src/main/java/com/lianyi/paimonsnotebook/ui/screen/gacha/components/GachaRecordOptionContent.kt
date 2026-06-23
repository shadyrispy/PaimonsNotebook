package com.lianyi.paimonsnotebook.ui.screen.gacha.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lianyi.core.ui.components.text.InfoText
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.common.components.dialog.LazyColumnDialog
import com.lianyi.paimonsnotebook.common.components.dialog.PropertiesDialog
import com.lianyi.paimonsnotebook.common.components.widget.InputTextFiled
import com.lianyi.paimonsnotebook.common.data.hoyolab.user.User
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.binding.UserGameRoleData
import com.lianyi.paimonsnotebook.ui.screen.account.components.dialog.UserGameRolesDialog
import com.lianyi.paimonsnotebook.ui.screen.setting.components.widgets.SettingsOptionSwitch
import com.lianyi.paimonsnotebook.ui.theme.Black_60
import com.lianyi.paimonsnotebook.ui.theme.Gray_F5
import com.lianyi.paimonsnotebook.ui.theme.Primary_2

/*
* 当前账号 UID 下拉选择框 slot
* */
@Composable
fun GachaRecordCurrentGameUidSlot(
    currentGameUid: String,
    expanded: Boolean,
    gachaRecordGameUidList: List<String>,
    onUidSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Box(contentAlignment = Alignment.TopEnd) {
        Text(text = currentGameUid, fontSize = 14.sp, color = Primary_2)

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            gachaRecordGameUidList.forEach {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clickable { onUidSelected(it) }
                        .padding(12.dp)
                )
            }
        }
    }
}

/*
* 导出记录箭头图标 slot
* */
@Composable
fun GachaRecordExportArrowSlot() {
    Icon(
        painter = painterResource(id = R.drawable.ic_chevron_right),
        contentDescription = null,
        modifier = Modifier.size(16.dp)
    )
}

/*
* 账号选择对话框 slot
* */
@Composable
fun GachaRecordSelectAccountSlot(
    onButtonClick: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    onSelectRole: (User, UserGameRoleData.Role) -> Unit
) {
    UserGameRolesDialog(
        onButtonClick = onButtonClick,
        onDismissRequest = onDismissRequest,
        onSelectRole = onSelectRole
    )
}

/*
* URL 输入对话框 slot
* */
@Composable
fun GachaRecordInputUrlSlot(
    inputValue: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    LazyColumnDialog(
        title = "请输入祈愿记录Url",
        titleSpacer = 20.dp,
        onClickButton = {
            if (it == 1) {
                onConfirm()
            }
            onDismissRequest()
        },
        titleTextSize = 16.sp,
        buttons = arrayOf("取消", "确定"),
        onDismissRequest = onDismissRequest
    ) {
        item {
            InputTextFiled(
                value = inputValue,
                onValueChange = onValueChange,
                inputFieldHeight = 200.dp,
                backgroundColor = Gray_F5,
                padding = PaddingValues(8.dp),
                placeholder = {
                    Text(
                        text = "请输入祈愿记录Url,并确保各个参数的有效性",
                        fontSize = 14.sp,
                        color = Black_60
                    )
                }
            )
        }
    }
}

/*
* UIGF Json 导入结果对话框 slot
* */
@Composable
fun GachaRecordImportResultSlot(
    properties: List<Pair<String, String>>,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    PropertiesDialog(
        title = "UIGF Json信息",
        properties = properties,
        onDismissRequest = onDismissRequest,
        buttons = arrayOf("取消", "确认导入"),
        onButtonClick = {
            if (it == 1) onConfirm()
            onDismissRequest()
        }
    )
}

/*
* UIGF V3 导出开关 slot
* */
@Composable
fun GachaRecordExportV3SwitchSlot(
    checked: Boolean
) {
    SettingsOptionSwitch(checked = checked)
}

/*
* 关于 UIGF 版本信息 slot
* */
@Composable
fun GachaRecordAboutUIGFVersionSlot(
    version: String
) {
    InfoText(text = version)
}
