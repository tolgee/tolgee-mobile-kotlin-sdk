package dev.datlag.tolgee

import android.content.Context
import androidx.annotation.StringRes
import dev.datlag.tooling.scopeCatching

data class TolgeeAndroid internal constructor(
    override val config: Config
) : Tolgee(config) {

    fun getString(context: Context, @StringRes key: Int, vararg args: Any): String {
        return getKeyFromRes(context, key)?.let {
            getTranslationFromCache(key = it, args = args)
        } ?: context.getString(key, *args)
    }

    private fun getKeyFromRes(context: Context, @StringRes key: Int): String? {
        return scopeCatching {
            context.resources.getResourceEntryName(key)
        }.getOrNull()?.trim()?.ifBlank { null }
    }
}