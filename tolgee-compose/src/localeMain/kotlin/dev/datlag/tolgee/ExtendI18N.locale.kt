package dev.datlag.tolgee

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val I18N.Companion.networkDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

fun main() {
    I18N {
        contentDelivery("")
    }
}