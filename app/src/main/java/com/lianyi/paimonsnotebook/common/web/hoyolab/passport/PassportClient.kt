package com.lianyi.paimonsnotebook.common.web.hoyolab.passport

import android.os.Build
import com.lianyi.core.common.util.RSAHelper
import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment
import com.lianyi.paimonsnotebook.common.database.user.entity.User
import com.lianyi.paimonsnotebook.common.extension.request.setDynamicSecret
import com.lianyi.paimonsnotebook.common.extension.request.setUser
import com.lianyi.paimonsnotebook.common.extension.request.setXRpcAigis
import com.lianyi.paimonsnotebook.common.util.hoyolab.DynamicSecret
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.emptyOkHttpClient
import com.lianyi.paimonsnotebook.common.util.request.getAsJson
import com.lianyi.paimonsnotebook.common.util.request.post
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.web.hoyolab.cookie.Cookie
import com.lianyi.paimonsnotebook.common.web.hoyolab.cookie.CookieHelper
import okhttp3.Request

class PassportClient {

    private fun Request.Builder.setXRpc2Defaults(): Request.Builder {
        addHeader("User-Agent", CoreEnvironment.HoyolabMobileUA)
        addHeader("Accept", "application/json")
        addHeader("x-rpc-aigis", "")
        addHeader("x-rpc-app_id", "bll8iq97cem8")
        addHeader("x-rpc-app_version", CoreEnvironment.XrpcVersion)
        addHeader("x-rpc-client_type", "2")
        addHeader("x-rpc-device_id", CoreEnvironment.DeviceId)
        addHeader("x-rpc-device_name", "")
        addHeader("x-rpc-device_fp", CoreEnvironment.DeviceFp)
        addHeader("x-rpc-game_biz", "bbs_cn")
        addHeader("x-rpc-sdk_version", "2.16.0")
        return this
    }

    suspend fun loginByTicket(ticket: String) =
        buildRequest {
            url(ApiEndpoints.loginByAuthTicket())

            addHeader("x-rpc-app_id", CoreEnvironment.AuthorizeKeyStarRail)
            addHeader("x-rpc-client_type", CoreEnvironment.ClientType)
            addHeader("x-rpc-sys_version", Build.VERSION.RELEASE)
            addHeader("x-rpc-device_fp", CoreEnvironment.DeviceFp)
            addHeader("x-rpc-device_name", "${Build.BRAND} ${Build.MODEL}")
            addHeader("x-rpc-device_id", CoreEnvironment.DeviceId)
            addHeader("x-rpc-device_model", Build.MODEL)
            addHeader("x-rpc-sdk_version", CoreEnvironment.SDKVersion)

            buildMap {
                put("ticket", ticket)
            }.post(this)

        }.getAsJson<LoginResultData>(emptyOkHttpClient)


    suspend fun createLoginCaptcha(mobile: String, areaCode: String, aigis: String = "") =
        buildRequest {
            url(ApiEndpoints.createLoginCaptcha)

            setXRpc2Defaults()

            if (aigis.isNotEmpty()) {
                setXRpcAigis(aigis)
            }

            buildMap {
                put("area_code", RSAHelper.encryptWithPublicKeyString(areaCode))
                put("mobile", RSAHelper.encryptWithPublicKeyString(mobile))
            }.post(this)

            setDynamicSecret(DynamicSecret.SaltType.PROD, DynamicSecret.Version.Gen2, true)

        }.getAsJson<CreateLoginCaptchaData>(carryResponseHeaders = true)

    suspend fun loginByMobileCaptcha(
        actionType: String,
        mobile: String,
        areaCode: String,
        code: String,
        aigis: String = ""
    ) = buildRequest {
        url(ApiEndpoints.loginByMobileCaptcha)

        setXRpc2Defaults()

        if (aigis.isNotEmpty()) {
            setXRpcAigis(aigis)
        }

        buildMap {
            put("action_type", actionType)
            put("captcha", code)
            put("area_code", RSAHelper.encryptWithPublicKeyString(areaCode))
            put("mobile", RSAHelper.encryptWithPublicKeyString(mobile))
        }.post(this)

        setDynamicSecret(DynamicSecret.SaltType.PROD, DynamicSecret.Version.Gen2, true)

    }.getAsJson<LoginResultData>(carryResponseHeaders = true)


    suspend fun getTokenByGameToken(accountId: Int, gameToken: String) = buildRequest {
        url(ApiEndpoints.getTokenByGameToken)

        addHeader("x-rpc-app_id", "bll8iq97cem8")

        buildMap {
            put("account_id", accountId)
            put("game_token", gameToken)
        }.post(this)

    }.getAsJson<GetTokenByGameTokenData>(emptyOkHttpClient)


    suspend fun getCookieTokenBySToken(user: User) =
        buildRequest {
            url(ApiEndpoints.AccountGetCookieTokenBySToken)

            setXRpc2Defaults()
            setUser(user, CookieHelper.Type.SToken)

            setDynamicSecret(DynamicSecret.SaltType.PROD, DynamicSecret.Version.Gen2, true)
        }.getAsJson<CookieAccountInfoByStokenData>(emptyOkHttpClient)

    suspend fun getCookieTokenBySToken(stokenV2: Cookie) =
        buildRequest {
            url(ApiEndpoints.AccountGetCookieTokenBySToken)

            setXRpc2Defaults()
            addHeader("Cookie", stokenV2.toString())

            setDynamicSecret(DynamicSecret.SaltType.PROD, DynamicSecret.Version.Gen2, true)
        }.getAsJson<CookieAccountInfoByStokenData>(emptyOkHttpClient)

    suspend fun getLTokenBySToken(user: User) =
        buildRequest {
            url(ApiEndpoints.AccountGetLtokenByStoken)

            setXRpc2Defaults()
            setUser(user, CookieHelper.Type.SToken)

            setDynamicSecret(DynamicSecret.SaltType.PROD, DynamicSecret.Version.Gen2, true)
        }.getAsJson<LtokenByStokenData>(emptyOkHttpClient)

    suspend fun getLTokenBySToken(stokenV2: Cookie) =
        buildRequest {
            url(ApiEndpoints.AccountGetLtokenByStoken)

            setXRpc2Defaults()
            addHeader("Cookie", stokenV2.toString())

            setDynamicSecret(DynamicSecret.SaltType.PROD, DynamicSecret.Version.Gen2, true)
        }.getAsJson<LtokenByStokenData>(emptyOkHttpClient)

}
