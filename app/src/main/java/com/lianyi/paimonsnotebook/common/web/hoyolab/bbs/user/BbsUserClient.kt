package com.lianyi.paimonsnotebook.common.web.hoyolab.bbs.user

import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment
import com.lianyi.paimonsnotebook.common.database.user.entity.User
import com.lianyi.paimonsnotebook.common.extension.request.setReferer
import com.lianyi.paimonsnotebook.common.extension.request.setUser
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.getAsJson
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.web.hoyolab.cookie.CookieHelper
import okhttp3.Request

class BbsUserClient {

    private fun Request.Builder.setXRpcDefaults(): Request.Builder {
        addHeader("User-Agent", CoreEnvironment.HoyolabGameRecordUA)
        addHeader("Accept", "application/json")
        addHeader("x-rpc-app_version", CoreEnvironment.XrpcVersion)
        addHeader("x-rpc-client_type", "5")
        addHeader("x-rpc-device_id", CoreEnvironment.DeviceId)
        addHeader("x-rpc-device_fp", CoreEnvironment.DeviceFp)
        return this
    }

    suspend fun getFullInfo(user: User) =
        buildRequest {
            url(ApiEndpoints.UserFullInfo)

            setXRpcDefaults()
            setUser(user, CookieHelper.Type.LToken)

            setReferer("https://bbs-api.mihoyo.com")
        }.getAsJson<UserFullInfoData>()
}
