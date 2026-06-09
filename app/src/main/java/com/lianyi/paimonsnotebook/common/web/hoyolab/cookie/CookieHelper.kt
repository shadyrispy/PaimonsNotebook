package com.lianyi.paimonsnotebook.common.web.hoyolab.cookie

object CookieHelper {

    object Keys {
        const val CookieToken = "cookie_token"
        const val AccountID = "account_id"
        const val LoginTicket = "login_ticket"
        const val LoginUID = "login_uid"
        const val Mid = "mid"

        const val LToken = "ltoken"
        const val LTuid = "ltuid"
        const val SToken = "stoken"
        const val STuid = "stuid"

        const val DEVICEFP = "DEVICEFP"
    }

    object Type {
        const val None = 0x0000
        const val CookieToken = 0x0001
        const val LToken = 0x0002
        const val SToken = 0x0004
        const val Cookie = CookieToken or LToken
        const val All = CookieToken or LToken or SToken
    }

    //连接字符串并转换为cookie
    fun concatStringToCookie(vararg values: Pair<String, String>): Cookie {
        val cookie = Cookie()
        values.forEach { pair ->
            cookie[pair.first] = pair.second
        }
        return cookie
    }

    fun stringToCookieMap(cookieString: String?): Map<String, String> {
        val cookies = mutableMapOf<String, String>()
        cookieString?.split(";")?.toList()
            ?.forEach { cookie ->
                val index = cookie.indexOfFirst { it == '=' }
                if (index != -1) {
                    val key = cookie.take(index).trim()
                    val value = cookie.takeLast(cookie.length - index - 1)
                    if (key.isNotBlank()) {
                        cookies[key] = value
                    }
                }
            }
        return cookies
    }

}