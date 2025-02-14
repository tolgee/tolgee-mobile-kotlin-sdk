package dev.datlag.tolgee.common

import dev.datlag.tolgee.Tolgee
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.cache.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Creates a platform-specific instance of Tolgee using the provided configuration.
 *
 * @param config The configuration object to initialize the Tolgee instance.
 * @return A platform-specific instance of Tolgee.
 */
internal actual fun createPlatformTolgee(config: Tolgee.Config): PlatformTolgee {
    return PlatformTolgee(config)
}

/**
 * Platform-specific instance of [HttpClient] configured for JavaScript environments.
 *
 * This HTTP client is preconfigured to:
 * - Follow redirects automatically.
 * - Utilize the HTTP cache for request optimizations.
 *
 * It uses the JavaScript engine (`Js`) as its backend and is initialized with
 * relevant plugins for typical use cases in browser or JavaScript-based applications.
 *
 * This client is marked as `internal` and primarily intended for internal usage
 * within the platform-specific implementations.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(Js) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Represents the platform-specific coroutine context for network operations.
 *
 * This property provides a `CoroutineContext` that is optimized for network-related tasks on the current platform.
 * The context is used for executing suspending functions and coroutine-based workflows that involve network interactions.
 *
 * On some platforms, this is typically based on `Dispatchers.Default` or equivalent, ensuring appropriate thread usage
 * and performance.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.Default

/**
 * A platform-specific implementation of the `Tolgee` class.
 *
 * This class provides platform-specific configuration and behavior for Tolgee functionalities
 * and extends the core `Tolgee` class by implementing platform-targeted logic.
 *
 * @constructor Creates an instance of `PlatformTolgee` with the provided configuration.
 * @param config The configuration used to initialize the Tolgee instance.
 */
@ConsistentCopyVisibility
actual data class PlatformTolgee internal constructor(override val config: Config) : Tolgee(config)