package dev.datlag.tolgee

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val I18N.Companion.networkDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO