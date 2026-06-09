package com.lianyi.paimonsnotebook.common.web.hoyolab.public_data_api

import com.google.gson.Gson
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.getAsText
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.web.hoyolab.public_data_api.device_fp.DeviceFpClient

class PublicDataApiClient {

    private val gson by lazy { Gson() }
    val deviceFpClient: DeviceFpClient = DeviceFpClient()

    suspend fun getExtList(platform: Int = 2): GetExtListData? = try {
        val request = buildRequest {
            url(ApiEndpoints.getExtList(platform))
        }
        gson.fromJson(request.getAsText(), GetExtListData::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
