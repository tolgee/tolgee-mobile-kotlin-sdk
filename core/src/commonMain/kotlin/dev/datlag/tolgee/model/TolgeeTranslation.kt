package dev.datlag.tolgee.model

import de.comahe.i18n4k.Locale
import dev.datlag.tolgee.Tolgee
import dev.datlag.tolgee.model.translation.TranslationEmpty
import dev.datlag.tolgee.model.translation.TranslationICU
import dev.datlag.tolgee.model.translation.TranslationSprintf
import kotlinx.collections.immutable.ImmutableList

internal interface TolgeeTranslation {
    val keys: ImmutableList<TolgeeKey>

    fun localized(key: String, params: TolgeeMessageParams, locale: Locale?): String?
    fun hasLocale(locale: Locale): Boolean

    companion object {
        internal operator fun invoke(
            keys: ImmutableList<TolgeeKey>,
            formatter: Tolgee.Formatter,
            usedLocale: Locale?,
        ): TolgeeTranslation = when (formatter) {
            is Tolgee.Formatter.ICU -> TranslationICU(keys)
            is Tolgee.Formatter.Sprintf -> TranslationSprintf(keys, usedLocale)
            else -> TranslationEmpty
        }
    }
}