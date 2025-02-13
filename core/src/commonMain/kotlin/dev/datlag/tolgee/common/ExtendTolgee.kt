package dev.datlag.tolgee.common

import dev.datlag.tolgee.Tolgee
import io.ktor.client.*
import kotlin.coroutines.CoroutineContext

/**
 * Creates a platform-specific instance of the Tolgee localization service.
 *
 * This function is expected to be implemented differently for each platform to initialize a
 * platform-compatible Tolgee instance based on the provided configuration.
 *
 * @param config The configuration object for initializing the Tolgee instance, containing API details,
 *               project-specific settings, and other customization options.
 * @return A platform-specific instance of the Tolgee class, initialized with the given configuration.
 */
internal expect fun createPlatformTolgee(config: Tolgee.Config): Tolgee

/**
 * Platform-specific instance of [HttpClient] used for making HTTP requests.
 * This property is expected to be provided by each platform's implementation
 * to enable network communication in a consistent manner.
 */
internal expect val platformHttpClient: HttpClient

/**
 * Expected declaration for the platform-specific CoroutineContext used for network-related operations.
 *
 * This property provides a platform-dependent coroutine context to manage concurrency in network interactions.
 * It is typically used as the default context for executing coroutines in networking scenarios.
 */
internal expect val platformNetworkContext: CoroutineContext