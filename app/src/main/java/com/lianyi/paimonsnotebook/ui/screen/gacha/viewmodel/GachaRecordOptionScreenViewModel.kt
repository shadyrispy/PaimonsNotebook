package com.lianyi.paimonsnotebook.ui.screen.gacha.viewmodel

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lianyi.paimonsnotebook.common.data.hoyolab.PlayerUid
import com.lianyi.paimonsnotebook.common.data.hoyolab.user.User
import com.lianyi.paimonsnotebook.common.data.hoyolab.user.UserAndUid
import com.lianyi.paimonsnotebook.common.data.repository.GachaRepository
import com.lianyi.paimonsnotebook.common.extension.data_store.editValue
import com.lianyi.paimonsnotebook.common.extension.intent.setComponentName
import com.lianyi.paimonsnotebook.common.extension.string.errorNotify
import com.lianyi.paimonsnotebook.common.extension.string.notify
import com.lianyi.paimonsnotebook.common.extension.string.warnNotify
import com.lianyi.paimonsnotebook.common.util.data_store.PreferenceKeys
import com.lianyi.paimonsnotebook.common.util.data_store.dataStoreValues
import com.lianyi.paimonsnotebook.common.util.file.FileHelper
import com.lianyi.paimonsnotebook.common.util.metadata.genshin.uigf.UIGFHelper
import com.lianyi.paimonsnotebook.common.util.system_service.SystemService
import com.lianyi.paimonsnotebook.common.util.time.TimeHelper
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.web.hoyolab.hk4e.event.gacha_info.GachaInfoClient
import com.lianyi.paimonsnotebook.common.web.hoyolab.hk4e.event.gacha_info.GachaQueryConfigData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.binding.BindingClient
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.binding.GameAuthKeyData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.binding.GenAuthKeyData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.binding.UserGameRoleData
import com.lianyi.paimonsnotebook.ui.screen.gacha.provider.GachaRecordOptionListProvider
import com.lianyi.paimonsnotebook.ui.screen.gacha.service.GachaItemsExportService
import com.lianyi.paimonsnotebook.ui.screen.gacha.service.GachaItemsImportService
import com.lianyi.paimonsnotebook.ui.screen.gacha.service.GachaLogService
import com.lianyi.paimonsnotebook.ui.screen.gacha.view.GachaRecordExportDataScreen
import com.lianyi.paimonsnotebook.ui.screen.home.util.HomeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class GachaRecordOptionScreenViewModel : ViewModel() {

    val gachaRecordGameUidList = mutableStateListOf<String>()

    internal var currentGameUid by mutableStateOf("")

    internal var gachaRecordExportToUIGFV3 by mutableStateOf(false)

    lateinit var startActivity: ActivityResultLauncher<Intent>

    private val dao by lazy {
        GachaRepository.gachaItemsDao
    }


    init {
        viewModelScope.launch {
            launch {
                dao.getAllGameUidFlow().collect {
                    gachaRecordGameUidList.clear()
                    gachaRecordGameUidList += it
                }
            }
            launch {
                dataStoreValues { preferences ->
                    currentGameUid =
                        preferences[PreferenceKeys.GachaRecordCurrentGameUid] ?: ""
                }
            }
            launch {
                dataStoreValues {
                    gachaRecordExportToUIGFV3 =
                        it[PreferenceKeys.GachaRecordExportToUIGFV3] ?: false
                }
            }
        }
    }

    private val bindingClient by lazy {
        BindingClient()
    }

    private val gachaInfoClient by lazy {
        GachaInfoClient()
    }

    private val gachaLogService by lazy {
        GachaLogService()
    }

    var showLoadingDialog by mutableStateOf(false)
    private var loadingDialogCurrentGachaLogIndex = 0
    private var loadingDialogCurrentGachaLogType = UIGFHelper.gachaList.first()

    var showRequestPermissionDialog by mutableStateOf(false)

    var loadingDialogProgressBarValue by mutableFloatStateOf(0f)
    var loadingDialogDescription by mutableStateOf("即将开始")

    var loadingDialogTitle by mutableStateOf("获取祈愿记录")

    internal var expandedCurrentGameUidDropMenu by mutableStateOf(false)
    internal var showInputUrlDialog by mutableStateOf(false)
    internal var inputDialogValue by mutableStateOf("")

    internal var showSelectAccountGameRoleDialog by mutableStateOf(false)

    internal var showImportUIGFJsonResultDialog by mutableStateOf(false)
    internal val importUIGFJsonPropertyList = mutableStateListOf<Pair<String, String>>()

    var showChooseExportUidDialog by mutableStateOf(false)

    private var activityResultFile: File? = null

    //存储权限检查方法
    lateinit var storagePermission: () -> Boolean

    //祈愿记录导出服务
    private val exportService by lazy {
        GachaItemsExportService()
    }

    //祈愿记录导入服务
    private val importService by lazy {
        GachaItemsImportService()
    }

    //是否显示游戏角色对话框
    var showGameRoleDialog by mutableStateOf(false)

    internal val scope get() = viewModelScope

    val gachaSettings get() = GachaRecordOptionListProvider.gachaSettings(this)
    val importSettings get() = GachaRecordOptionListProvider.importSettings(this)
    val exportSettings get() = GachaRecordOptionListProvider.exportSettings(this)
    val aboutSettings get() = GachaRecordOptionListProvider.aboutSettings(this)

    internal fun showGameRoleDialog() {
        showGameRoleDialog = true
    }

    fun dismissGameRoleDialog(index: Int = 0) {
        showGameRoleDialog = false
    }

    fun showChooseExportUidDialog() {
        showChooseExportUidDialog = true
    }

    fun dismissChooseExportUidDialog() {
        showChooseExportUidDialog = false
    }

    fun onSelectGameRole(user: User, role: UserGameRoleData.Role) {
        generateAuthKeyByAccount(user, role, true)
    }

    //使用账号生成祈愿密钥
    internal fun generateAuthKeyByAccount(
        user: User,
        roleData: UserGameRoleData.Role,
        onlyGetUrl: Boolean = false
    ) {
        showLoadingDialog = true
        loadingDialogTitle = if (onlyGetUrl) "获取祈愿记录URL" else "获取祈愿记录"

        viewModelScope.launch {
            val playerUid = PlayerUid(
                value = roleData.game_uid,
                region = roleData.region
            )
            val result =
                bindingClient.generateAuthenticationKey(UserAndUid(user.userEntity, playerUid))
            if (result.success) {
                val authKey = result.data.asEncodeAuthKeyData()

                if (onlyGetUrl) {
                    SystemService.setClipBoardText(
                        ApiEndpoints.GachaInfoGetGachaLog(
                            GachaQueryConfigData(
                                gachaType = loadingDialogCurrentGachaLogType,
                                gameAuthKeyData = authKey,
                                genAuthKeyData = GenAuthKeyData.createForWebViewGacha(playerUid = playerUid),
                            ).asQueryParameter
                        )
                    )
                    "已将祈愿记录URL复制到剪切板,如果没有复制到剪切板,请检查是否禁用了程序的剪切板权限".notify(
                        autoDismissTime = 6000
                    )
                    dismissGameRoleDialog()
                    showLoadingDialog = false
                } else {
                    getGachaLog(gameAuthKeyData = authKey, playerUid)
                }
            } else {
                "获取失败:${result.retcode},${result.message}".errorNotify()
                showLoadingDialog = false
            }
        }
    }

    //从祈愿Url中获取authKey并尝试获取记录
    internal fun getGaLogFromUrl() {
        showLoadingDialog = true
        loadingDialogTitle = "获取祈愿记录"

        val urls = inputDialogValue.split("?")
        if (urls.isEmpty()) {
            "输入URL有误,请检查后再次尝试".errorNotify()
            return
        }

        val params = urls.last().split("&")
        val map = mutableMapOf<String, String>()

        params.forEach {
            val kv = it.split("=")

            if (kv.isEmpty()) {
                "URL参数错误,请检查后再次尝试".errorNotify()
                return
            }

            val k = kv.first().trim()
            val v = kv.last().trim()
            map[k] = v
        }

        val authKey = map["authkey"]
        val authKeyVer = (map["authkey_ver"] ?: "").toIntOrNull()
        val signType = (map["sign_type"] ?: "").toIntOrNull()

        val region = map["region"]

        if (authKey.isNullOrBlank() || authKeyVer == null || signType == null || region.isNullOrBlank()) {
            "URL中缺少参数,需要的参数:authkey,authkey_ver,sign_type,region".errorNotify()
            showLoadingDialog = false
        } else {
            getGachaLog(
                gameAuthKeyData = GameAuthKeyData(
                    authkey = authKey,
                    authkey_ver = authKeyVer,
                    sign_type = signType
                ),
                PlayerUid(value = "", region = region)
            )
        }
    }

    //重置内部变量
    private fun resetLocalValues() {
        loadingDialogCurrentGachaLogIndex = 0
        loadingDialogProgressBarValue = 0f
        loadingDialogCurrentGachaLogType = UIGFHelper.gachaList.first()
        inputDialogValue = ""
        showLoadingDialog = false

        if (currentGameUid.isEmpty() && gachaRecordGameUidList.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                PreferenceKeys.GachaRecordCurrentGameUid.editValue(gachaRecordGameUidList.first())
            }
        }
    }

    //通过authKey获取祈愿记录
    private fun getGachaLog(gameAuthKeyData: GameAuthKeyData, playerUid: PlayerUid) {
        var success = true

        viewModelScope.launch(Dispatchers.IO) {
            if (!checkGachaLogServiceAvailable()) return@launch

            var endId = "0"

            //当前页面索引
            var pageCount = 1

            //总页面数
            var togglePageCount = 0

            val gameUid = playerUid.value

            //对应卡池类型的最后一条uid,在获取记录时判断是否到达之前的记录处
            var currentGameUidGachaTypeEndId =
                dao.getLastIdByUidAndUIGFGachaType(
                    gameUid,
                    loadingDialogCurrentGachaLogType
                )

            while (true) {
                //更新描述
                loadingDialogDescription =
                    "正在获取${UIGFHelper.getUIGFName(loadingDialogCurrentGachaLogType)}的第${pageCount++}页记录"

                val result = gachaInfoClient.getGachaLogPage(
                    GachaQueryConfigData(
                        gachaType = loadingDialogCurrentGachaLogType,
                        gameAuthKeyData = gameAuthKeyData,
                        genAuthKeyData = GenAuthKeyData.createForWebViewGacha(playerUid = playerUid),
                        endId = endId
                    )
                )

                togglePageCount++

                //请求失败退出
                if (!result.success) {
                    "获取记录时发生了异常:${result.errorMsg}".errorNotify()
                    success = false
                    break
                }

                val list = result.data.list

                //需要添加的记录列表
                val shouldAddItemList = list.takeWhile {
                    it.id != currentGameUidGachaTypeEndId
                }

                //从记录中获取item_id
                if (shouldAddItemList.isNotEmpty()) {
                    //添加至数据库,并通过名称获取item_id
                    //此处的数据长度在[0,20]
                    importService.gachaItemListFlushToDB(shouldAddItemList.map {
                        val model = gachaLogService.getModelByName(it.name)
                        it.asGachaItems(itemId = "${model?.id ?: ""}")
                    })
                }

                //重新设置当前记录的最后记录的id
                endId = if (shouldAddItemList.size < 20) {
                    //更换卡池
                    nextGachaLogItemType()

                    //所有卡池都遍历完毕
                    if (loadingDialogCurrentGachaLogType.isBlank()) {
                        break
                    }

                    //重置当前页面索引
                    pageCount = 1


                    //设置当前祈愿卡池最后的ID
                    currentGameUidGachaTypeEndId =
                        dao.getLastIdByUidAndUIGFGachaType(
                            gameUid,
                            loadingDialogCurrentGachaLogType
                        )

                    ""
                } else {
                    //重新设置指定卡池的最后记录的id
                    list.last().id
                }

                //每10页停留5秒,否则随机1~2秒
                val delayTime = if (togglePageCount % 10 == 0) {
                    5000L
                } else {
                    (1000L..2000L).random()
                }

                delay(delayTime)
            }

            if (success) {
                "祈愿记录获取完毕".notify(closeable = true)
            }

            resetLocalValues()
        }
    }

    private fun nextGachaLogItemType() {
        loadingDialogCurrentGachaLogType =
            if (++loadingDialogCurrentGachaLogIndex < UIGFHelper.uigfGachaTypeCount) {
                UIGFHelper.gachaList[loadingDialogCurrentGachaLogIndex]
            } else {
                ""
            }
        loadingDialogProgressBarValue =
            loadingDialogCurrentGachaLogIndex.toFloat() / UIGFHelper.uigfGachaTypeCount
    }

    internal fun launchSelectJsonActivity() {
        startActivity.launch(Intent(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent.ACTION_OPEN_DOCUMENT
            } else {
                Intent.ACTION_GET_CONTENT
            }
        ).apply {
            type = "application/json"
            addCategory(Intent.CATEGORY_OPENABLE)
        })
    }

    fun activityResult(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            return
        }

        val data = result.data?.data
        if (data == null) {
            "文件数据为空,请检查后再次尝试:data is null".warnNotify()
            return
        }

        val file = FileHelper.uriToFile(data)

        if (file == null) {
            "文件数据为空,请检查后再次尝试:data to uri is null".warnNotify()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            activityResultFile = file
            showLoadingDialog = true

            try {
                importUIGFJsonPropertyList.clear()

                importUIGFJsonPropertyList += importService.getUIGFJsonPropertyListCompat(file)

            } catch (e: Exception) {
                "${e.message}".errorNotify()
            }

            showLoadingDialog = false

            showImportUIGFJsonResultDialog = importUIGFJsonPropertyList.isNotEmpty()
        }
    }

    internal fun saveGachaLogToDB() {
        val file = activityResultFile

        if (file == null) {
            "还未选择文件".warnNotify()
            return
        }

        showLoadingDialog = true
        loadingDialogDescription = "正在将数据保存至本地数据库"

        viewModelScope.launch(Dispatchers.IO) {
            if(!checkGachaLogServiceAvailable()) return@launch

            try {
                importService.saveUIGFJsonItemsCompat(file, gachaLogService)
                "祈愿记录导入结束".notify(closeable = true)

            } catch (e: Exception) {
                "${e.message}".errorNotify()

            } finally {
                showLoadingDialog = false
                resetLocalValues()

            }
        }
    }

    internal fun onClickExportUIGFJson() {
        if (gachaRecordExportToUIGFV3) {

            if (currentGameUid.isEmpty()) {
                "请先选择一个uid".warnNotify(false)
                return
            }

            exportUIGFJson(listOf(currentGameUid))

            return
        }

        showChooseExportUidDialog()
    }

    fun confirmExportSelectedUidRecord(
        uidList: List<String>
    ) {
        dismissChooseExportUidDialog()

        if (uidList.isEmpty()) {
            "必须选择至少一个uid".warnNotify(false)

            return
        }

        exportUIGFJson(uidList)
    }


    private fun exportUIGFJson(uidList: List<String>) {
        if (uidList.isEmpty()) {
            "导出失败:uid列表为空".warnNotify(false)
            return
        }

        loadingDialogTitle = "导出祈愿记录"
        loadingDialogDescription = "正在导出数据,导出的时长与数据量有关"
        showLoadingDialog = true

        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "PaimonsNotebook UIGF_${System.currentTimeMillis()}"
            val file = FileHelper.getUIGFJsonSaveFile(fileName)

            exportService.exportGachaRecordToUIGFJson(uidList, file, gachaRecordExportToUIGFV3)

            showLoadingDialog = false

            "祈愿记录已导出,可通过[导出的祈愿记录功能]管理已导出的祈愿记录文件".notify(
                autoDismissTime = 5000
            )
        }
    }

    //前往祈愿记录导出数据界面
    internal fun goGachaRecordExportDataListScreen() {
        HomeHelper.goActivityByIntentNewTask {
            setComponentName(GachaRecordExportDataScreen::class.java)
        }
    }

    private fun checkGachaLogServiceAvailable(): Boolean {
        if (!gachaLogService.isAvailable) {
            "祈愿服务不可用,请检查元数据完整性后再次尝试".errorNotify()
        }

        return gachaLogService.isAvailable
    }

}