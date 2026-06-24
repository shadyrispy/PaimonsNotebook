package com.lianyi.paimonsnotebook.ui.screen.items.viewmodel.base

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lianyi.paimonsnotebook.common.data.repository.CultivateRepository
import com.lianyi.paimonsnotebook.common.database.cultivate.data.CultivateItemType
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateProject
import com.lianyi.paimonsnotebook.common.database.user.util.AccountHelper
import com.lianyi.paimonsnotebook.common.extension.intent.setComponentName
import com.lianyi.paimonsnotebook.common.extension.scope.launchIO
import com.lianyi.paimonsnotebook.common.extension.string.errorNotify
import com.lianyi.paimonsnotebook.common.extension.string.notify
import com.lianyi.paimonsnotebook.common.extension.string.warnNotify
import com.lianyi.paimonsnotebook.common.navigation.Routes
import com.lianyi.paimonsnotebook.common.util.enums.LoadingState
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.binding.UserGameRoleData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.event.calculate.BatchCalculatePromotionDetail
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.event.calculate.CalculateClient
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.item.Material
import com.lianyi.paimonsnotebook.ui.screen.cultivate_project.view.CultivateProjectOptionScreen
import com.lianyi.paimonsnotebook.ui.screen.home.util.HomeHelper
import com.lianyi.paimonsnotebook.ui.screen.items.data.cultivate.CultivateConfigData
import com.lianyi.paimonsnotebook.ui.screen.items.util.ItemComputeHelper
import com.lianyi.paimonsnotebook.ui.screen.items.util.ItemFilterType

/*
* item基本viewModel
*
* observeCurrentItemState:是否观测当前item的状态
* */
open class ItemBaseViewModel<T>(private val observeCurrentItemState: Boolean = true) : ViewModel() {

    var currentItem: T? by mutableStateOf(null)

    var itemAddedToCurrentCultivateProject by mutableStateOf(false)
    var compareItem: T? by mutableStateOf(null)
    var loadingState: LoadingState by mutableStateOf(LoadingState.Loading)
    var showLoadingDialog by mutableStateOf(false)

    val materialList = mutableStateListOf<Material>()

    var currentItemLevel by mutableIntStateOf(1)
    var selectCompareItem = false

    var showNoCultivateProjectNoticeDialog by mutableStateOf(false)
    var showItemConfigDialog by mutableStateOf(false)

    open val tabs: Array<String> = arrayOf()
    val cultivateConfigList = mutableStateListOf<CultivateConfigData>()

    val itemConfigDialogButtons: Array<String> = arrayOf("取消", "确定")

    private var calculateItemId: Int = 0

    /*
    * 当前养成计划缓存
    * 在点击添加按钮时设置
    * */
    private var currentSelectedCultivateProjectCache: CultivateProject? = null

    private var currentGameRoleCache: UserGameRoleData.Role? = null

    init {
        if (observeCurrentItemState) {
            viewModelScope.launchIO {
                snapshotFlow { currentItem }.collect {
                    if (it == null) {
                        itemAddedToCurrentCultivateProject = false
                        return@collect
                    }

                    itemAddedToCurrentCultivateProject =
                        getEntityHasAddedSelectedProject(getCurrentItemId())
                }
            }
        }
    }

    private val projectDao by lazy {
        CultivateRepository.cultivateProjectDao
    }

    private val cultivateEntityDao by lazy {
        CultivateRepository.cultivateEntityDao
    }

    private val cultivateItemsDao by lazy {
        CultivateRepository.cultivateItemsDao
    }

    private val cultivateItemMaterialsDao by lazy {
        CultivateRepository.cultivateItemMaterialsDao
    }

    private val calculateClient by lazy {
        CalculateClient()
    }

    open fun init(intent: Intent) {
    }

    open fun updateMaterial() {
    }

    open fun onClickCompareItem() {
    }

    open fun onChangeItemLevel(value: Int, promoted: Boolean) {
        this.currentItemLevel = value
    }

    open fun onPromotedChange(promoted: Boolean) {
    }

    open fun showListView() {
    }

    open fun showDetailView() {
    }

    fun dismissNoCultivateProjectNoticeDialog() {
        showNoCultivateProjectNoticeDialog = false
    }

    fun addCurrentItemToCultivateProject() {
        val user = AccountHelper.selectedUserFlow.value
        val itemId = getCurrentItemId()

        if (user == null) {
            "必须设置一个默认用户才能使用此功能".warnNotify(false)
            return
        }

        if (user.userGameRoles.isEmpty()) {
            "当前用户没有找到游戏角色,请更换账号或稍后再试".warnNotify()
            return
        }

        currentGameRoleCache = user.getSelectedGameRole()

        if (currentGameRoleCache == null && user.userGameRoles.isNotEmpty()) {
            val role = user.userGameRoles.first()
            currentGameRoleCache = role

            "由于当前用户[${user.userInfo.nickname}]没有设置默认用户,已自动选择角色列表中的第一个角色[${role.nickname}](uid:${role.game_uid})作为本次请求的角色".warnNotify()
        }

        calculateItemId = itemId
        viewModelScope.launchIO {
            currentSelectedCultivateProjectCache = projectDao.getSelectedProject()

            if (currentSelectedCultivateProjectCache == null) {
                showNoCultivateProjectNoticeDialog = true
                return@launchIO
            }

            onShowItemConfigDialog()
        }
    }

    fun updateCurrentItemSelectedState(itemId: Int) {
        //判断当前item是否存在于当前养成计划中
        viewModelScope.launchIO {
            cultivateEntityDao.entityHasAddedSelectedProject(itemId = itemId)
        }
    }

    //这个方法需要子类重写
    open fun getCurrentItemId(): Int = -1

    fun onClickItemConfigDialogButton(index: Int) {
        if (index == 0) {
            showItemConfigDialogRequestDismiss()
            return
        }

        val role = this.currentGameRoleCache

        if (role == null) {
            "没有找到缓存的用户角色数据,请稍后再试".warnNotify()
            showItemConfigDialogRequestDismiss()
            return
        }

        val cultivateConfigDataMap = cultivateConfigList.groupBy {
            it.type
        }

        var avatar: CultivateConfigData? = null
        var weapon: BatchCalculatePromotionDetail.Weapon? = null
        var cultivateSkillList: List<BatchCalculatePromotionDetail.Skill>? = null

        val avatarList = cultivateConfigDataMap[CultivateItemType.Avatar]
        val weaponList = cultivateConfigDataMap[CultivateItemType.Weapon]
        val skillList = cultivateConfigDataMap[CultivateItemType.Skill]

        val items = mutableListOf<BatchCalculatePromotionDetail.Item>()

        if (!skillList.isNullOrEmpty()) {
            cultivateSkillList = skillList.map {
                BatchCalculatePromotionDetail.Skill(
                    id = it.id,
                    level_current = it.fromLevel,
                    level_target = it.toLevel
                )
            }
        }

        if (!avatarList.isNullOrEmpty() && cultivateSkillList != null) {
            avatar = avatarList.first()

            items += BatchCalculatePromotionDetail.Item(
                avatar_id = avatar.id,
                avatar_level_current = avatar.fromLevel,
                avatar_level_target = avatar.toLevel,
                element_attr_id = avatar.itemTypeId,
                skill_list = cultivateSkillList
            )
        }

        if (!weaponList.isNullOrEmpty()) {
            weapon = weaponList.first().let {
                BatchCalculatePromotionDetail.Weapon(
                    id = it.id,
                    level_current = it.fromLevel,
                    level_target = it.toLevel
                )
            }

            items += BatchCalculatePromotionDetail.Item(
                weapon = weapon
            )
        }

        val promotionDetail = BatchCalculatePromotionDetail(
            items = items,
            region = role.region,
            uid = role.game_uid
        )

        compute(promotionDetail)
        showItemConfigDialogRequestDismiss()
    }

    protected fun onMissingFile() {
        loadingState = LoadingState.Error
    }

    fun showItemConfigDialogRequestDismiss() {
        cultivateConfigList.clear()
        showItemConfigDialog = false
    }

    open fun onShowItemConfigDialog() {
        showItemConfigDialog = true
    }

    open fun getItemDataContent(item: T, type: ItemFilterType, isList: Boolean): String = ""

    open fun onClickItem(item: T) {
        this.currentItem = item
    }

    private fun compute(promotionDetail: BatchCalculatePromotionDetail) {
        val user = AccountHelper.selectedUserFlow.value

        if (user == null) {
            "必须设置一个默认用户才能使用此功能".warnNotify(false)
            return
        }

        viewModelScope.launchIO {
            val res = calculateClient.getCalculateBatchCompute(user, promotionDetail)

            if (!res.success) {
                "添加至养成计划失败:${res.message}".warnNotify(false)
                return@launchIO
            }

            try {
                val projectId = currentSelectedCultivateProjectCache?.projectId
                    ?: return@launchIO

                ItemComputeHelper.saveAvatarComputeResult(
                    result = res.data,
                    promotionDetail = promotionDetail,
                    projectId = projectId,
                    itemAlreadyAdded = itemAddedToCurrentCultivateProject,
                    cultivateEntityDao = cultivateEntityDao,
                    cultivateItemsDao = cultivateItemsDao,
                    cultivateItemMaterialsDao = cultivateItemMaterialsDao
                )?.let { avatarId ->
                    onDataAddSuccess("角色", avatarId)
                }

                ItemComputeHelper.saveWeaponComputeResult(
                    result = res.data,
                    promotionDetail = promotionDetail,
                    projectId = projectId,
                    itemAlreadyAdded = itemAddedToCurrentCultivateProject,
                    cultivateEntityDao = cultivateEntityDao,
                    cultivateItemsDao = cultivateItemsDao,
                    cultivateItemMaterialsDao = cultivateItemMaterialsDao
                )?.let { weaponId ->
                    onDataAddSuccess("武器", weaponId)
                }
            } catch (e: Exception) {
                e.printStackTrace()

                "添加数据至数据库时出现错误:${e.message}".errorNotify()
            }
        }
    }

    private suspend fun onDataAddSuccess(tag: String, id: Int) {
        "${tag}成功${if (itemAddedToCurrentCultivateProject) "更新" else "添加"}至养成计划[${currentSelectedCultivateProjectCache?.projectName}]中".notify()

        itemAddedToCurrentCultivateProject = getEntityHasAddedSelectedProject(id)
    }

    private suspend fun getEntityHasAddedSelectedProject(itemId: Int): Boolean {
        return cultivateEntityDao.entityHasAddedSelectedProject(itemId)
    }

    fun goCultivateProjectOptionScreen() {
        HomeHelper.goActivityByIntentNewTask {
            setComponentName(CultivateProjectOptionScreen::class.java)
            putExtra(Routes.EXTRA_ADD_FLAG, true)
        }
        dismissNoCultivateProjectNoticeDialog()
    }

}