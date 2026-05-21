package com.lianyi.paimonsnotebook.common.web.hoyolab.geetest

interface VerificationCallback {
    fun onNeedVerification(headers: List<Pair<String, String>>?)
    fun onVerificationSuccess(challenge: String)
    fun onVerificationFailed()
}
