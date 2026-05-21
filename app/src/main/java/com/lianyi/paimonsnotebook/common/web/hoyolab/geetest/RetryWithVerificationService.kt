package com.lianyi.paimonsnotebook.common.web.hoyolab.geetest

import com.lianyi.paimonsnotebook.common.data.ResultData

class RetryWithVerificationService(
    private val geetestService: GeetestService
) {
    suspend fun <T> executeWithRetry(
        request: suspend (String) -> ResultData<T>,
        onNeedVerification: (List<Pair<String, String>>?) -> Unit
    ): ResultData<T> {
        var challenge = ""
        var result = request(challenge)

        if (result.validate && result.responseHeaders != null) {
            onNeedVerification(result.responseHeaders)
            
            return result
        }

        return result
    }

    suspend fun <T> executeWithAutoRetry(
        request: suspend (String) -> ResultData<T>,
        onNeedVerification: () -> Unit,
        getAigis: () -> String?,
        onVerificationSuccess: () -> Unit,
        onVerificationFailed: () -> Unit
    ): ResultData<T> {
        var challenge = ""
        var result = request(challenge)

        if (result.validate) {
            onNeedVerification()
            
            val aigis = getAigis()
            if (aigis != null) {
                var verified = false
                
                geetestService.verifyChallenge(
                    rawAigis = aigis,
                    onSuccess = { newChallenge ->
                        challenge = newChallenge
                        verified = true
                    },
                    onFailed = {
                        verified = false
                    }
                )

                if (verified && challenge.isNotBlank()) {
                    result = request(challenge)
                    if (result.success) {
                        onVerificationSuccess()
                    } else {
                        onVerificationFailed()
                    }
                } else {
                    onVerificationFailed()
                }
            }
        }

        return result
    }
}
