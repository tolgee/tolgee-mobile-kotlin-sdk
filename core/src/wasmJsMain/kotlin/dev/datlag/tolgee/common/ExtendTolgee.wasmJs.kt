package dev.datlag.tolgee.common

import dev.datlag.tolgee.Tolgee
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.cache.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Creates and returns a platform-specific instance of Tolgee.
 *
 * @param config The configuration object required to initialize the Tolgee instance.
 * @return A newly created instance of Tolgee initialized with the provided configuration.
 */
internal actual fun createPlatformTolgee(config: Tolgee.Config): PlatformTolgee {
    return PlatformTolgee(config)
}

/**
 * A platform-specific instance of [HttpClient] configured for JavaScript environments.
 *
 * - Uses the `Js` engine to interact with network resources in JavaScript runtime.
 * - Enables automatic following of HTTP redirects through `followRedirects = true`.
 * - Installs an HTTP cache plugin (`HttpCache`) for caching network responses when appropriate.
 *
 * This client is typically utilized as the default network client across the platform-specific
 * network infrastructure in the application.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(Js) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Represents the coroutine context used for network-related operations on the current platform.
 *
 * This context is typically used by default in platform-specific implementations
 * to provide a standardized coroutine execution environment for network tasks.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.Default

/**
 * A platform-specific implementation of the Tolgee class.
 * This class extends the Tolgee base class and provides platform-dependent behavior.
 *
 * @constructor Creates an instance of PlatformTolgee with the specified configuration.
 * @param config The configuration object used to initialize the Tolgee instance.
 */
@ConsistentCopyVisibility
actual data class PlatformTolgee internal constructor(override val config: Config) : Tolgee(config)