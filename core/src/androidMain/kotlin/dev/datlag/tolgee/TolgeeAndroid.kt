package dev.datlag.tolgee

import android.content.Context
import androidx.annotation.StringRes
import dev.datlag.tooling.scopeCatching

data class TolgeeAndroid internal constructor(
    override val config: Config
) : Tolgee(config) {

    suspend fun getString(context: Context, @StringRes id: Int, vararg formatArgs: Any): String {
        return getKeyFromRes(context, id)?.let {
            translation(key = it, formatArgs = formatArgs)
        } ?: getStringFromCache(context, id, *formatArgs)
    }

    fun getStringFromCache(context: Context, @StringRes id: Int, vararg formatArgs: Any): String {
        return getKeyFromRes(context, id)?.let {
            translationFromCache(key = it, formatArgs = formatArgs)
        } ?: context.getString(id, *formatArgs)
    }

    companion object {
        fun getKeyFromRes(context: Context, @StringRes id: Int): String? {
            return scopeCatching {
                context.resources.getResourceEntryName(id)
            }.getOrNull()?.trim()?.ifBlank { null }
        }
    }
}