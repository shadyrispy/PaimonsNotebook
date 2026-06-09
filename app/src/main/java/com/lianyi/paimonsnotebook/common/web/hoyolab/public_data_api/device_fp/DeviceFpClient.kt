package com.lianyi.paimonsnotebook.common.web.hoyolab.public_data_api.device_fp

import com.google.gson.Gson
import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment
import com.lianyi.paimonsnotebook.common.data.ResultData
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.getAsText
import com.lianyi.paimonsnotebook.common.util.request.post
import com.lianyi.paimonsnotebook.common.util.request.toRequestBody
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import org.json.JSONObject

class DeviceFpClient {

    private val gson = Gson()

    suspend fun getFingerprintAsync(data: DeviceFpData): ResultData<DeviceFpWrapper> = try {
        val request = buildRequest {
            url(ApiEndpoints.getFp)
            post(JSONObject().apply {
                put("device_id", data.device_id)
                put("bbs_device_id", data.bbs_device_id)
                put("seed_id", data.seed_id)
                put("seed_time", data.seed_time)
                put("platform", data.platform)
                put("device_fp", data.device_fp)
                put("app_name", data.app_name)
                put("ext_fields", data.ext_fields)
            }.toString().toRequestBody())
        }
        val resp = gson.fromJson(request.getAsText(), ResultDataDto::class.java)
        val deviceFpWrapper = resp?.data
            ?: DeviceFpWrapper(device_fp = "", code = -1, message = "empty")
        ResultData(
            message = resp?.message.orEmpty(),
            retcode = resp?.retcode ?: ResultData.UNKNOWN_EXCEPTION,
            data = deviceFpWrapper,
        )
    } catch (e: Exception) {
        e.printStackTrace()
        ResultData(
            message = e.message ?: "unknown",
            retcode = ResultData.UNKNOWN_EXCEPTION,
            data = DeviceFpWrapper(device_fp = "", code = -1, message = "exception"),
        )
    }

    fun buildDefaultDeviceFpData(
        bbsDeviceId: String = CoreEnvironment.BBSDeviceId,
        deviceId: String = CoreEnvironment.DeviceId,
        deviceIdSeed: String = CoreEnvironment.DeviceIdSeed,
        deviceIdSeedTime: Long = CoreEnvironment.DeviceIdSeedTime,
        platform: String = CoreEnvironment.ClientType,
        currentFp: String = "",
    ): DeviceFpData {
        val safeFp = currentFp.ifBlank {
            (1..13).map { "0123456789abcdef".random() }.joinToString("")
        }
        val seedTimeStr = if (deviceIdSeedTime > 0) "$deviceIdSeedTime" else "${System.currentTimeMillis()}"
        return DeviceFpData(
            device_id = bbsDeviceId,
            bbs_device_id = deviceId,
            seed_id = deviceIdSeed,
            seed_time = seedTimeStr,
            platform = platform,
            device_fp = safeFp,
            app_name = "bbs_cn",
            ext_fields = DeviceFpHelper.getExtFields(),
        )
    }
}

private data class ResultDataDto(
    val retcode: Int,
    val message: String,
    val data: DeviceFpWrapper?,
)
