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
            computeEntryName(resources, resId)?.let { return it }
        }
        return accessEntryName(resources, resId) ?: scopeCatching {
            resources.getResourceEntryName(resId)
        }.getOrNull()?.ifBlank { null }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun computeEntryName(resources: Resources, @AnyRes resId: Int): String? {
        return scopeCatching {
            cache.computeIfAbsent(resId) {
                resources.getResourceEntryName(resId)
            }
        }.getOrNull()?.ifBlank { null }
    }

    private fun accessEntryName(resources: Resources, @AnyRes resId: Int): String? {
        cache[resId]?.ifBlank { null }?.let { return it }

        val name = scopeCatching {
            resources.getResourceEntryName(resId)
        }.getOrNull()?.ifBlank { null } ?: return null

        return cache.putIfAbsent(resId, name)?.ifBlank { null } ?: name
    }
}