package com.lianyi.paimonsnotebook.common.web.hoyolab.public_data_api.device_fp

data class DeviceFpData(
    val device_id: String,
    val bbs_device_id: String,
    val seed_id: String,
    val seed_time: String,
    val platform: String,
    val device_fp: String,
    val app_name: String,
    val ext_fields: String,
)
