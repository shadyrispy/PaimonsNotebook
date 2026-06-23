package com.lianyi.paimonsnotebook.ui.screen.items.viewmodel.screen

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lianyi.paimonsnotebook.common.data.popup.IconTitleInformationPopupWindowData
import com.lianyi.paimonsnotebook.common.data.popup.PopupWindowPositionProvider
import com.lianyi.paimonsnotebook.common.database.PaimonsNotebookDatabase
import com.lianyi.paimonsnotebook.common.extension.list.takeFirstIf
import com.lianyi.paimonsnotebook.common.extension.scope.launchIO
import com.lianyi.paimonsnotebook.common.util.enums.LoadingState
import com.lianyi.paimonsnotebook.common.util.time.TimeHelper
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.avatar.AvatarData
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.common.service.AvatarService
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.common.service.MaterialService
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.common.service.WeaponService
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.item.Material
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.item.Materials
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.weapon.WeaponData
import com.lianyi.paimonsnotebook.ui.screen.home.util.HomeHelper
import com.lianyi.paimonsnotebook.ui.screen.items.util.ItemHelper
import com.lianyi.paimonsnotebook.ui.screen.items.view.AvatarScreen
import com.lianyi.paimonsnotebook.ui.screen.items.view.WeaponScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class CultivationMaterialScreenViewModel : ViewModel() {

    var loadingState by mutableStateOf(LoadingState.Loading)
        private set

    private val materialService by lazy {
        MaterialService {
            onMissingFile()
        }
    }

    private val avatarService by lazy {
        AvatarService {
            onMissingFile()
        }
    }

    init {
        setWeekData(LocalDateTime.now().dayOfWeek.value)
    }

    /*
    * 在数据加载成功后启动养成计划监听
    * 此时数据库已初始化完成
    * */
    private fun startCultivateProjectHighlightListener() {
        if (cultivateListenerStarted) return
        cultivateListenerStarted = true
        viewModelScope.launchIO {
            try {
                cultivateProjectDao.getSelectedProjectFlow().collectLatest { project ->
                    if (project == null) {
                        hasActiveProject = false
                        highlightMaterialIds = emptySet()
                        highlightEntityIds = emptySet()
                    } else {
                        hasActiveProject = true
                        updateHighlightData(project.projectId)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    private val weaponService by lazy {
        WeaponService {
            onMissingFile()
        }
    }

    private fun onMissingFile() {
        loadingState = LoadingState.Error
    }

    var avatarList by mutableStateOf<List<Pair<List<Material>, List<AvatarData>>>>(listOf())
        private set
    var weaponList by mutableStateOf<List<Pair<List<Material>, List<WeaponData>>>>(listOf())
        private set

    var weekName by mutableStateOf("")
        private set

    //养成计划高亮：所需材料ID集合
    var highlightMaterialIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    //养成计划高亮：养成实体(角色/武器)ID集合
    var highlightEntityIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    //是否有活跃的养成计划
    var hasActiveProject by mutableStateOf(false)
        private set

    //确保养成计划监听只启动一次
    private var cultivateListenerStarted = false

    private val cultivateProjectDao by lazy { PaimonsNotebookDatabase.database.cultivateProjectDao }
    private val cultivateEntityDao by lazy { PaimonsNotebookDatabase.database.cultivateEntityDao }
    private val cultivateItemsDao by lazy { PaimonsNotebookDatabase.database.cultivateItemsDao }


    val dropMenuList by lazy {
        (1..7).map {
            TimeHelper.getWeekName(it) to it
        }
    }

    var currentPageIndex by mutableIntStateOf(0)
        private set

    val tabs by lazy {
        arrayOf(
            "天赋培养", "武器突破"
        )
    }

    var showMaterialPopupWindow by mutableStateOf(false)
        private set

    var popupWindowProvider by mutableStateOf(PopupWindowPositionProvider())
        private set

    lateinit var popupWindowData: IconTitleInformationPopupWindowData

    fun onChangePage(value: Int) {
        currentPageIndex = value
    }

    private fun setWeekData(week: Int, ignoreHour: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {

            Materials.getMaterialsIdByWeek(week, ignoreHour).also { result ->
                val materialMap = materialService.getMaterialListByIds(result.first)
                    .associateBy { it.Id }

                weaponList = formatDataList(
                    materialMap = materialMap,
                    list = weaponService.weaponList,
                    groupByCondition = {
                        val id = it.cultivationItems
                            .takeFirstIf { id -> materialMap[id] != null }?.let { value ->
                                value + if (it.rankLevel < 3) 1 else 0
                            }
                        materialMap[id]
                    }, listSortCondition = {
                        it.rankLevel
                    }, typeMaterialCount = 4
                )

                avatarList = formatDataList(
                    materialMap = materialMap,
                    list = avatarService.avatarList,
                    groupByCondition = {
                        materialMap[it.cultivationItems.takeFirstIf { id -> materialMap[id] != null }]
                    }, listSortCondition = {
                        it.starCount
                    }, typeMaterialCount = 3
                )

                if (loadingState == LoadingState.Loading) {
                    viewModelScope.launch(Dispatchers.Main) {
                        weekName = TimeHelper.getWeekName(result.second)
                        loadingState = LoadingState.Success
                        startCultivateProjectHighlightListener()
                    }
                }
            }
        }
    }

    /*
    * 格式化数据列表
    *
    * materialMap:材料map
    * list:数据列表
    * groupByCondition:分组条件
    * listSortCondition:列表排序条件
    * typeMaterialCount:类型材料个数
    * */
    private fun <T> formatDataList(
        materialMap: Map<Int, Material>,
        list: List<T>,
        groupByCondition: (T) -> Material?,
        listSortCondition: (T) -> Int,
        typeMaterialCount: Int
    ) =
        list.asSequence().filter {
            groupByCondition.invoke(it) != null
        }.groupBy(groupByCondition).toList()
            .sortedByDescending { it.first?.RankLevel ?: 0 }
            .map { pair ->
                val material = pair.first
                val typeList = mutableListOf<Material>()

                if (material != null) {
                    repeat(typeMaterialCount) {
                        val item = materialMap[material.Id - it]
                        if (item != null) {
                            typeList += item
                        }
                    }
                }
                typeList to pair.second.sortedByDescending(listSortCondition)
            }

    var showDropMenu by mutableStateOf(false)

    fun onSelectDropMenuItem(pair: Pair<String, Int>) {
        loadingState = LoadingState.Loading
        dismissDropMenu()
        setWeekData(pair.second, ignoreHour = true)
    }

    fun showDropMenu() {
        showDropMenu = true
    }

    fun dismissDropMenu() {
        showDropMenu = false
    }

    fun dismissPopupWindow() {
        showMaterialPopupWindow = false
    }

    fun onClickAvatar(avatarData: AvatarData) {
        HomeHelper.goActivity(AvatarScreen::class.java, Bundle().apply {
            putInt(ItemHelper.PARAM_INT_ITEM_ID, avatarData.id)
        })
    }

    fun onClickWeapon(weaponData: WeaponData) {
        HomeHelper.goActivity(WeaponScreen::class.java, Bundle().apply {
            putInt(ItemHelper.PARAM_INT_ITEM_ID, weaponData.id)
        })
    }

    fun onClickMaterialItem(material: Material, intSize: IntSize, offset: Offset) {
        popupWindowProvider = PopupWindowPositionProvider(
            contentOffset = offset,
            itemSize = intSize,
            itemSpace = 8.dp
        )

        popupWindowData = material.getShowPopupWindowInfo()

        showMaterialPopupWindow = true
    }

    /*
    * 更新养成计划高亮数据
    * 从养成计划中获取所有实体ID和所需材料ID
    * */
    private fun updateHighlightData(projectId: Int) {
        //监听养成实体变化
        viewModelScope.launchIO {
            try {
                cultivateEntityDao.getCultivateEntityMapListFlowByProjectId(projectId)
                    .collectLatest { entityMap ->
                        val entityIds = entityMap.keys.map { it.itemId }.toSet()
                        withContext(Dispatchers.Main) {
                            highlightEntityIds = entityIds
                        }
                    }
            } catch (_: Exception) {
            }
        }

        //监听养成材料变化
        viewModelScope.launchIO {
            try {
                cultivateItemsDao.getCultivateIdsMaterialsMapFlowByProjectId(projectId)
                    .collectLatest { materialsMap ->
                        val materialIds = materialsMap.values
                            .flatten()
                            .map { it.itemId }
                            .toSet()
                        withContext(Dispatchers.Main) {
                            highlightMaterialIds = materialIds
                        }
                    }
            } catch (_: Exception) {
            }
        }
    }

}