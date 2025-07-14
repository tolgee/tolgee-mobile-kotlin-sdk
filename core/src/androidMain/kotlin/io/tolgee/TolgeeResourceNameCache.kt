package io.tolgee

import android.content.res.Resources
import android.os.Build
import androidx.annotation.AnyRes
import androidx.annotation.RequiresApi
import dev.datlag.tooling.scopeCatching
import java.util.concurrent.ConcurrentHashMap

internal object TolgeeResourceNameCache {

    private val cache = ConcurrentHashMap<Int, String>()

    fun getEntryName(resources: Resources, @AnyRes resId: Int): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return computeEntryName(resources, resId)
        }
        return computeEntryNameFallback(resources, resId)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun computeEntryName(resources: Resources, @AnyRes resId: Int): String? {
        return scopeCatching {
            cache.computeIfAbsent(resId) {
                resources.getResourceEntryName(resId)
            }
        }.getOrNull()?.ifBlank { null }
    }

    private fun computeEntryNameFallback(resources: Resources, @AnyRes resId: Int): String? {
        // computeIfAbsent not available for API < N (24)
        return scopeCatching {
            cache.computeIfAbsentFallback(resId) {
                resources.getResourceEntryName(it)
            }
        }.getOrNull()?.ifBlank { null }
    }

    private fun <K, V> ConcurrentHashMap<in K, V>.computeIfAbsentFallback(key: K, mappingFunction: (K) -> V): V {
        get(key)?.let { return it }
        val name = mappingFunction(key)
        return putIfAbsent(key, name) ?: name
    }
}