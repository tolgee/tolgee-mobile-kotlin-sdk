package dev.datlag.tolgee

import androidx.compose.runtime.Composable
import dev.datlag.tolgee.I18N.ContentDelivery
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

/**
 * Uses the [I18N] object found in your DI container and falls back to resources if non found.
 *
 * @param res the [StringResource] used by default.
 * @see I18N.stringResource
 * @return [String] from [I18N.ContentDelivery] or [StringResource]
 */
@Composable
fun kodeinStringResource(res: StringResource): String {
    val i18n by localDI().instanceOrNull<I18N>()

    return i18n?.stringResource(res) ?: stringResource(res)
}

/**
 * Uses the [I18N] object found in your DI container and falls back to resources if non found.
 *
 * @param res the [StringResource] used by default.
 * @param formatArgs arguments for formatting. (Mostly Java-format and C-sprintf compatible)
 * @see I18N.stringResource
 * @return [String] from [ContentDelivery] or default [StringResource].
 */
@Composable
fun kodeinStringResource(res: StringResource, vararg formatArgs: Any): String {
    val i18n by localDI().instanceOrNull<I18N>()

    return i18n?.stringResource(res) ?: stringResource(res, *formatArgs)
}