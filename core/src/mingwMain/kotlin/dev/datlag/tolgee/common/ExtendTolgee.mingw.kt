package dev.datlag.tolgee.common

import dev.datlag.tolgee.Tolgee
import io.ktor.client.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.plugins.cache.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

internal actual fun createPlatformTolgee(config: Tolgee.Config): Tolgee {
    return Tolgee(config)
}

internal actual val platformHttpClient: HttpClient = HttpClient(WinHttp) {
    followRedirects = true
    install(HttpCache)
}

internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO
