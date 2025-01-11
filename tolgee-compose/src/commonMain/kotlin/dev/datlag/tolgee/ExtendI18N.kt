package dev.datlag.tolgee

import kotlinx.coroutines.CoroutineDispatcher

internal expect val I18N.Companion.networkDispatcher: CoroutineDispatcher

fun I18N(block: I18N.Builder.() -> Unit) = I18N.Builder().apply(block).build()