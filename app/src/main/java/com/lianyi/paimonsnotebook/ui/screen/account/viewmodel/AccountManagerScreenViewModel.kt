package com.lianyi.paimonsnotebook.ui.screen.account.viewmodel

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lianyi.paimonsnotebook.common.data.hoyolab.user.User
import com.lianyi.paimonsnotebook.common.database.disk_cache.entity.DiskCache
import com.lianyi.paimonsnotebook.common.database.disk_cache.util.DiskCacheDataType
import com.lianyi.paimonsnotebook.common.database.user.util.AccountHelper
import com.lianyi.paimonsnotebook.common.extension.list.takeFirstIf
import com.lianyi.paimonsnotebook.common.extension.scope.launchIO
import com.lianyi.paimonsnotebook.common.extension.scope.withContextMain
import com.lianyi.paimonsnotebook.common.extension.string.errorNotify
import com.lianyi.paimonsnotebook.common.extension.string.notify
import com.lianyi.paimonsnotebook.common.extension.string.warnNotify
import com.lianyi.paimonsnotebook.common.util.system_service.SystemService
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.auth.AuthClient
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.binding.BindingClient
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.binding.UserGameRoleData
import com.lianyi.paimonsnotebook.ui.screen.account.util.LoginHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountManagerScreenViewModel : ViewModel() {

    val userList = mutableStateListOf<User>()

    private var selectedUser: User? = null

    val loginHelper by lazy {
        LoginHelper(
            scope = viewModelScope,
            userList = userList,
            getSelectedUser = { selectedUser },
            showLoadingDialog = { showLoadingDialog() },
            dismissLoadingDialog = { dismissLoadingDialog() }
        )
    }

    init {
        viewModelScope.launchIO {
            //在主线程等待state对象创建完毕
            launchIO {
                AccountHelper.userListFlow.collect { list ->
                    withContextMain {
                        userList.clear()
                        userList.addAll(list)
                    }
                }
            }
            launchIO {
                AccountHelper.selectedUserFlow.collect {
                    withContextMain {
                        selectedUser = it
                    }
                }
            }
        }
    }

    //待操作用户
    var pendingActionUser: User? = null

    var showMenu by mutableStateOf(false)

    var showRefreshCookieConfirmDialog by mutableStateOf(false)
        private set

    //确认删除用户对话框
    var showConfirmDeleteUserDialog by mutableStateOf(false)
        private set

    var showLoadingDialog by mutableStateOf(false)
        private set

    lateinit var startActivity: ActivityResultLauncher<Intent>

    lateinit var requestStoragePermission: () -> Unit

    lateinit var checkStoragePermission: () -> Boolean


    private val bindingClient by lazy {
        BindingClient()
    }

    private val authClient by lazy {
        AuthClient()
    }

    fun showConfirmDialog(user: User) {
        pendingActionUser = user
        showRefreshCookieConfirmDialog = true
    }

    fun confirmRefreshCookieDialog() {
        val user = pendingActionUser
        if (user == null) {
            "刷新cookie失败:选中用户为空".errorNotify()
            return
        }

        refreshUserCookie(user)
        dismissConfirmDialog()
    }

    fun dismissConfirmDialog() {
        showRefreshCookieConfirmDialog = false
    }

    fun toggleMenu() {
        showMenu = !showMenu
    }

    fun dismissMenu() {
        showMenu = false
    }

    private fun showLoadingDialog() {
        showLoadingDialog = true
    }

    private fun dismissLoadingDialog() {
        showLoadingDialog = false
    }

    fun showConfirmDeleteUserDialog() {
        showConfirmDeleteUserDialog = true
    }

    fun dismissConfirmDeleteUserDialog() {
        showConfirmDeleteUserDialog = false
    }


    //切换用户选择状态
    fun switchUserSelectState(user: User) {
        if (!user.isAvailable) {
            "账号[${user.userEntity.mid}]Cookie失效,请重新登录".warnNotify(false)
            return
        }

        AccountHelper.apply {
            user.isSelected = !user.isSelected

            if (selectedUser != null && selectedUser!!.userEntity.mid != user.userEntity.mid) {
                selectedUser!!.isSelected = false
                updateUserEntitySelectedState(selectedUser!!, false)
            }

            updateUserEntitySelectedState(user, user.isSelected)
        }
    }

    fun deleteUser() {
        val user = pendingActionUser

        if (user == null) {
            "待删除用户为空".errorNotify()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            AccountHelper.deleteUser(user.userEntity)

            pendingActionUser = null
            dismissConfirmDeleteUserDialog()

            "账号[${user.userInfo.nickname}]已删除".notify()
        }
    }

    fun onDeleteUser(user: User) {
        pendingActionUser = user
        showConfirmDeleteUserDialog()
    }

    fun copyCookie(user: User) {
        SystemService.setClipBoardText(user.userEntity.cookies)
        "已将账号[${user.userInfo.nickname}]的cookie复制到剪切板".notify()
    }

    private fun refreshUserCookie(user: User) {
        "正在刷新[${user.userInfo.nickname}]的Cookie".notify()
        showLoadingDialog()

        viewModelScope.launch(Dispatchers.IO) {
            val result = AccountHelper.refreshCookieToken(user.userEntity)

            if (result.success) {
                "刷新成功".notify()
            } else {
                "刷新失败:${result.retcode}".errorNotify()
            }
            dismissLoadingDialog()
            pendingActionUser = null
        }
    }

    fun changeUserChosePlayer(user: User, role: UserGameRoleData.Role) {
        val chooseRole = user.userGameRoles.takeFirstIf { it.is_chosen }

        //已经选中的用户不能再设置为选中
        if (chooseRole != null && chooseRole.game_uid == role.game_uid) {
            return
        }

        "正在修改账号的默认游戏角色".notify()
        showLoadingDialog()

        viewModelScope.launch {
            val authClient = authClient.getActionTicketBySToken(user.userEntity)
            val result = bindingClient.changeGameRoleByDefault(authClient.data.ticket, role)

            if (result.success) {
                AccountHelper.reloadUserGameRoles(user)
                "已将默认游戏角色更改为[${role.nickname}]".notify()
            } else {
                "修改默认游戏角色失败:${result.retcode},${result.message}".warnNotify()
            }
            dismissLoadingDialog()
        }
    }

    fun getUserAvatarDiskCacheData(user: User): DiskCache =
        DiskCache(
            url = user.userInfo.avatar_url,
            name = "账户头像",
            createFrom = "账号管理",
            type = DiskCacheDataType.Stable,
            lastUseFrom = "账号管理"
        )

    fun onBackPressed(onFinished: () -> Unit) {
        if (showMenu || loginHelper.showAddAccountByCookieDialog) {
            showMenu = false
            loginHelper.hideCookieInputDialog()
        } else {
            onFinished()
        }
    }

    override fun onCleared() {
        super.onCleared()
        loginHelper.onCleared()
    }
}
