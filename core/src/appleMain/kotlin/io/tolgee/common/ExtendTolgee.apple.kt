package io.tolgee.common

import de.comahe.i18n4k.createLocale
import io.tolgee.Tolgee
import io.tolgee.TolgeeApple
import io.ktor.client.*
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.cache.*
import io.tolgee.storage.TolgeeStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.languageCode
import platform.Foundation.variantCode
import kotlin.coroutines.CoroutineContext

/**
 * The default platform-specific HTTP client configured for Darwin-based platforms (e.g., iOS, macOS).
 *
 * This client includes the following configurations:
 * - Redirection following (`followRedirects = true`) to handle HTTP redirects automatically.
 * - Caching support through the HTTP cache plugin (`install(HttpCache)`).
 *
 * It serves as the default HTTP client for operations requiring network interactions when targeting Darwin platforms.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(Darwin) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Creates a platform-specific instance of the Tolgee localization tool.
 *
 * @param config Configuration object used to initialize the Tolgee instance.
 * @return A platform-specific Tolgee instance.
 */
internal actual fun createPlatformTolgee(config: Tolgee.Config): PlatformTolgee {
    return TolgeeApple(config)
}

/**
 * Provides the platform-specific CoroutineContext for performing network-related operations.
 *
 * This context ensures that network tasks are executed on an appropriate thread
 * or dispatcher, depending on the platform implementation.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO

internal actual val platformStorage: TolgeeStorageProvider?
    get() = null

/**
 * Provides a platform-specific type alias for the Tolgee localization framework implementation.
 * On Apple platforms, `PlatformTolgee` is resolved to `TolgeeApple`, which handles localization
 * functionality specific to Apple environments.
 */
actual typealias PlatformTolgee = TolgeeApple

/**
 * Sets the locale configuration for the builder and returns the instance for further customization.
 *
 * @param nsLocale The locale to be set for the builder.
 */
fun Tolgee.Config.Builder.locale(nsLocale: NSLocale) = locale(
    createLocale(
        language = nsLocale.languageCode,
        country = nsLocale.countryCode?.ifBlank { null },
        variant = nsLocale.variantCode?.ifBlank { null }
    )
)