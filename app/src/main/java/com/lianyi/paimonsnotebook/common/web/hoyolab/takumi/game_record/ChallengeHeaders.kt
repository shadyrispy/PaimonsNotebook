package com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record

data class ChallengeHeaders(
    val challengeGame: Int = 2,
    val challengePath: String,
    val page: String = TOOL_VERSION_PAGE
) {
    companion object {
        private const val TOOL_VERSION_PAGE = "v5.0.1-ys_#/ys"
        private const val TOOL_VERSION_PAGE_CALENDAR = "v5.0.1-ys_#/ys/calendar"
        private const val TOOL_VERSION_PAGE_ROLE_ALL = "v5.0.1-ys_#/ys/role/all"
        private const val TOOL_VERSION_PAGE_ROLE_DETAIL = "v5.0.1-ys_#/ys/role/detail"

        // 挑战路径常量
        private const val GAME_RECORD_DAILY_NOTE_PATH = "/game_record/genshin/aapi/widget/v2"
        private const val GAME_RECORD_INDEX_PATH = "/game_record/genshin/aapi/widget/v2"
        private const val GAME_RECORD_SPIRAL_ABYSS_PATH = "/game_record/genshin/aapi/widget/v2"
        private const val GAME_RECORD_CHARACTER_LIST_PATH = "/game_record/genshin/aapi/widget/v2"
        private const val GAME_RECORD_HARD_CHALLENGE_PATH = "/game_record/genshin/aapi/widget/v2"
        private const val GAME_RECORD_ROLE_COMBAT_PATH = "/game_record/genshin/aapi/widget/v2"

        fun createForDailyNote(): ChallengeHeaders = ChallengeHeaders(
            challengePath = GAME_RECORD_DAILY_NOTE_PATH
        )

        fun createForIndex(): ChallengeHeaders = ChallengeHeaders(
            challengePath = GAME_RECORD_INDEX_PATH
        )

        fun createForSpiralAbyss(): ChallengeHeaders = ChallengeHeaders(
            challengePath = GAME_RECORD_SPIRAL_ABYSS_PATH
        )

        fun createForCharacterAll(): ChallengeHeaders = ChallengeHeaders(
            challengePath = GAME_RECORD_CHARACTER_LIST_PATH,
            page = TOOL_VERSION_PAGE_ROLE_ALL
        )

        fun createForCharacterDetail(): ChallengeHeaders = ChallengeHeaders(
            challengePath = GAME_RECORD_CHARACTER_LIST_PATH,
            page = TOOL_VERSION_PAGE_ROLE_DETAIL
        )

        fun createForRoleCombat(): ChallengeHeaders = ChallengeHeaders(
            challengePath = GAME_RECORD_ROLE_COMBAT_PATH
        )

        fun createForHardChallenge(): ChallengeHeaders = ChallengeHeaders(
            challengePath = GAME_RECORD_HARD_CHALLENGE_PATH
        )

        fun createForActCalendar(): ChallengeHeaders = ChallengeHeaders(
            challengePath = GAME_RECORD_HARD_CHALLENGE_PATH,
            page = TOOL_VERSION_PAGE_CALENDAR
        )
    }
}
