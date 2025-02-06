package dev.datlag.tolgee.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

fun <T> Flow<T>.mapNotNull(): Flow<T & Any> = this.mapNotNull { value -> value }