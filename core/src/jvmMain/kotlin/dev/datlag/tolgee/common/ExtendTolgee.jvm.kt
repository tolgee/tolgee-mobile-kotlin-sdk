package dev.datlag.tolgee.common

import dev.datlag.tolgee.Tolgee
import io.ktor.client.*
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.cache.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Creates and returns an instance of the `Tolgee` class, configured with the provided settings.
 *
 * @param config The configuration object used to initialize the `Tolgee` instance.
 * @return A new instance of the `Tolgee` class initialized with the specified configuration.
 */
internal actual fun createPlatformTolgee(config: Tolgee.Config): Tolgee {
    return Tolgee(config)
}

/**
 * The default HTTP client configured for the platform-specific implementation.
 *
 * This client is tailored for use on the JVM, leveraging the Java HTTP engine.
 * It includes the capability to follow redirects automatically and has an HTTP
 * cache installed to handle caching of network requests.
 *
 * Used internally to provide a consistent HTTP client across the platform-specific
 * implementations of networking functionalities.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(Java) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Provides a platform-specific CoroutineContext for network operations.
 *
 * On the JVM platform, this specifically uses `Dispatchers.IO`, which is optimized
 * for executing blocking I/O tasks such as file or network access.
 *
 * This property ensures that network-related coroutines are run on the most appropriate
 * thread pool for their operations, improving performance and responsiveness.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO
