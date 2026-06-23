package com.lianyi.paimonsnotebook.ui.screen.cultivate_project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lianyi.paimonsnotebook.common.data.popup.IconTitleInformationPopupWindowData
import com.lianyi.paimonsnotebook.common.data.popup.PopupWindowPositionProvider
import com.lianyi.paimonsnotebook.common.data.repository.CultivateRepository
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateEntity
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateItemMaterials
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateItems
import com.lianyi.paimonsnotebook.common.extension.scope.launchIO
import com.lianyi.paimonsnotebook.common.extension.scope.withContextMain
import com.lianyi.paimonsnotebook.common.extension.string.warnNotify
import com.lianyi.paimonsnotebook.common.util.data_store.PreferenceKeys
import com.lianyi.paimonsnotebook.common.util.data_store.dataStoreValues
import com.lianyi.paimonsnotebook.common.util.enums.LoadingState
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.avatar.AvatarData
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.common.service.AvatarService
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.common.service.MaterialService
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.common.service.WeaponService
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.item.Material
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.weapon.WeaponData
import com.lianyi.paimonsnotebook.ui.screen.cultivate_project.data.CultivateItemInfoData
import com.lianyi.paimonsnotebook.ui.screen.cultivate_project.data.EntityBaseInfo
import com.lianyi.paimonsnotebook.ui.screen.cultivate_project.data.MaterialBaseInfo
import com.lianyi.paimonsnotebook.ui.screen.cultivate_project.util.CultivateProjectCalculator
import com.lianyi.paimonsnotebook.ui.screen.cultivate_project.view.CultivateProjectOptionScreen
import com.lianyi.paimonsnotebook.ui.screen.home.util.HomeHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CultivateProjectScreenViewModel : ViewModel() {

    var loadingState by mutableStateOf(LoadingState.Loading)

    private val avatarList = mutableListOf<AvatarData>()
    private val weaponList = mutableListOf<WeaponData>()

    private val avatarService by lazy {
        AvatarService(this::onMissingFile)
    }

    private val weaponService by lazy {
        WeaponService(this::onMissingFile)
    }

    private val materialService by lazy {
        MaterialService(this::onMissingFile)
    }

    val entityCultivateItemsPairList =
        mutableStateListOf<Pair<CultivateEntity, List<CultivateItems>>>()

    /*
    * 存储所有计算项(cultivate_item)的所需材料
    * key = id(角色消耗为角色/武器id,技能消耗为技能id,消耗总览为负的角色/武器id)
    * */
    private val itemsMaterialsMap = mutableMapOf<Int, List<CultivateItemMaterials>>()

    private val avatarIdMap = mutableMapOf<Int, AvatarData>()
    private val weaponIdMap = mutableMapOf<Int, WeaponData>()

    private val cultivateProjectDao = CultivateRepository.cultivateProjectDao
    private val cultivateEntityDao = CultivateRepository.cultivateEntityDao
    private val cultivateItemsDao = CultivateRepository.cultivateItemsDao
    private val cultivateItemMaterialsDao =
        CultivateRepository.cultivateItemMaterialsDao

    /*
    * 材料总览分组
    * 将相同组别的材料(可合成高一级的)分为同一组
    * */
    val overallMaterialBaseInfoGroupList = mutableStateListOf<List<MaterialBaseInfo>>()

    val overallMaterialBaseInfoGroupListFlatten = mutableStateListOf<MaterialBaseInfo>()

    /*
    * 材料总览实体分组
    * 根据材料总览分组分每个实体对应的组,实体A用材料B,就分到材料B的组中
    * key = 同组材料中第一个材料的material_id, value =使用此材料的实体
    * */
    private val overallEntityBaseInfoMap = mutableMapOf<Int, MutableList<EntityBaseInfo>>()

    var showOverallPageGridList by mutableStateOf(true)
        private set

    //养成项是否根据实体类型排序
    private var cultivateProjectSortByEntityType = false

    //设置setData job引用
    private var setDataJob: Job? = null

    private val mutex = Mutex()

    private var cultivateEntityMapList = mutableMapOf<CultivateEntity, List<CultivateItems>>()

    fun load() {
        loadingState = LoadingState.Loading

        viewModelScope.launchIO {
            avatarList += avatarService.avatarList
            weaponList += weaponService.weaponList

            avatarIdMap += avatarService.avatarList.associateBy { it.id }
            weaponIdMap += weaponService.weaponList.associateBy { it.id }

        }

        viewModelScope.launchIO {
            dataStoreValues {
                cultivateProjectSortByEntityType =
                    it[PreferenceKeys.CultivateProjectSortByEntityType] ?: false

                /*
                * 当setDataJob为空,表示是第一次进入界面,此次不更新数据集合
                * 当不为空时表示已经完成初始化阶段
                * */
                if (setDataJob == null || setDataJob?.isCompleted == true) {
                    return@dataStoreValues
                }

                mutex.withLock {
                    setDataJob?.cancelAndJoin()
                    setDataJob = launchIO {
                        updateMaterialOverallGroup()
                    }
                }
            }
        }

        viewModelScope.launchIO {
            //更新当前选择的养成计划时更新
            cultivateProjectDao.getSelectedProjectFlow().collectLatest { cultivateProject ->
                currentSelectedProjectId = cultivateProject?.projectId ?: -1

                if (cultivateProject == null) {
                    withContextMain {
                        loadingState = LoadingState.Empty
                    }
                    return@collectLatest
                } else {
                    withContextMain {
                        loadingState = LoadingState.Loading
                    }
                }

                withMutexLockUpdateData()
            }
        }
    }

    val tabs = arrayOf("养成计划", "材料总览")

    var currentPageIndex by mutableIntStateOf(0)
        private set

    //当前选中的养成实体id
    private var currentSelectedEntityId: Int = -1

    //当前选中的养成计划id
    private var currentSelectedProjectId: Int = -1

    //显示删除养成实体对话框
    var showDeleteEntityConfirmDialog by mutableStateOf(false)
        private set

    //删除对话框的显示内容
    var deleteEntityConfirmDialogContent = ""
        private set

    var showPopupWindow by mutableStateOf(false)
        private set

    lateinit var showPopupWindowInfo: IconTitleInformationPopupWindowData
        private set

    var popupWindowProvider by mutableStateOf(PopupWindowPositionProvider())
        private set

    private suspend fun withMutexLockUpdateData() {
        mutex.withLock {
            setDataJob?.cancelAndJoin()
            setDataJob = viewModelScope.launchIO {
                setListData()
            }
        }
    }

    private suspend fun setListData() {
        //更新实体时会回调此部分
        cultivateEntityDao.getCultivateEntityMapListFlowByProjectId(
            currentSelectedProjectId
        ).collectLatest { cultivateMap ->
            cultivateEntityMapList.clear()
            cultivateEntityMapList += cultivateMap

            updateCultivateMaterialsMap()
        }
    }

    private suspend fun updateCultivateMaterialsMap() {
        //更新材料时会回调此部分
        cultivateItemsDao.getCultivateIdsMaterialsMapFlowByProjectId(
            currentSelectedProjectId
        ).collect {

            withContextMain {
                itemsMaterialsMap.clear()

                itemsMaterialsMap += it
            }

            /*
            * 判断两个延迟初始化的map是否初始化完成,没有完成就每100毫秒循环判断一次
            * 此功能只会在第一次进入此界面时用到
            * */
            while (!(avatarIdMap.isNotEmpty() && weaponIdMap.isNotEmpty()) && loadingState != LoadingState.Error) {
                delay(100)
            }

            updateMaterialOverallGroup()
        }
    }

    private fun onMissingFile() {
        loadingState = LoadingState.Error
    }

    fun onSelectTabBar(index: Int) {
        currentPageIndex = index
    }

    fun goOptionScreen() {
        HomeHelper.goActivity(CultivateProjectOptionScreen::class.java)
    }

    fun switchShowOverallPageGridList() {
        showOverallPageGridList = !showOverallPageGridList
    }

    fun onShowMaterialInfoPopupDialog(
        material: Material,
        provider: PopupWindowPositionProvider
    ) {
        showPopupWindowInfo = material.getShowPopupWindowInfo()
        popupWindowProvider = provider
        showPopupWindow = true
    }

    fun onShowEntityInfoPopupDialog(
        id: Int,
        provider: PopupWindowPositionProvider
    ) {
        val weaponData = getWeaponData(id)
        val avatarData = getAvatarData(id)

        if (weaponData == null && avatarData == null) return

        if (avatarData != null) {
            showPopupWindowInfo = IconTitleInformationPopupWindowData(
                title = avatarData.name,
                subTitle = avatarData.fetterInfo.Title,
                iconUrl = avatarData.iconUrl,
                content = avatarData.description
            )
        }

        if (weaponData != null) {
            showPopupWindowInfo = IconTitleInformationPopupWindowData(
                title = weaponData.name,
                subTitle = weaponData.weaponTypeName,
                iconUrl = weaponData.iconUrl,
                content = weaponData.description
            )
        }

        popupWindowProvider = provider
        showPopupWindow = true
    }

    fun onPopupWindowDismissRequest() {
        showPopupWindow = false
    }

    fun entityConfirmDialogDismissRequest() {
        showDeleteEntityConfirmDialog = false
        deleteEntityConfirmDialogContent = ""
        currentSelectedEntityId = -1
    }

    /*
    * 获取角色计算项map
    * */
    fun getAvatarCultivateIdMap(avatarData: AvatarData) = mapOf(
        avatarData.id.let {
            it to CultivateItemInfoData(
                id = it,
                name = "等级提升",
                icon = ""
            )
        },
        avatarData.skillDepot.Skills.first().let {
            it.GroupId to CultivateItemInfoData(
                id = it.GroupId,
                name = "普通攻击",
                icon = it.iconUrl
            )
        },
        avatarData.skillDepot.Skills.last().let {
            it.GroupId to CultivateItemInfoData(
                id = it.GroupId,
                name = "元素战技",
                icon = it.iconUrl
            )
        },
        avatarData.skillDepot.EnergySkill.let {
            it.GroupId to CultivateItemInfoData(
                id = it.GroupId,
                name = "元素爆发",
                icon = it.iconUrl
            )
        },
    )

    fun getAvatarData(id: Int) = avatarIdMap[id]

    fun getWeaponData(id: Int) = weaponIdMap[id]

    fun getMaterialData(id: Int) = materialService.getMaterialById(id)

    //获取计算的材料类型,并根据状态排序
    fun getMaterialListByCultivateItemId(itemId: Int) =
        (itemsMaterialsMap[itemId] ?: listOf()).sortedWith(
            compareBy(
                { it.tempStatus },
                { it.itemId })
        )

    fun onClickCultivateCardDelete(cultivateEntity: CultivateEntity, name: String) {
        currentSelectedEntityId = cultivateEntity.itemId

        deleteEntityConfirmDialogContent = "确定要从养成计划中删除[${name}]吗?"
        showDeleteEntityConfirmDialog = true
    }

    fun deleteCurrentSelectedEntity() {
        if (currentSelectedEntityId == -1) return
        if (currentSelectedProjectId == -1) return

        viewModelScope.launchIO {

            cultivateEntityDao.deleteEntityByItemIdAndProjectId(
                itemId = currentSelectedEntityId,
                projectId = currentSelectedProjectId
            )

            "已删除所选项".warnNotify(false)

            entityConfirmDialogDismissRequest()
        }
    }

    private suspend fun updateMaterialOverallGroup() {
        //如果当前没有选中养成计划就直接返回
        if (currentSelectedProjectId == -1) return

        val result = CultivateProjectCalculator.calculateMaterialOverallGroup(
            cultivateEntityMapList = cultivateEntityMapList,
            itemsMaterialsMap = itemsMaterialsMap,
            sortByEntityType = cultivateProjectSortByEntityType,
            getMaterialData = ::getMaterialData,
            getAvatarData = ::getAvatarData,
            getWeaponData = ::getWeaponData
        ) ?: return

        /*
        * 更新数据集与加载状态
        * */
        withContextMain {
            overallMaterialBaseInfoGroupList.clear()
            overallMaterialBaseInfoGroupListFlatten.clear()
            overallEntityBaseInfoMap.clear()
            entityCultivateItemsPairList.clear()

            overallMaterialBaseInfoGroupList += result.materialBaseInfoGroups
            overallMaterialBaseInfoGroupListFlatten += result.materialBaseInfoGroupsFlatten
            overallEntityBaseInfoMap += result.entityBaseInfoMap
            entityCultivateItemsPairList += result.sortedEntityItemsPairList

            loadingState = LoadingState.Success
        }
    }

    /*
    * 当提交材料item更新队列时
    * 批量更新点击的材料列表,频繁更新数据库频繁造成UI更新消耗大量性能,因此一段时间内的状态更新会汇总后批量更新
    * */
    fun onEmitMaterialItemUpdateQueue(
        cultivateItems: CultivateItems,
        cultivateItemMaterials: List<CultivateItemMaterials>
    ) {
        viewModelScope.launchIO {
            //状态取反
            cultivateItemMaterials.groupBy {
                it.tempStatus
            }.forEach { (status, list) ->
                cultivateItemMaterialsDao.updateStatusByMaterialIds(
                    status = status,
                    projectId = cultivateItems.projectId,
                    cultivateItemId = cultivateItems.itemId,
                    materialIds = list.map { it.itemId }
                )
            }
        }
    }

    /*
    * 通过材料id获取使用此材料的实体列表
    * materialId = 材料分组中第一个材料
    * */
    fun getOverallEntityBaseInfoListByMaterialId(materialId: Int) =
        overallEntityBaseInfoMap[materialId] ?: listOf()
}