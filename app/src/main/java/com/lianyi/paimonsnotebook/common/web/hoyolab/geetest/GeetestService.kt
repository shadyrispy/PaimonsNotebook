package com.lianyi.paimonsnotebook.common.web.hoyolab.geetest

import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.utils.GT3ServiceNode
import com.lianyi.paimonsnotebook.common.util.json.JSON
import com.lianyi.paimonsnotebook.common.web.hoyolab.passport.XRpcAigisData

class GeetestService(
    private val getGeetestUtils: () -> GT3GeetestUtils
) {
    private var geetestUtils: GT3GeetestUtils? = null

    suspend fun verifyChallenge(
        rawAigis: String,
        onSuccess: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        val aigisData = JSON.parse<XRpcAigisData>(rawAigis)

        geetestUtils = getGeetestUtils.invoke()

        val config = GT3ConfigBean().apply {
            pattern = 1
            isCanceledOnTouchOutside = false
            lang = "zh-cn"
            timeout = 10000
            webviewTimeout = 10000
            gt3ServiceNode = GT3ServiceNode.NODE_IPV6

            listener = object : com.geetest.sdk.GT3Listener() {
                override fun onButtonClick() {
                    this@apply.api1Json = JSONObject().apply {
                        put("gt", aigisData.data.gt)
                        put("challenge", aigisData.data.challenge)
                        put("success", aigisData.data.success)
                        put("new_captcha", aigisData.data.new_captcha)
                    }
                    geetestUtils?.getGeetest()
                }

                override fun onDialogResult(result: String?) {
                    if (!result.isNullOrBlank()) {
                        val challenge = "${aigisData.session_id};${result}"
                        onSuccess(challenge)
                    } else {
                        onFailed()
                    }
                    geetestUtils?.destory()
                    geetestUtils = null
                }
            }
        }

        geetestUtils?.init(config)
        geetestUtils?.startCustomFlow()
    }

    fun destroy() {
        geetestUtils?.destory()
        geetestUtils = null
    }
}
