package io.tolgee.common

import io.tolgee.Tolgee
import io.ktor.client.*
import io.ktor.client.engine.curl.Curl
import io.ktor.client.plugins.cache.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

/**
 * Creates a platform-specific instance of Tolgee with the given configuration.
 *
 * @param config The configuration object used to initialize the Tolgee instance.
 * @return A platform-specific instance of Tolgee initialized with the provided configuration.
 */
internal actual fun createPlatformTolgee(config: Tolgee.Config): PlatformTolgee {
    return PlatformTolgee(config)
}

/**
 * Represents the platform-specific `HttpClient` instance configured for use with Curl engine.
 *
 * This HTTP client is pre-configured to:
 * - Follow HTTP redirects automatically.
 * - Utilize HTTP caching via the `HttpCache` plugin.
 *
 * It is intended for internal use within the platform-specific functionality and should
 * be utilized where consistent HTTP client behavior is required across the platform.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(Curl) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Represents the platform-specific CoroutineContext used for network operations.
 *
 * This context defaults to `Dispatchers.IO` and is utilized for performing network requests
 * efficiently, offloading these operations from the main thread.
 *
 * It is particularly useful in configurations where network functionalities are required,
 * such as the `Network` class or its `Builder`, to ensure operations are executed in an optimized, non-blocking environment.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO

/**
 * An actual implementation of the `Tolgee` class for a specific platform.
 * This class is used to handle platform-specific operations and configurations
 * related to the Tolgee localization framework.
 *
 * @constructor Initializes the platform-specific instance of `PlatformTolgee`.
 * @property config The configuration object that contains settings and options
 * necessary for the operation of the `Tolgee` framework.
 */
@ConsistentCopyVisibility
actual data class PlatformTolgee internal constructor(override val config: Config) : Tolgee(config)