package dev.datlag.tolgee.model.translation

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.language
import dev.datlag.tolgee.common.sprintf
import dev.datlag.tolgee.model.TolgeeKey
import dev.datlag.tolgee.model.TolgeeMessageParams
import dev.datlag.tolgee.model.TolgeeTranslation
import kotlinx.collections.immutable.ImmutableList

internal data class TranslationSprintf(
    override val keys: ImmutableList<TolgeeKey>,
    private var usedLocale: Locale?,
) : TolgeeTranslation {

    override fun localized(key: String, params: TolgeeMessageParams, locale: Locale?): String? {
        val requestedKey = keys.firstOrNull { it.keyName == key } ?: return null

        if (usedLocale == null) {
            usedLocale = locale
        }

        val args = when (params) {
            is TolgeeMessageParams.None -> arrayOf<Any>()
            is TolgeeMessageParams.Indexed -> params.formatArgs.toTypedArray()
            else -> throw IllegalArgumentException("Only indexed or none parameters are supported when using sprintf format.")
        }

        return requestedKey.translationForOrFirst(
            locale?.language?.ifBlank { null }
                ?: this.usedLocale?.language?.ifBlank { null }
        )?.let {
            it.text?.sprintf(*args)
        }
    }

    override fun hasLocale(locale: Locale): Boolean {
        return locale == this.usedLocale || locale.language == this.usedLocale?.language
    }
}