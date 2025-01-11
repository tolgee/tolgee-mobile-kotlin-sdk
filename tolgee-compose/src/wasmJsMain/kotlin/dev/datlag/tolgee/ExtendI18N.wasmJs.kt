package dev.datlag.tolgee

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val I18N.Companion.networkDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default

internal actual val I18N.Companion.defaultLocale: I18N.Locale?
    get() = null