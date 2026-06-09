package com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.auth

import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment
import com.lianyi.paimonsnotebook.common.database.user.entity.User
import com.lianyi.paimonsnotebook.common.extension.request.setDynamicSecret
import com.lianyi.paimonsnotebook.common.extension.request.setUser
import com.lianyi.paimonsnotebook.common.util.hoyolab.DynamicSecret
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.getAsJson
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.web.hoyolab.cookie.CookieHelper
import okhttp3.Request

class AuthClient {

    private fun Request.Builder.setXRpcDefaults(): Request.Builder {
        addHeader("User-Agent", CoreEnvironment.HoyolabGameRecordUA)
        addHeader("Accept", "application/json")
        addHeader("x-rpc-app_version", CoreEnvironment.XrpcVersion)
        addHeader("x-rpc-client_type", "5")
        addHeader("x-rpc-device_id", CoreEnvironment.DeviceId)
        addHeader("x-rpc-device_fp", CoreEnvironment.DeviceFp)
        return this
    }

    suspend fun getActionTicketBySToken(user: User, action: String = "game_role") =
        buildRequest {
            url(ApiEndpoints.AuthActionTicket(action, "", user.aid))

            setXRpcDefaults()
            setUser(user, CookieHelper.Type.SToken)
            setDynamicSecret(DynamicSecret.SaltType.K2, DynamicSecret.Version.Gen1, true)
        }.getAsJson<ActionTicketData>()

    suspend fun getGameToken(user: User) =
        buildRequest {
            url(ApiEndpoints.getGameToken(user.aid))

            setUser(user, CookieHelper.Type.SToken)
        }.getAsJson<GameTokenData>()

}
