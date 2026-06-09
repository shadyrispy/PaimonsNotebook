package com.lianyi.paimonsnotebook.common.web.hoyolab

import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.getAsJson
import com.lianyi.paimonsnotebook.common.database.user.entity.User as UserEntity

class GeetestService {

    data class CardVerificationHeaders(
        val gt: String,
        val challenge: String,
        val seccode: String
    )

    suspend fun verifyXrpcChallenge(user: UserEntity, headers: CardVerificationHeaders): String {
        return try {
            val response = buildRequest {
                url(ApiEndpoints.geetestGet(headers.gt, headers.challenge, headers.seccode))
            }.getAsJson<Map<String, Any>>()

            if (response.success) {
                response.data["challenge"]?.toString() ?: ""
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun verifyXrpcChallenge(user: UserEntity, gt: String, challenge: String, seccode: String): String {
        return verifyXrpcChallenge(user, CardVerificationHeaders(gt, challenge, seccode))
    }
}