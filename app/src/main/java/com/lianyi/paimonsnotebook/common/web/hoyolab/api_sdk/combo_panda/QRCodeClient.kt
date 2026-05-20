package com.lianyi.paimonsnotebook.common.web.hoyolab.api_sdk.combo_panda

import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment
import com.lianyi.paimonsnotebook.common.data.EmptyData
import com.lianyi.paimonsnotebook.common.extension.request.setHost
import com.lianyi.paimonsnotebook.common.extension.request.setReferer
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.emptyOkHttpClient
import com.lianyi.paimonsnotebook.common.util.request.getAsJson
import com.lianyi.paimonsnotebook.common.util.request.post
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.web.hoyolab.hk4e.sdk.combo_panda.QrcodeFetchData
import com.lianyi.paimonsnotebook.common.web.hoyolab.hk4e.sdk.combo_panda.QrcodeQueryData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.auth.GameTokenData

class QRCodeClient {

    //请求二维码 (按照 PizzaHelperUnited 的方式，不发送额外请求头)
    suspend fun fetch() = buildRequest {
        url(ApiEndpoints.getQRCodeFetch())

        buildMap {
            put("app_id", CoreEnvironment.APP_ID)
            put("device", CoreEnvironment.DeviceId40)
        }.post(this)

    }.getAsJson<QrcodeFetchData>(emptyOkHttpClient)

    //轮询查询二维码状态 (按照 PizzaHelperUnited 的方式，不发送额外请求头)
    suspend fun query(ticket: String) = buildRequest {
        url(ApiEndpoints.getQRCodeQuery())

        buildMap {
            put("app_id", CoreEnvironment.APP_ID)
            put("device", CoreEnvironment.DeviceId40)
            put("ticket", ticket)
        }.post(this)

    }.getAsJson<QrcodeQueryData>(emptyOkHttpClient)

    //扫码 (扫描其他APP的二维码，用于我们的APP作为扫码器的场景)
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

    //确认登录 (用于我们的APP作为扫码器时，确认登录)
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
