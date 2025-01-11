package dev.datlag.tolgee

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.datlag.tooling.async.scopeCatching

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