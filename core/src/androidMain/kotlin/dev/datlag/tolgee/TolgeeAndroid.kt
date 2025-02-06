package dev.datlag.tolgee

import android.content.Context
import androidx.annotation.StringRes
import dev.datlag.tolgee.common.mapNotNull
import dev.datlag.tolgee.model.TolgeeMessageParams
import dev.datlag.tooling.scopeCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

data class TolgeeAndroid internal constructor(
    override val config: Config
) : Tolgee(config) {

    fun translation(context: Context, @StringRes id: Int): Flow<String> = flow {
        emit(context.getString(id))

        getKeyFromStringResource(context, id)?.let { key ->
            emitAll(translation(key, TolgeeMessageParams.None).mapNotNull())
        }
    }

    fun translation(context: Context, @StringRes id: Int, vararg formatArgs: Any): Flow<String> = flow {
        emit(context.getString(id, *formatArgs))

        getKeyFromStringResource(context, id)?.let { key ->
            emitAll(translation(key, TolgeeMessageParams.Indexed(*formatArgs)))
        }
    }

    fun instant(context: Context, @StringRes id: Int): String {
        return getKeyFromStringResource(context, id)?.let { key ->
            instant(key)
        } ?: context.getString(id)
    }

    fun instant(context: Context, @StringRes id: Int, vararg formatArgs: Any): String {
        return getKeyFromStringResource(context, id)?.let { key ->
            instant(key, TolgeeMessageParams.Indexed(*formatArgs))
        } ?: context.getString(id, *formatArgs)
    }

    companion object {
        fun getKeyFromStringResource(context: Context, @StringRes id: Int): String? {
            return scopeCatching {
                context.resources.getResourceEntryName(id)
            }.getOrNull()?.trim()?.ifBlank { null }
        }
    }
}