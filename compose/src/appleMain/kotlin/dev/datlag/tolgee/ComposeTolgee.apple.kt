package dev.datlag.tolgee

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dev.datlag.tolgee.model.TolgeeMessageParams

/**
 * Provides a localized string based on the given key and optional default, using the specified Tolgee instance.
 *
 * @param tolgee An instance of the Tolgee library used for retrieving translations.
 * @param key The key used to look up the localized string.
 * @param default The default string returned if no translation is found. Can be null.
 * @param table An optional parameter specifying the translation table or namespace. Defaults to null.
 * @return The localized string corresponding to the provided key, or a fallback based on the default and table parameters.
 */
@Composable
fun stringResource(tolgee: Tolgee, key: String, default: String?, table: String? = null): String {
    val translationFlow = (tolgee as? TolgeeApple)?.translation(key, default, table) ?: tolgee.translation(key)
    val res = tolgee.getLocale().getLanguage().ifBlank { null }

    return translationFlow.collectAsState(
        initial = TolgeeApple.getLocalizedStringFromBundle(res, key, default, table) ?: default?.ifBlank { null } ?: ""
    ).value
}

/**
 * Retrieves a localized string based on the provided key, default value, and optional table name.
 *
 * If a localization instance is available, it fetches the translation using it.
 * Otherwise, it attempts to fetch the localized string from the application bundle or returns the default string.
 *
 * @param key The key used to identify the localized string.
 * @param default The default string to use if no localized string is found. If blank, it will be treated as null.
 * @param table The optional table or namespace for looking up the key. Defaults to null if not specified.
 * @return The localized string if available, or the fallback string based on the provided default or key.
 */
@Composable
fun stringResource(key: String, default: String?, table: String? = null): String {
    val instance = Tolgee.instance ?: return run {
        val res = Tolgee.systemLocale.getLanguage().ifBlank { null }

        TolgeeApple.getLocalizedStringFromBundle(res, key, default, table) ?: default?.ifBlank { null } ?: ""
    }

    return stringResource(instance, key, default, table)
}

/**
 * Retrieves a localized string resource using the provided translation key, default value, optional table,
 * and optional arguments. The translation is fetched from the specified Tolgee instance and updates
 * dynamically using a state-based approach.
 *
 * @param tolgee The Tolgee instance responsible for fetching translations.
 * @param key The translation key used to identify the desired string resource.
 * @param default The default string to use if no translation is found.
 * @param table The optional table name where the key is located.
 * @param args Optional arguments for formatting the translated string.
 * @return A string value representing the localized and formatted resource.
 */
@Composable
fun stringResource(tolgee: Tolgee, key: String, default: String?, table: String? = null, vararg args: Any): String {
    val translationFlow = (tolgee as? TolgeeApple)?.translation(key, default, table, *args)
        ?: tolgee.translation(key, TolgeeMessageParams.Indexed(*args))
    val res = tolgee.getLocale().getLanguage().ifBlank { null }

    return translationFlow.collectAsState(
        initial = TolgeeApple.getLocalizedStringFromBundleFormatted(res, key, default, table, *args) ?: ""
    ).value
}

/**
 * Retrieves a localized and formatted string based on the specified key, default value, table, and arguments.
 * The method ensures the use of a `Tolgee` instance if available, otherwise falls back to bundle-based localization.
 *
 * @param key The key identifying the localized string resource.
 * @param default The default string to use if no localization is found. Can be null.
 * @param table The optional table from which the localization data is fetched. Can be null.
 * @param args The arguments to format the localized string.
 * @return The localized and formatted string; returns an empty string if no localization is found and no default value is provided.
 */
@Composable
fun stringResource(key: String, default: String?, table: String? = null, vararg args: Any): String {
    val instance = Tolgee.instance ?: return run {
        val res = Tolgee.systemLocale.getLanguage().ifBlank { null }

        TolgeeApple.getLocalizedStringFromBundleFormatted(res, key, default, table, *args) ?: ""
    }

    return stringResource(instance, key, default, table, *args)
}