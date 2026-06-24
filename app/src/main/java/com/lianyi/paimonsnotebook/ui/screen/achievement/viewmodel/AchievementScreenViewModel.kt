package com.lianyi.paimonsnotebook.ui.screen.achievement.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lianyi.paimonsnotebook.common.data.repository.AchievementRepository
import com.lianyi.paimonsnotebook.common.database.achievement.entity.AchievementUser
import com.lianyi.paimonsnotebook.common.extension.intent.setComponentName
import com.lianyi.paimonsnotebook.common.extension.scope.launchIO
import com.lianyi.paimonsnotebook.common.extension.scope.launchMain
import com.lianyi.paimonsnotebook.common.extension.string.errorNotify
import com.lianyi.paimonsnotebook.common.extension.string.warnNotify
import com.lianyi.paimonsnotebook.common.navigation.Routes
import com.lianyi.paimonsnotebook.common.util.enums.LoadingState
import com.lianyi.paimonsnotebook.common.util.json.JSON
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.achievement.AchievementData
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.achievement.service.AchievementService
import com.lianyi.paimonsnotebook.ui.screen.achievement.data.AchievementGoalOverviewData
import com.lianyi.paimonsnotebook.ui.screen.achievement.util.helper.AchievementHelper
import com.lianyi.paimonsnotebook.ui.screen.achievement.view.AchievementGoalScreen
import com.lianyi.paimonsnotebook.ui.screen.achievement.view.AchievementOptionScreen
import com.lianyi.paimonsnotebook.ui.screen.home.util.HomeHelper

class AchievementScreenViewModel : ViewModel() {

    private val achievementService by lazy {
        AchievementService {
            loadingState = LoadingState.Error
        }
    }

    private val achievementList = mutableStateListOf<AchievementData>()

    //成就分类列表
    val achievementGoalList = mutableStateListOf<AchievementGoalOverviewData>()

    //成就列表map,key = goal_id
    private val achievementListGoalMap = mutableMapOf<Int, List<AchievementData>>()

    //成就分类map,key = id , value = overview data
    private val achievementGoalMap = mutableMapOf<Int, AchievementGoalOverviewData>()

    private val achievementsDao = AchievementRepository.achievementsDao
    private val achievementUserDao = AchievementRepository.achievementUserDao

    //当前用户
    var selectedUser by mutableStateOf<AchievementUser?>(null)
        private set

    //当前用户成就完成数量
    var achievementFinishCount = -1
        private set

    //当前用户专辑(分类)完成个数
    var achievementGoalFinishCount = 0
        private set

    //任务进度百分比
    var processPercent by mutableFloatStateOf(0f)
        private set

    //成就个数
    var achievementsCount = 0
        private set

    //成就分类个数
    var achievementGoalCount = 0
        private set

    //原石总数
    var totalPrimogems = 0
        private set

    //已领取原石数
    var earnedPrimogems = 0
        private set

    init {
        //当选中的用户更改时更新数据
        viewModelScope.launchIO {
            achievementUserDao.getSelectedUserFlow().collect { user ->
                launchMain {
                    selectedUser = user
                    updateList()
                }
            }
        }

        //接收用户成就个数变化
        viewModelScope.launchIO {
            achievementsDao.getAllUserAchievementOverviewFlow().collect { list ->
                //防止第一次进入初始化两次列表
                if (achievementFinishCount == -1) {
                    achievementFinishCount = 0
                    return@collect
                }

                if (list.isEmpty() || selectedUser == null) return@collect

                val map = list.associateBy({
                    it.userId
                }) {
                    it.finishCount
                }

                val newValue = map[selectedUser!!.id] ?: 0
                if (newValue == achievementFinishCount) return@collect

                //当新值与当前值不同时才更新数据列表
                achievementFinishCount = newValue
                updateList()
            }
        }
    }

    var loadingState by mutableStateOf(LoadingState.Loading)

    var inputTextValue by mutableStateOf("")

    var showDetailInfo by mutableStateOf(false)

    var resultList = mutableStateListOf<AchievementData>()

    //防止重复更新
    private var isUpdating = false

