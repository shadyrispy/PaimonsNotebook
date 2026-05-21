package com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record

import com.lianyi.paimonsnotebook.common.core.enviroment.EnvironmentClientType
import com.lianyi.paimonsnotebook.common.data.hoyolab.PlayerUid
import com.lianyi.paimonsnotebook.common.data.hoyolab.user.UserAndUid
import com.lianyi.paimonsnotebook.common.extension.request.setDynamicSecret
import com.lianyi.paimonsnotebook.common.extension.request.setUser
import com.lianyi.paimonsnotebook.common.extension.request.setXRpcChallenge
import com.lianyi.paimonsnotebook.common.extension.request.setXRpcChallengeGame
import com.lianyi.paimonsnotebook.common.extension.request.setXRpcChallengePath
import com.lianyi.paimonsnotebook.common.extension.request.setXRpcClientType
import com.lianyi.paimonsnotebook.common.extension.request.setXRpcToolVersion
import com.lianyi.paimonsnotebook.common.extension.request.setReferer
import com.lianyi.paimonsnotebook.common.util.hoyolab.DynamicSecret
import com.lianyi.paimonsnotebook.common.util.json.JSON
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
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

class GameRecordClient {
    suspend fun getDailyNote(
        user: UserAndUid,
        challenge: String = "",
    ) = getDailyNote(user.userEntity, user.playerUid, challenge)

    suspend fun getDailyNote(
        user: UserEntity,
        role: UserGameRoleData.Role,
        challenge: String = "",
    ) = getDailyNote(user, PlayerUid(role.game_uid, role.region), challenge)

    // GameRecord 统一使用 X4+Gen2（与 Snap.Hutao/PizzaHelperUnited 一致）
    suspend fun getDailyNoteForWidget(
        user: UserEntity
    ) = buildRequest {
        url(ApiEndpoints.CardWidgetDataV2)

        setUser(user, CookieHelper.Type.Ltoken or CookieHelper.Type.Stoken)
        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
    }.getAsJson<DailyNoteWidgetData>()

    private suspend fun getDailyNote(
        user: UserEntity,
        playerUid: PlayerUid,
        challenge: String = "",
    ): ResultData<DailyNoteData> {
        val result = buildRequest {
            url(ApiEndpoints.GameRecordDailyNote(playerUid))

            setUser(user = user, cookieType = CookieHelper.Type.Cookie)
            setReferer("https://webstatic.mihoyo.com/")

            setDynamicSecret(
                saltType = DynamicSecret.SaltType.X4,
                version = DynamicSecret.Version.Gen2
            )

            setXRpcToolVersion()

            if (challenge.isNotBlank() && challenge != "error") {
                setXRpcChallenge(challenge)
                setXRpcChallengeGame()
                setXRpcChallengePath("/game_record/genshin/aapi/widget/v2")
            }

        }.getAsJson<DailyNoteData>()

        return result
    }

    suspend fun getSpiralAbyssData(
        user: UserAndUid,
        scheduleType: String,
    ) = buildRequest {
        url(ApiEndpoints.gameRecordSpiralAbyss(scheduleType = scheduleType, uid = user.playerUid))

        setUser(user.userEntity, CookieHelper.Type.Cookie)
        setReferer("https://webstatic.mihoyo.com/")
        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
        setXRpcToolVersion()
    }.getAsJson<SpiralAbyssData>()

    suspend fun getCharacterList(
        user: UserAndUid,
        sortType: Int = 1
    ) = buildRequest {
        url(ApiEndpoints.gameRecordCharacterList)

        setUser(user.userEntity, CookieHelper.Type.Cookie)
        setReferer("https://webstatic.mihoyo.com/")

        setXRpcClientType(EnvironmentClientType.WEB)
        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
        setXRpcToolVersion()

        buildMap {
            put("server", user.playerUid.region)
            put("role_id", user.playerUid.value)
        }.post(this)

    }.getAsJson<CharacterListData>()

    suspend fun getCharacterDetail(
        user: UserAndUid,
        characterIds: List<Int>
    ) = buildRequest {
        url(ApiEndpoints.gameRecordCharacterDetail)

        setUser(user.userEntity, CookieHelper.Type.Cookie)
        setReferer("https://webstatic.mihoyo.com/")

        setXRpcClientType(EnvironmentClientType.WEB)
        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
        setXRpcToolVersion()

        buildMap {
            put("role_id", user.playerUid.value)
            put("server", user.playerUid.region)
            put("character_ids", characterIds)
        }.post(this)

    }.getAsJson<CharacterDetailData>()

    suspend fun getPlayerInfo(
        user: UserAndUid,
        challenge: String = ""
    ) = buildRequest {
        url(ApiEndpoints.GameRecordIndex(user.playerUid))

        setUser(user.userEntity, CookieHelper.Type.Cookie)
        setReferer("https://webstatic.mihoyo.com/")
        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
        setXRpcToolVersion()

        if (challenge.isNotBlank() && challenge != "error") {
            setXRpcChallenge(challenge)
            setXRpcChallengeGame()
            setXRpcChallengePath("/game_record/genshin/aapi/widget/v2")
        }
    }.getAsJson<Any>()

    suspend fun getRoleBasicInfo(
        user: UserAndUid
    ) = buildRequest {
        url(ApiEndpoints.GameRecordRoleBasicInfo(user.playerUid))

        setUser(user.userEntity, CookieHelper.Type.Cookie)
        setReferer("https://webstatic.mihoyo.com/")
        setDynamicSecret(DynamicSecret.SaltType.X4, DynamicSecret.Version.Gen2)
    }.getAsJson<Any>()

}
