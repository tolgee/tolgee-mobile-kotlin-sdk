package dev.datlag.tolgee

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Composable
fun kodeinStringResource(@StringRes id: Int): String {
    val i18n by localDI().instanceOrNull<I18N>()

    return i18n?.stringResource(id) ?: stringResource(id)
}

@Composable
fun kodeinStringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val i18n by localDI().instanceOrNull<I18N>()

    return i18n?.stringResource(id, *formatArgs) ?: stringResource(id, *formatArgs)
}