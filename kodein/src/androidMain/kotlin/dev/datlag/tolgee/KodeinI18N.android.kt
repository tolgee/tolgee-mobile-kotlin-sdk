package dev.datlag.tolgee

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.datlag.tolgee.I18N.ContentDelivery
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

/**
 * Uses the [I18N] object found in your DI container and falls back to resources if non found.
 *
 * @param id the [StringRes] used by default.
 * @see I18N.stringResource
 * @return [String] from [I18N.ContentDelivery] or [StringRes]
 */
@Composable
fun kodeinStringResource(@StringRes id: Int): String {
    val i18n by localDI().instanceOrNull<I18N>()

    return i18n?.stringResource(id) ?: stringResource(id)
}

/**
 * Uses the [I18N] object found in your DI container and falls back to resources if non found.
 *
 * @param id the [StringRes] used by default.
 * @param formatArgs arguments for formatting. (Mostly Java-format and C-sprintf compatible)
 * @see I18N.stringResource
 * @return [String] from [ContentDelivery] or default [StringRes].
 */
@Composable
fun kodeinStringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val i18n by localDI().instanceOrNull<I18N>()

    return i18n?.stringResource(id, *formatArgs) ?: stringResource(id, *formatArgs)
}