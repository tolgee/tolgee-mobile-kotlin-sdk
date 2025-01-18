package dev.datlag.tolgee

import com.vanniktech.locale.Country
import com.vanniktech.locale.Language
import com.vanniktech.locale.Locale
import com.vanniktech.locale.Locales
import dev.datlag.tooling.Platform
import kotlin.jvm.JvmOverloads

internal actual val I18N.Companion.defaultLocale: I18N.Locale?
    get() = I18N.Locale.Builder().locale().build()

private fun Locale.Companion.default(): Locale {
    return fromOrNull(Locales.currentLocaleString()) ?: run {
        val all = Locales.currentLocaleStrings()

        for (l in all) {
            return fromOrNull(l) ?: continue
        }

        return fromOrNull(Locales.currentLocaleString()) ?: Locale(
            language = Language.ENGLISH,
            territory = Country.USA
        )
    }
}

private fun Locale.localized(): String = when {
    Platform.isIOS || Platform.isMacOS || Platform.isTVOS || Platform.isWatchOS -> this.appleAppStoreLocale()?.toString()
    Platform.isAndroid -> this.googlePlayStoreLocale()?.toString()
    else -> null
}?.ifBlank { null } ?: this.language.code

@JvmOverloads
fun I18N.Locale.Builder.locale(locale: Locale = Locale.default()) = apply {
    localization(locale.localized())
    languageCode(locale.language.code)
    (locale.territory?.code?.ifBlank { null } ?: locale.territory?.code3?.ifBlank { null })?.let { regionCode(it) }
}

@JvmOverloads
fun I18N.Builder.locale(locale: Locale = Locale.default()) = apply {
    locale { locale(locale) }
}