    //更新列表
    private fun updateList() {
        if (isUpdating) return
        isUpdating = true

        loadingState = LoadingState.Loading
        //记录值归零
        achievementFinishCount = 0
        achievementGoalFinishCount = 0
        processPercent = 0f
        totalPrimogems = 0
        earnedPrimogems = 0

        viewModelScope.launchIO {
            achievementListGoalMap.clear()
            achievementList.clear()
            achievementGoalList.clear()
            achievementGoalMap.clear()


            val mAchievementList = achievementService.achievementList
            achievementListGoalMap += mAchievementList.groupBy { it.goal }

            val userId = selectedUser?.id ?: -1

            // 一次查询该用户所有已完成成就的ID集合（分片处理SQLite 999参数限制）
            val allIds = mAchievementList.map { it.id }
            val finishedAchievements = if (allIds.isNotEmpty() && userId != -1) {
                allIds.chunked(900).flatMap { chunk ->
                    achievementsDao.getAchievementListByUserIdAndIds(userId, chunk).map { it.id }
                }.toSet()
            } else {
                emptySet()
            }

            val mAchievementGoalList =
                achievementService.achievementGoalList.map { goalData ->

                    val list = achievementListGoalMap[goalData.id] ?: listOf()

                    val finishCount = list.count { finishedAchievements.contains(it.id) }
                    val goalCount = list.size

                    //更新成就专辑完成个数
                    if (goalCount == finishCount) {
                        achievementGoalFinishCount++
                    }

                    achievementFinishCount += finishCount

                    // 计算原石统计
                    val goalPrimogems = list.sumOf { it.finishReward.Count }
                    val goalEarnedPrimogems = list.filter { finishedAchievements.contains(it.id) }.sumOf { it.finishReward.Count }
                    totalPrimogems += goalPrimogems
                    earnedPrimogems += goalEarnedPrimogems

                    AchievementGoalOverviewData(
                        goal = goalData,
                        userId = userId,
                        finishCount = finishCount,
                        total = goalCount,
                        totalPrimogems = goalPrimogems,
                        earnedPrimogems = goalEarnedPrimogems
                    )
                }.sortedBy {
                    it.goal.order
                }

            achievementList += mAchievementList
            achievementGoalList += mAchievementGoalList

            //更新成就与分类个数
            achievementGoalCount = achievementGoalList.size
            achievementsCount = achievementList.size

            //更新总览数据
            achievementGoalMap += achievementGoalList.associateBy { it.goal.id }

            //更新进度
            processPercent = achievementFinishCount.toFloat() / achievementsCount

            loadingState = if (selectedUser == null) {
                LoadingState.Empty
            } else {
                LoadingState.Success
            }

            isUpdating = false
        }
    }

    fun onClickGoalItem(goalId: Int) {
        val goal = achievementGoalMap[goalId]

        if (goal == null) {
            "无法查看当前成就的详细信息".warnNotify(false)
            return
        }

        onClickGoalItem(goal)
    }

    private fun onClickGoalItem(goalOverviewData: AchievementGoalOverviewData, targetId: Int = -1) {
        val list = achievementListGoalMap[goalOverviewData.goal.id]

        if (list.isNullOrEmpty()) {
            "出现错误:无法查看当前成就分类".errorNotify()
        }

        HomeHelper.goActivityByIntentNewTask {
            setComponentName(AchievementGoalScreen::class.java)
            putExtra(Routes.EXTRA_GOAL_JSON, JSON.stringify(goalOverviewData))
            putExtra(Routes.EXTRA_TARGET_ID, targetId)

            putExtra(Routes.EXTRA_LIST_JSON, JSON.stringify(list))
        }
    }

    //版本号验证正则表达式
    private val versionNumberExp by lazy {
        Regex("^([1-9][0-9]*)+(\\.[0-9])?\$")
    }

    fun onInputTextValueChange(value: String) {
        this.inputTextValue = value

        resultList.clear()
        val matchResult = versionNumberExp.find(value)

        resultList += if (matchResult != null && matchResult.value.isNotEmpty()) {
            achievementList.filter {
                it.version == value
            }
        } else {
            achievementList.filter { achievementData ->
                achievementData.title.contains(inputTextValue, true) ||
                        achievementData.description.contains(inputTextValue, true)
            }
        }
    }

    fun onClearInputText() {
        this.inputTextValue = ""
    }

    fun goOptionScreen() {
        HomeHelper.goActivityByIntentNewTask {
            setComponentName(AchievementOptionScreen::class.java)
        }
    }

    fun toggleDetailInfo() {
        showDetailInfo = !showDetailInfo
    }

    //当点击搜索结果item时
    fun onClickSearchResultItem(achievementData: AchievementData) {
        val goal = achievementGoalMap[achievementData.goal]

        if (goal == null) {
            "无法查看当前成就的详细信息".warnNotify(false)
            return
        }

        onClickGoalItem(goal, achievementData.id)

        //跳转后清空搜索结果
//        onClearInputText()
    }

}