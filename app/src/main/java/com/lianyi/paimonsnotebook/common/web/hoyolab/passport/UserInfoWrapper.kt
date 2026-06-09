package com.lianyi.paimonsnotebook.common.web.hoyolab.passport

data class UserInfoWrapper(
    val user_info: UserInformation?,
    val bindings: List<BindingInfo>?,
)
