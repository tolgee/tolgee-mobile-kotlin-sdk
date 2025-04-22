package io.tolgee.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Transforms the elements emitted by this flow, filtering out any null values.
 *
 * This operator skips any `null` values emitted by the original flow and emits only the non-null values.
 *
 * @return A new flow that emits only non-null values from the original flow.
 */
fun <T> Flow<T>.mapNotNull(): Flow<T & Any> = this.mapNotNull { value -> value }