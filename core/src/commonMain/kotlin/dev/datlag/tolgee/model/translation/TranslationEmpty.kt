package dev.datlag.tolgee.model.translation

import de.comahe.i18n4k.Locale
import dev.datlag.tolgee.model.TolgeeKey
import dev.datlag.tolgee.model.TolgeeMessageParams
import dev.datlag.tolgee.model.TolgeeTranslation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal object TranslationEmpty : TolgeeTranslation {
    override val keys: ImmutableList<TolgeeKey> = persistentListOf()

    override fun localized(key: String, params: TolgeeMessageParams, locale: Locale?): String? {
        return null
    }

    override fun hasLocale(locale: Locale): Boolean {
        return false
    }
}
