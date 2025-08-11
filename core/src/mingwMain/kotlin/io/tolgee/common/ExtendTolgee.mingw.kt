package io.tolgee.common

import io.tolgee.Tolgee
import io.ktor.client.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.plugins.cache.*
import io.tolgee.storage.TolgeeStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

/**
 * Creates a platform-specific instance of the Tolgee class with the given configuration.
 *
 * @param config The configuration object used to initialize the Tolgee instance.
 * @return A platform-specific instance of the Tolgee class.
 */
internal actual fun createPlatformTolgee(config: Tolgee.Config): PlatformTolgee {
    return PlatformTolgee(config)
}

/**
 * Provides a platform-specific instance of the `HttpClient` configured with the WinHttp engine.
 *
 * This `HttpClient` is set up with the following configurations:
 * - Redirects are automatically followed (`followRedirects = true`).
 * - The HTTP cache plugin is installed for caching support.
 *
 * This client is utilized as the default HTTP client for network-related operations on
 * Windows platforms within the library.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(WinHttp) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Provides a platform-specific coroutine context for network operations.
 *
 * Represents the default coroutine context used for executing network-related tasks on the current platform.
 * Typically, this context is based on the `Dispatchers.IO` dispatcher, optimized for IO-bound operations.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO

internal actual val platformStorage: TolgeeStorageProvider?
    get() = null

/**
 * Actual implementation of the `PlatformTolgee` class for a specific platform.
 * It extends the `Tolgee` base class and is initialized with a `Config` object.
 *
 * This class is used to provide platform-specific implementations or configurations
 * for the Tolgee library.
 *
 * @constructor Creates a `PlatformTolgee` instance with the provided configuration.
 * @param config The configuration object used to initialize the `PlatformTolgee` instance.
 */
@ConsistentCopyVisibility
actual data class PlatformTolgee internal constructor(override val config: Config) : Tolgee(config)