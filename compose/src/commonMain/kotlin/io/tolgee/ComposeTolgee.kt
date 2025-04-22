package io.tolgee

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.intl.Locale
import io.tolgee.common.mapNotNull
import io.tolgee.model.TolgeeMessageParams
import org.jetbrains.compose.resources.StringResource

/**
 * Retrieves a localized string resource using the provided Tolgee translation library and resource key.
 *
 * @param tolgee The Tolgee instance used for fetching translations.
 * @param resource The StringResource containing the key for the desired translation.
 * @return The localized string corresponding to the provided key.
 */
@Composable
fun stringResource(tolgee: Tolgee, resource: StringResource): String {
    return tolgee.translation(
        key = resource.key,
        parameters = TolgeeMessageParams.None
    ).mapNotNull().collectAsState(
        initial = org.jetbrains.compose.resources.stringResource(resource)
    ).value
}

/**
 * Returns a localized string for the given string resource.
 *
 * This function checks if an instance of Tolgee exists. If it does, it retrieves the string
 * translation using Tolgee. Otherwise, it falls back to the default string resource resolution.
 *
 * @param resource The string resource to be localized.
 * @return The localized string corresponding to the given resource.
 */
@Composable
fun stringResource(resource: StringResource): String {
    val tolgee = Tolgee.instance ?: return org.jetbrains.compose.resources.stringResource(resource)
    return stringResource(tolgee, resource)
}

/**
 * Composable function to retrieve a localized string resource using Tolgee.
 *
 * @param tolgee An instance of Tolgee used for fetching translations.
 * @param resource The resource key representing the string to be translated.
 * @param formatArgs Optional arguments to format the translated string.
 * @return The localized string resource.
 */
@Composable
fun stringResource(tolgee: Tolgee, resource: StringResource, vararg formatArgs: Any): String {
    return tolgee.translation(
        key = resource.key,
        parameters = TolgeeMessageParams.Indexed(*formatArgs)
    ).mapNotNull().collectAsState(
        initial = org.jetbrains.compose.resources.stringResource(resource, *formatArgs)
    ).value
}

/**
 * Provides a localized string for the given resource and format arguments.
 *
 * This function first attempts to use the Tolgee instance for retrieving the localized string.
 * If Tolgee is not initialized, it falls back to the Compose resource string method.
 *
 * @param resource The string resource containing the key for localization.
 * @param formatArgs Optional arguments to format the localized string.
 * @return The localized string formatted with the provided arguments.
 */
@Composable
fun stringResource(resource: StringResource, vararg formatArgs: Any): String {
    val tolgee = Tolgee.instance ?: return org.jetbrains.compose.resources.stringResource(resource, *formatArgs)
    return stringResource(tolgee, resource, *formatArgs)
}

/**
 * Sets the locale configuration for the builder and returns the instance for further customization.
 *
 * @param composeLocale The locale to be set for the builder.
 */
fun Tolgee.Config.Builder.locale(composeLocale: Locale) = locale(composeLocale.toLanguageTag())

/**
 * Sets the current locale for the Tolgee instance, updating it in the reactive locale flow.
 *
 * @param composeLocale The locale to be set for translations and related operations.
 */
fun Tolgee.setLocale(composeLocale: Locale) = setLocale(composeLocale.toLanguageTag())