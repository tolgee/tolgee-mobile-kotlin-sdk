package dev.datlag.tolgee

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Composable
fun kodeinStringResource(res: StringResource): String {
    val i18n by localDI().instanceOrNull<I18N>()

    return i18n?.stringResource(res) ?: stringResource(res)
}

@Composable
fun kodeinStringResource(res: StringResource, vararg formatArgs: Any): String {
    val i18n by localDI().instanceOrNull<I18N>()

    return i18n?.stringResource(res) ?: stringResource(res, *formatArgs)
}