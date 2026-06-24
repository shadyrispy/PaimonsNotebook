package com.lianyi.paimonsnotebook.ui.screen.gacha.util

import com.google.gson.stream.JsonReader
import com.lianyi.paimonsnotebook.common.extension.file.createJsonReader
import com.lianyi.paimonsnotebook.common.extension.json.findField
import com.lianyi.paimonsnotebook.common.util.metadata.genshin.uigf.UIGFHelper
import com.lianyi.paimonsnotebook.ui.screen.gacha.data.UIGFInfoCompat
import com.lianyi.paimonsnotebook.ui.screen.gacha.data.UIGFJsonData
import com.lianyi.paimonsnotebook.ui.screen.gacha.data.UIGFJsonV4Data
import java.io.File
import java.io.InputStreamReader

class GachaItemsParser {

    //获取uigf version
    fun getUIGFVersion(file: File): UIGFInfoCompat? {
        val jsonReader = JsonReader(InputStreamReader(file.inputStream()))

        val hasInfoField = jsonReader.findField("info")

        if (!hasInfoField) {
            return null
        }

        var uigfVersion = ""
        var version = ""
        var regionTimeZone = 0L


        jsonReader.apply {
            beginObject()

            while (hasNext()) {
                when (nextName()) {
                    UIGFHelper.Field.Info.UIGFVersion -> uigfVersion = nextString()
                    UIGFHelper.Field.Info.Version -> version = nextString()
                    UIGFHelper.Field.Info.RegionTimeZone -> regionTimeZone =
                        nextString().toLongOrNull() ?: 0L

                    else -> {
                        skipValue()
                    }
                }
            }

            endObject()
        }

        jsonReader.close()

        return UIGFInfoCompat(
            uigfVersion = uigfVersion,
            version = version,
            regionTimeZone = regionTimeZone
        )
    }

    fun getUIGFJsonPropertyListCompat(file: File): List<Pair<String, String>> {
        val uigfInfoCompat =
            getUIGFVersion(file) ?: error("无法读取该json中的info对象")

        //正常情况下不会同时触发v3与v4的判断
        //v3
        if (!uigfInfoCompat.uigfVersion.isNullOrBlank()) {
            return getUIGFInfoV3(file).getPropertyList()
        }

        //v4
        if (!uigfInfoCompat.version.isNullOrBlank()) {
            return getUIGFInfoV4(file).getPropertyList()
        }

        return emptyList()
    }

    //获取json info对象,指针需指向info的value部分
    fun getUIGFInfoV3(file: File): UIGFJsonData.Info {
        val jsonReader = file.createJsonReader()

        val hasInfoField = jsonReader.findField("info")

        if (!hasInfoField) {
            error("数据结构错误:无法读取该json中的[info]字段")
        }

        val infoMap = mutableMapOf<String, String>()

        jsonReader.apply {
            beginObject()

            while (hasNext()) {
                infoMap[nextName()] = nextString()
            }

            endObject()
            close()
        }

        //检查info是否包含所需的字段
        UIGFHelper.Field.Info.requiredFields.forEach { infoField ->
            if (infoMap[infoField].isNullOrBlank()) {
                error("字段缺失错误:data.info中的[$infoField]字段未找到")
            }
        }

        val uid = infoMap[UIGFHelper.Field.Info.Uid]!!

        //此处转换时区,当时区不存在时,根据UID生成
        val regionTimeZone =
            infoMap[UIGFHelper.Field.Info.RegionTimeZone]?.toLongOrNull()
                ?: UIGFHelper.getRegionTimeZoneByUid(uid)


        return UIGFJsonData.Info(
            uid = uid,
            lang = infoMap[UIGFHelper.Field.Info.Lang] ?: "",
            export_timestamp = infoMap[UIGFHelper.Field.Info.ExportTimestamp]
                ?.toLongOrNull() ?: 0L,
            export_time = infoMap[UIGFHelper.Field.Info.ExportTime] ?: "",
            export_app = infoMap[UIGFHelper.Field.Info.ExportApp] ?: "",
            export_app_version = infoMap[UIGFHelper.Field.Info.ExportAppVersion] ?: "",
            uigf_version = infoMap[UIGFHelper.Field.Info.UIGFVersion]!!,
            region_time_zone = regionTimeZone
        )
    }

    //通过不同的游戏id获取记录中的uid
    private fun getUidListByField(
        jsonReader: JsonReader,
        gameId: String = "hk4e"
    ): List<String> {
        val uidList = mutableListOf<String>()

        val hasGameIdItems = jsonReader.findField(gameId)

        if (!hasGameIdItems) {
            return emptyList()
        }

        jsonReader.apply {
            beginArray()

            while (hasNext()) {
                beginObject()

                while (hasNext()) {

                    if (nextName() == "uid") {
                        uidList += nextString()
                    } else {
                        skipValue()
                    }

                }
                endObject()
            }
            endArray()

            close()
        }

        return uidList
    }

    fun getUIGFInfoV4(file: File): UIGFJsonV4Data.Info {
        val jsonReader = JsonReader(InputStreamReader(file.inputStream()))

        val hasInfoFiled = jsonReader.findField("info")

        if (!hasInfoFiled) {
            error("数据结构错误:无法读取该json中的[info]字段")
        }

        val infoMap = mutableMapOf<String, String>()

        jsonReader.apply {
            beginObject()

            while (hasNext()) {
                infoMap[nextName()] = nextString()
            }

            endObject()
            close()
        }

        UIGFHelper.Field.Info.requiredFieldsV4.forEach { infoField ->
            if (infoMap[infoField].isNullOrBlank()) {
                error("字段缺失错误:{data}.info中的[$infoField]字段未找到")
            }
        }

        return UIGFJsonV4Data.Info(
            exportTimestamp = infoMap[UIGFHelper.Field.Info.ExportTimestamp] ?: "",
            exportApp = infoMap[UIGFHelper.Field.Info.ExportApp] ?: "",
            exportAppVersion = infoMap[UIGFHelper.Field.Info.ExportAppVersion] ?: "",
            version = infoMap[UIGFHelper.Field.Info.Version] ?: ""
        ).apply {
            uidList += getUidListByField(JsonReader(InputStreamReader(file.inputStream())))
        }
    }
}
