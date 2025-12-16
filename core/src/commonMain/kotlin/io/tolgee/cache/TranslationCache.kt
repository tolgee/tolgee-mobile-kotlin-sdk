package io.tolgee.cache

import io.tolgee.model.TolgeeTranslation

/**
 * Thread-safe LRU (Least Recently Used) cache for translation objects.
 *
 * This cache stores translations by locale tag and automatically evicts
 * the least recently used entry when the maximum size is reached (if a limit is set).
 *
 * @property maxSize Maximum number of translations to cache.
 *                   - `null`: unlimited cache (no eviction)
 *                   - `>= 1`: limited cache with LRU eviction
 */
internal class TranslationCache(
    private val maxSize: Int?
) {
    init {
        require(maxSize == null || maxSize >= 1) {
            "maxSize must be null (unlimited) or at least 1, but was $maxSize"
        }
    }

    /**
     * Internal cache entry that wraps a translation with its access counter.
     * Higher counter values indicate more recent access.
     */
    private data class CacheEntry(
        val translation: TolgeeTranslation,
        var accessCounter: Long
    )

    private val cache = mutableMapOf<String, CacheEntry>()
    private var globalAccessCounter: Long = 0

    /**
     * Retrieves a translation from the cache and updates its access counter.
     *
     * @param localeTag The locale tag (e.g., "en-US", "fr")
     * @return The cached translation, or null if not found
     */
    fun get(localeTag: String): TolgeeTranslation? {
        val entry = cache[localeTag] ?: return null
        // Update access counter to mark as recently used
        entry.accessCounter = ++globalAccessCounter
        return entry.translation
    }

    /**
     * Adds a translation to the cache, evicting the LRU entry if needed.
     *
     * If the locale is already cached, this updates its access counter.
     * If the cache is full (and has a size limit), the least recently used entry is evicted.
     *
     * @param localeTag The locale tag (e.g., "en-US", "fr")
     * @param translation The translation object to cache
     */
    fun put(localeTag: String, translation: TolgeeTranslation) {
        val currentCounter = ++globalAccessCounter

        // Update existing entry
        cache[localeTag]?.let { entry ->
            entry.accessCounter = currentCounter
            return
        }

        // Evict LRU if at capacity (only if maxSize is set)
        if (maxSize != null && cache.size >= maxSize) {
            evictLRU()
        }

        // Add new entry
        cache[localeTag] = CacheEntry(
            translation = translation,
            accessCounter = currentCounter
        )
    }

    /**
     * Checks if a locale is currently cached.
     *
     * @param localeTag The locale tag to check
     * @return true if the locale is cached, false otherwise
     */
    fun contains(localeTag: String): Boolean = cache.containsKey(localeTag)

    /**
     * Clears all cached translations.
     */
    fun clear() = cache.clear()

    /**
     * Returns the current number of cached translations.
     *
     * @return The number of entries in the cache
     */
    fun size(): Int = cache.size

    /**
     * Evicts the least recently used entry from the cache.
     *
     * This method is called internally when the cache reaches its maximum size.
     * The entry with the lowest access counter is removed.
     */
    private fun evictLRU() {
        if (cache.isEmpty()) return
        val lruKey = cache.minByOrNull { it.value.accessCounter }?.key
        lruKey?.let { cache.remove(it) }
    }
}
