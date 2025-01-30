package dev.datlag.tolgee.common

import dev.datlag.tolgee.Tolgee
import dev.datlag.tolgee.TolgeeApple
import io.ktor.client.*
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

internal actual val platformHttpClient: HttpClient = HttpClient(Darwin) {
    followRedirects = true
}

internal actual fun createPlatformTolgee(config: Tolgee.Config): Tolgee {
    return TolgeeApple(config)
}

internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO