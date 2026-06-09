package com.lianyi.paimonsnotebook.common.extension.request

import com.lianyi.paimonsnotebook.common.util.hoyolab.DynamicSecret
import okhttp3.Request
import java.net.URLDecoder

fun Request.Builder.setDynamicSecret(
    saltType: DynamicSecret.SaltType,
    version: DynamicSecret.Version = DynamicSecret.Version.Gen2,
    includeChars: Boolean = false,
) {
    val built = this.build()
    val url = built.url.toString()
    val urlParts = url.split("?")

    val query = if (urlParts.size > 1) {
        URLDecoder.decode(urlParts.last(), "UTF-8")
            .split("&")
            .sortedBy { it }
            .joinToString("&")
    } else {
        ""
    }

    val body = built.body?.let {
        val buffer = okio.Buffer()
        it.writeTo(buffer)
        buffer.readUtf8()
    } ?: ""

    val b = if (saltType == DynamicSecret.SaltType.PROD) "{}" else body

    this.addHeader(
        "DS",
        DynamicSecret.getDynamicSecret(version, saltType, includeChars, query, b)
    )
}
