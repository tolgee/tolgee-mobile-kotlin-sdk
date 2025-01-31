package dev.datlag.tolgee

import android.content.Context
import androidx.annotation.StringRes
import dev.datlag.tooling.scopeCatching

data class TolgeeAndroid internal constructor(
    override val config: Config
) : Tolgee(config) {

    suspend fun getString(context: Context, @StringRes key: Int, vararg args: Any): String {
        return getKeyFromRes(context, key)?.let {
            getTranslation(key = it, args = args)
        } ?: getStringFromCache(context, key, *args)
    }

    fun getStringFromCache(context: Context, @StringRes key: Int, vararg args: Any): String {
        return getKeyFromRes(context, key)?.let {
            getTranslationFromCache(key = it, args = args)
        } ?: context.getString(key, *args)
    }

    companion object {
        internal fun getKeyFromRes(context: Context, @StringRes key: Int): String? {
            return scopeCatching {
                context.resources.getResourceEntryName(key)
            }.getOrNull()?.trim()?.ifBlank { null }
        }
    }
}