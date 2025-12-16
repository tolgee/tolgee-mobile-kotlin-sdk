package io.tolgee.model

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.forLocaleTag
import kotlinx.serialization.Serializable

/**
 * Represents metadata about available translations in the project.
 *
 * This metadata is fetched from the CDN and contains information about
 * which locales are available for translation fallback logic.
 *
 * @property locales List of locale tags (e.g., ["en", "en-US", "de", "fr-FR"])
 *                   that are available in this Tolgee project. If null, the
 *                   fallback mechanism is disabled and only exact locale matches
 *                   will be used.
 */
@Serializable
internal data class TolgeeManifest(
    val locales: List<String>?
) {
    /**
     * Converts the string locale tags to Locale objects.
     * Lazily computed and cached for performance.
     * Returns null if locales is null (fallback disabled).
     */
    val availableLocales: List<Locale>? by lazy {
        locales?.map { forLocaleTag(it) }
    }
}
