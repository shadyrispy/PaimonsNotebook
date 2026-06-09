package com.lianyi.paimonsnotebook.common.web.hoyolab.cookie

import com.lianyi.paimonsnotebook.common.web.hoyolab.cookie.CookieHelper.Keys

fun Cookie.tryGetLoginTicket(): Cookie? {
    return this.tryGetValuesToCookie(listOf(Keys.LoginTicket, Keys.LoginUID))
}

fun Cookie.tryGetCookieToken(): Cookie? {
    return this.tryGetValuesToCookie(listOf(Keys.AccountID, Keys.CookieToken))
}

fun Cookie.tryGetSToken(): Cookie? {
    return this.tryGetValuesToCookie(listOf(Keys.Mid, Keys.SToken, Keys.STuid))
}

fun Cookie.tryGetLToken(): Cookie? {
    return this.tryGetValuesToCookie(listOf(Keys.LToken, Keys.LTuid))
}

fun Cookie.tryGetDeviceFp(): String? {
    return this[Keys.DEVICEFP]
}

private fun Cookie.tryGetValuesToCookie(keys: List<String>): Cookie? {
    val map = mutableMapOf<String, String>()
    keys.forEach { key ->
        val value = this[key] ?: return null
        map[key] = value
    }
    return Cookie().also {
        map.forEach { (k, v) -> it[k] = v }
    }
}
