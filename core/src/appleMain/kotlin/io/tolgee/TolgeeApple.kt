package io.tolgee

import de.comahe.i18n4k.createLocale
import io.tolgee.common.fromRes
import io.tolgee.model.TolgeeMessageParams
import io.tolgee.common.localizedString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSString
import platform.Foundation.countryCode
import platform.Foundation.languageCode
import platform.Foundation.localizedStringWithFormat
import platform.Foundation.variantCode

/**
 * A data class representing the implementation of the Tolgee localization framework for Apple platforms.
 * This class extends the base Tolgee class and provides methods for accessing translated strings,
 * either as flows or as immediate values.
 */
@ConsistentCopyVisibility
data class TolgeeApple internal constructor(
    override val config: Config
) : Tolgee(config) {

    /**
     * Returns the currently used language for Tolgee.
     * Will be used to retrieve the correct language from the app bundle.
     */
    private val bundleRes: String?
        get() = getLocale().getLanguage().ifBlank { null }

    /**
     * Provides a flow of localized strings based on the given key, default value, and optional table name.
     *
     * The translation process considers the current locale and emits updates as the locale or translated strings change.
     *
     * @param key The key used to fetch the localized string.
     * @param default The default string to use if no translation is found, or null if no default is provided.
     * @param table The name of the table where the key is searched for, or null to use the default table.
     * @return A flow that emits localized strings corresponding to the given key.
     */
    fun tFlow(key: String, default: String?, table: String? = null): Flow<String> = flow {
        emit(
            getLocalizedStringFromBundle(
                bundleRes,
                key,
                default,
                table
            ) ?: default?.ifBlank { null }
        )

        emitAll(tFlow(key, TolgeeMessageParams.None))
    }.filterNotNull()

    /**
     * Provides a flow of localized string representations for a given key, allowing localization updates
     * based on dynamic arguments and optional table-specific localization contexts.
     *
     * The method emits localized strings based on the provided key and optional arguments. It handles
     * both initial translation retrieval and updates when localization parameters or configurations change.
     *
     * @param key The key representing the string to be localized.
     * @param default An optional default string to use if no localization is found for the specified key.
     * @param table An optional table or namespace for scoping the localization lookup.
     * @param args A variable number of arguments for formatting the localized string.
     * @return A flow that emits localized and formatted string values corresponding to the given key and arguments.
     */
    fun tFlow(key: String, default: String?, table: String? = null, vararg args: Any): Flow<String> = flow {
        emit(getLocalizedStringFromBundleFormatted(bundleRes, key, default, table, *args))

        emitAll(tFlow(key, TolgeeMessageParams.Indexed(*args)))
    }.filterNotNull()

    /**
     * Retrieves the translation for a given key immediately.
     * If no translation is found, falls back to the provided default value or bundle localization.
     *
     * @param key The key to look up for translation.
     * @param default The default value to return if no translation is found. Can be null.
     * @param table The optional localization table where the key is searched. Can be null.
     * @return The localized string if found, or the default value if provided. Returns null if none is available.
     */
    fun t(key: String, default: String?, table: String? = null): String? {
        return t(key) ?: getLocalizedStringFromBundle(
            bundleRes,
            key,
            default,
            table
        ) ?: default?.ifBlank { null }
    }

    /**
     * Retrieves an immediate translation string for the given key with optional parameters and a fallback.
     *
     * If the key is not found or no translations are loaded, it falls back
     * to a localized string obtained from a resource bundle or returns the provided default value.
     *
     * @param key The key to identify the desired translation string.
     * @param default The fallback string to be used if the key is not found or no translation is available. Can be null.
     * @param table The lookup table name for the translation, if applicable. Defaults to null.
     * @param args Optional arguments to be used for formatting the translation string.
     * @return The translated and optionally formatted string, or null if no translation is found and no default is provided.
     */
    fun t(key: String, default: String?, table: String? = null, vararg args: Any): String? {
        return t(key, TolgeeMessageParams.Indexed(*args))
            ?: getLocalizedStringFromBundleFormatted(bundleRes, key, default, table, *args)
    }

    /**
     * Sets the current locale for the Tolgee instance, updating it in the reactive locale flow.
     *
     * @param nsLocale The locale to be set for translations and related operations.
     */
    fun setLocale(nsLocale: NSLocale) = setLocale(
        createLocale(
            language = nsLocale.languageCode,
            country = nsLocale.countryCode?.ifBlank { null },
            variant = nsLocale.variantCode?.ifBlank { null }
        )
    )

    /**
     * Contains utility methods for fetching localized strings from the main resource bundle
     * or a fallback base resource bundle.
     */
    companion object {
        /**
         * Retrieves a localized string for a given key from the main application bundle or a base resource bundle.
         * If the localized string is not found, the method returns a default string or null.
         *
         * @param res The resource specification, usually a language code.
         * @param key The key for the localized string.
         * @param default The default string to return if the localized string is not found. If this value is blank, it will be treated as null.
         * @param table The table in which to search for the key. This can be null to use the default table.
         * @return The localized string if found, the default string if provided, or null if no match exists.
         */
        fun getLocalizedStringFromBundle(
            res: String?,
            key: String,
            default: String?,
            table: String?
        ): String? {
            return NSBundle.fromRes(res, "lproj")?.localizedString(key, default, table)
                ?: NSBundle.mainBundle.localizedString(key, default, table)
                ?: NSBundle.fromRes("Base", "lproj")?.localizedString(key, default, table)
        }

        /**
         * Retrieves a localized and formatted string from a specified bundle.
         * Uses the provided `key`, `default` value, `table`, and any additional arguments for string formatting.
         * If no localized string is found, it falls back to the `default` value.
         *
         * @param res The resource specification, usually a language code.
         * @param key The key associated with the localized string resource.
         * @param default The default string to use if the localized string is not found. Can be null.
         * @param table The name of the table (e.g., file) in the bundle containing the localization data. Can be null.
         * @param args The arguments to format the resulting string. Supports up to 10 arguments.
         * @return The localized and formatted string, or null if no string could be resolved.
         */
        fun getLocalizedStringFromBundleFormatted(
            res: String?,
            key: String,
            default: String?,
            table: String?,
            vararg args: Any
        ): String? {
            return (getLocalizedStringFromBundle(res, key, default, table) ?: default?.ifBlank { null })?.let { format ->
                when (args.size) {
                    0 -> NSString.localizedStringWithFormat(format)
                    1 -> NSString.localizedStringWithFormat(format, args[0])
                    2 -> NSString.localizedStringWithFormat(format, args[0], args[1])
                    3 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2])
                    4 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3])
                    5 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4])
                    6 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5])
                    7 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5], args[6])
                    8 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7])
                    9 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8])
                    else -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9])
                }
            }
        }
    }

}
