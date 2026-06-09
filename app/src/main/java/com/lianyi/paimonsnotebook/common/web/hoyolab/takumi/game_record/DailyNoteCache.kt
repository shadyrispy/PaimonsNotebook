package com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record.daily_note

object DailyNoteCache {
    private const val CACHE_DURATION_MS = 15 * 60 * 1000L

    private val cache = mutableMapOf<String, CacheEntry>()

    data class CacheEntry(
        val data: DailyNoteData,
        val timestamp: Long
    )

    fun get(key: String): DailyNoteData? {
        val entry = cache[key]
        if (entry != null && System.currentTimeMillis() - entry.timestamp < CACHE_DURATION_MS) {
            return entry.data
        }
        cache.remove(key)
        return null
    }

    fun put(key: String, data: DailyNoteData) {
        cache[key] = CacheEntry(data, System.currentTimeMillis())
    }

    fun remove(key: String) {
        cache.remove(key)
    }

    fun clear() {
        cache.clear()
    }

    fun contains(key: String): Boolean {
        return get(key) != null
    }
}