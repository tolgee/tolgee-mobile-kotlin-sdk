package dev.datlag.tolgee.common

import dev.datlag.tolgee.Tolgee
import io.ktor.client.*
import io.ktor.client.engine.java.Java
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal actual fun createPlatformTolgee(config: Tolgee.Config): Tolgee {
    return Tolgee(config)
}

internal actual val platformHttpClient: HttpClient = HttpClient(Java) {
    followRedirects = true
}
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO
