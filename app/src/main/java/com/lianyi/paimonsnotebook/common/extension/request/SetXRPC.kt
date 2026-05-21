package com.lianyi.paimonsnotebook.common.extension.request

import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment
import okhttp3.Request


/*
*
* 设置请求头app信息
* */
fun Request.Builder.setXRpcAppInfo(
    clientType: String = CoreEnvironment.ClientType,
    appId: String = "bll8iq97cem8"
) {
    addHeader("x-rpc-app_version", CoreEnvironment.XrpcVersion)
    addHeader("x-rpc-client_type", clientType)
    addHeader("x-rpc-app_id", appId)
}

fun Request.Builder.setXRpcChallenge(value:String) = this.addHeader("x-rpc-challenge",value)

fun Request.Builder.setXRpcClientType(value:String) = this.header("x-rpc-client_type",value)

fun Request.Builder.setXRpcAigis(value:String) = this.header("x-rpc-aigis",value)

fun Request.Builder.setXRpcSigngame(value:String = "hk4e") = this.header("x-rpc-signgame",value)

fun Request.Builder.setXRpcToolVersion(value:String = "v5.0.1-ys") = this.header("x-rpc-tool_verison",value)

fun Request.Builder.setXRpcChallengeGame(value:Int = 2) = this.header("x-rpc-challenge_game", value.toString())

fun Request.Builder.setXRpcChallengePath(value:String) = this.header("x-rpc-challenge_path",value)

fun Request.Builder.setReferer(value:String) = this.header("Referer", value)
