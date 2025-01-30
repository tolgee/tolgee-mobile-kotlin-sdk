package dev.datlag.tolgee.common

import dev.datlag.tolgee.Tolgee
import io.ktor.client.*
import kotlin.coroutines.CoroutineContext

internal expect fun createPlatformTolgee(config: Tolgee.Config): Tolgee

internal expect val platformHttpClient: HttpClient
internal expect val platformNetworkContext: CoroutineContext