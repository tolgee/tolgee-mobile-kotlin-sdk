package dev.datlag.tolgee

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.datlag.tolgee.I18N.ContentDelivery
import dev.datlag.tolgee.format.sprintf
import dev.datlag.tooling.async.scopeCatching
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val I18N.Companion.networkDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

/**
 * Retrieves the translation from cache or resources by default and updates if new translations are available.
 *
 * @param id the [StringRes] used by default.
 * @return [String] from [ContentDelivery] or default [StringRes].
 */
@Composable
fun I18N.stringResource(@StringRes id: Int): String {
    val resources = LocalContext.current.resources
    val key = remember(id) {
        scopeCatching {
            resources.getResourceEntryName(id)
        }.getOrNull()
    }

    return produceState<String>(
        key?.let(::getTranslation) ?: androidx.compose.ui.res.stringResource(id)
    ) {
        value = key?.let { translation(it) } ?: key?.let(::getTranslation) ?: value
    }.value
}

/**
 * Retrieves the translation from cache or resources by default and updates if new translations are available.
 *
 * @param id the [StringRes] used by default.
 * @param formatArgs arguments for formatting. (Mostly Java-format and C-sprintf compatible)
 * @return [String] from [ContentDelivery] or default [StringRes].
 */
@Composable
fun I18N.stringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val resources = LocalContext.current.resources
    val key = remember(id) {
        scopeCatching {
            resources.getResourceEntryName(id)
        }.getOrNull()
    }

    return produceState<String>(
        scopeCatching {
            key?.let(::getTranslation)?.sprintf(*formatArgs)
        }.getOrNull() ?: androidx.compose.ui.res.stringResource(id, *formatArgs)
    ) {
        value = suspendCatching {
            key?.let { translation(it) }?.sprintf(*formatArgs)
        }.getOrNull() ?: suspendCatching {
            key?.let(::getTranslation)?.sprintf(*formatArgs)
        }.getOrNull() ?: value
    }.value
}