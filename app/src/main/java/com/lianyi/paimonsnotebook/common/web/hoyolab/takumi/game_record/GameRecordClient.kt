package com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record

import com.lianyi.paimonsnotebook.common.core.enviroment.CoreEnvironment
import com.lianyi.paimonsnotebook.common.data.ResultData
import com.lianyi.paimonsnotebook.common.data.hoyolab.PlayerUid
import com.lianyi.paimonsnotebook.common.data.hoyolab.user.UserAndUid
import com.lianyi.paimonsnotebook.common.extension.request.setDynamicSecret
import com.lianyi.paimonsnotebook.common.extension.request.setUser
import com.lianyi.paimonsnotebook.common.extension.request.setXRpcChallenge
import com.lianyi.paimonsnotebook.common.util.hoyolab.DynamicSecret
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.gameRecordOkHttpClient
import com.lianyi.paimonsnotebook.common.util.request.getAsJson
import com.lianyi.paimonsnotebook.common.util.request.post
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.web.hoyolab.cookie.CookieHelper
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.binding.UserGameRoleData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record.abyss.SpiralAbyssData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record.character.CharacterDetailData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record.character.CharacterListData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record.daily_note.DailyNoteData
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record.daily_note.DailyNoteWidgetData
import com.lianyi.paimonsnotebook.common.database.user.entity.User as UserEntity
import okhttp3.Request

class GameRecordClient {

    private fun Request.Builder.setGameRecordDefaults(): Request.Builder {
        addHeader("User-Agent", CoreEnvironment.HoyolabGameRecordUA)
        addHeader("Accept", "application/json")
        addHeader("x-rpc-app_version", CoreEnvironment.XrpcVersion)
        addHeader("x-rpc-client_type", "5")
        addHeader("x-rpc-device_id", CoreEnvironment.DeviceId)
        addHeader("x-rpc-device_fp", CoreEnvironment.DeviceFp)
        return this
    }

    suspend fun getDailyNote(
        user: UserAndUid,
        challenge: String = "",
    ) = getDailyNote(user.userEntity, user.playerUid, challenge)

    suspend fun getDailyNote(
        user: UserEntity,
        role: UserGameRoleData.Role,
        challenge: String = "",
    ) = getDailyNote(user, PlayerUid(role.game_uid, role.region), challenge)

    suspend fun getDailyNoteWithFallback(
        user: UserEntity,
        playerUid: PlayerUid,
        challenge: String = ""
    ): ResultData<DailyNoteData> = getDailyNote(user, playerUid, challenge)

    suspend fun getDailyNoteForWidget(
        user: UserEntity
    ) = buildRequest {
        url(ApiEndpoints.CardWidgetDataV2)
        setGameRecordDefaults()
        setUser(user, CookieHelper.Type.LToken or CookieHelper.Type.SToken or CookieHelper.Type.Cookie)
        addHeader("Referer", ApiEndpoints.WebStaticMihoyoReferer)
        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
    }.getAsJson<DailyNoteWidgetData>(gameRecordOkHttpClient)

    private suspend fun getDailyNote(
        user: UserEntity,
        playerUid: PlayerUid,
        challenge: String = "",
    ) = buildRequest {
        url(ApiEndpoints.GameRecordDailyNote(playerUid))
        setGameRecordDefaults()
        setUser(user = user, cookieType = CookieHelper.Type.Cookie)
        addHeader("Referer", ApiEndpoints.WebStaticMihoyoReferer)

        setDynamicSecret(
            saltType = DynamicSecret.SaltType.X4,
            version = DynamicSecret.Version.Gen2
        )

        if (challenge.isNotBlank() && challenge != "error") {
            setXRpcChallenge(challenge)
        }
    }.getAsJson<DailyNoteData>(gameRecordOkHttpClient)

    suspend fun getSpiralAbyssData(
        user: UserAndUid,
        scheduleType: String,
    ) = buildRequest {
        url(ApiEndpoints.gameRecordSpiralAbyss(scheduleType = scheduleType, uid = user.playerUid))
        setGameRecordDefaults()
        setUser(user.userEntity, CookieHelper.Type.Cookie)
        addHeader("Referer", ApiEndpoints.WebStaticMihoyoReferer)
        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
    }.getAsJson<SpiralAbyssData>(gameRecordOkHttpClient)

    suspend fun getCharacterList(
        user: UserAndUid,
    ) = buildRequest {
        url(ApiEndpoints.GameRecordCharacter)
        setGameRecordDefaults()
        setUser(user.userEntity, CookieHelper.Type.Cookie)
        addHeader("Referer", ApiEndpoints.WebStaticMihoyoReferer)

        buildMap {
            put("role_id", user.playerUid.value)
            put("server", user.playerUid.region)
            put("sort_type", 1)
        }.post(this)

        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
    }.getAsJson<CharacterListData>(gameRecordOkHttpClient)

    suspend fun getCharacterDetail(
        user: UserAndUid,
        characterIds: List<Int>
    ) = buildRequest {
        url(ApiEndpoints.GameRecordCharacterDetail)
        setGameRecordDefaults()
        setUser(user.userEntity, CookieHelper.Type.Cookie)
        addHeader("Referer", ApiEndpoints.WebStaticMihoyoReferer)

        buildMap {
            put("role_id", user.playerUid.value)
            put("server", user.playerUid.region)
            put("character_ids", characterIds)
            put("sort_type", 1)
        }.post(this)

        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
    }.getAsJson<CharacterDetailData>(gameRecordOkHttpClient)
}
