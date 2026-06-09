package com.lianyi.paimonsnotebook.common.web.hoyolab.passport

data class QrLoginData(
    val url: String,
    val ticket: String
)

data class QrLoginStatusData(
    val status: String,
    val tokens: List<TokenWrapper>?,
    val user_info: QrUserInfo?,
    val need_realperson: Boolean?
) {
    data class TokenWrapper(
        val token: String,
        val token_type: Int
    )

    data class QrUserInfo(
        val aid: String,
        val mid: String
    )

    val isConfirmed: Boolean
        get() = status == "Confirmed"

    val sToken: String?
        get() = tokens?.firstOrNull { it.token_type == 1 }?.token
}
