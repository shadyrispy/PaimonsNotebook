package com.lianyi.paimonsnotebook.common.web.hoyolab.api_sdk.combo_panda

import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment
import com.lianyi.paimonsnotebook.common.data.EmptyData
import com.lianyi.paimonsnotebook.common.extension.request.setHost
import com.lianyi.paimonsnotebook.common.extension.request.setReferer
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.emptyOkHttpClient
import com.lianyi.paimonsnotebook.common.util.request.getAsJson
import com.lianyi.paimonsnotebook.common.util.request.post
import com.lianyi.paimonsnotebook.common.util.request.toRequestBody
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.web.hoyolab.hk4e.sdk.combo_panda.QrcodeFetchData
import com.lianyi.paimonsnotebook.common.web.hoyolab.hk4e.sdk.combo_panda.QrcodeQueryData
import com.lianyi.paimonsnotebook.common.web.hoyolab.passport.QrLoginData
import com.lianyi.paimonsnotebook.common.web.hoyolab.passport.QrLoginStatusData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.auth.GameTokenData
import okhttp3.Request

class QRCodeClient {

    private fun Request.Builder.setXRpc5Defaults(): Request.Builder {
        addHeader("User-Agent", "HYPContainer/1.1.4.133")
        addHeader("Accept", "application/json")
        addHeader("x-rpc-app_id", "ddxf5dufpuyo")
        addHeader("x-rpc-client_type", "3")
        addHeader("x-rpc-device_id", CoreEnvironment.DeviceId53)
        return this
    }

    suspend fun createQrLogin() =
        buildRequest {
            url(ApiEndpoints.AccountCreateQrLogin)

            setXRpc5Defaults()

            "{}".toRequestBody().let { this.post(it) }

        }.getAsJson<QrLoginData>(emptyOkHttpClient)

    suspend fun queryQrLoginStatus(ticket: String) =
        buildRequest {
            url(ApiEndpoints.AccountQueryQrLoginStatus)

            setXRpc5Defaults()

            buildMap {
                put("ticket", ticket)
            }.post(this)

        }.getAsJson<QrLoginStatusData>(emptyOkHttpClient)

    suspend fun fetch() = buildRequest {
        url(ApiEndpoints.getQRCodeFetch())

        buildMap {
            put("app_id", CoreEnvironment.APP_ID)
            put("device", CoreEnvironment.DeviceId40)
        }.post(this)

    }.getAsJson<QrcodeFetchData>(emptyOkHttpClient)

    suspend fun query(ticket: String) = buildRequest {
        url(ApiEndpoints.getQRCodeQuery())

        buildMap {
            put("app_id", CoreEnvironment.APP_ID)
            put("device", CoreEnvironment.DeviceId40)
            put("ticket", ticket)
        }.post(this)

    }.getAsJson<QrcodeQueryData>(emptyOkHttpClient)

    suspend fun scan(param: QRCodeParamData) =
        buildRequest {
            url(ApiEndpoints.getQRCodeScan(param.gameBiz))

            buildMap {
                put("app_id", param.appId)
                put("device", CoreEnvironment.DeviceId)
                put("ticket", param.ticket)
            }.post(this)

            setHost(ApiEndpoints.Hk4eSdkHost)
            setReferer(ApiEndpoints.AppMihoyoReferer)

        }.getAsJson<EmptyData>()

    suspend fun confirm(
        aid: String,
        gameTokenData: GameTokenData,
        param: QRCodeParamData
    ) =
        buildRequest {
            url(ApiEndpoints.getQRCodeConfirm(param.gameBiz))

            val raw = "{\"uid\":\"${aid}\",\"token\":\"${gameTokenData.game_token}\"}"

            buildMap {
                put("app_id", param.appId)
                put("device", CoreEnvironment.DeviceId)
                put(
                    "payload",
                    GamePayloadData(proto = "Account", raw = raw)
                )
                put("ticket", param.ticket)
            }.post(this)

            setHost(ApiEndpoints.Hk4eSdkHost)
            setReferer(ApiEndpoints.AppMihoyoReferer)

        }.getAsJson<EmptyData>()

}
