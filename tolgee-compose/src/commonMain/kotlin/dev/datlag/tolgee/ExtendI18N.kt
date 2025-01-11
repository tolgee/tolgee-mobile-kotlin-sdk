package dev.datlag.tolgee

import kotlinx.coroutines.CoroutineDispatcher

internal expect val I18N.Companion.networkDispatcher: CoroutineDispatcher
internal expect val I18N.Companion.defaultLocale: I18N.Locale?

fun I18N(block: I18N.Builder.() -> Unit) = I18N.Builder().apply(block).build()