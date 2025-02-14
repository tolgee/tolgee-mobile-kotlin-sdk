package dev.datlag.tolgee.common

import android.content.Context
import androidx.annotation.StringRes
import dev.datlag.tolgee.Tolgee
import dev.datlag.tolgee.TolgeeAndroid
import dev.datlag.tolgee.model.TolgeeMessageParams
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.cache.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * A platform-specific instance of [HttpClient] configured for Android with default settings.
 *
 * This client is initialized with Android-specific configurations, enabling functionalities like
 * automatic handling of HTTP redirects and caching support through the [HttpCache] feature.
 * It serves as the default HTTP client used in platform-dependent operations where HTTP communication is needed.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(Android) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Creates a platform-specific instance of the Tolgee library.
 *
 * @param config The configuration object required to initialize Tolgee.
 * @return A platform-specific implementation of the Tolgee library.
 */
internal actual fun createPlatformTolgee(config: Tolgee.Config): PlatformTolgee {
    return TolgeeAndroid(config)
}

/**
 * Represents a platform-specific CoroutineContext used for network operations.
 *
 * This property provides a CoroutineContext optimized for performing I/O operations,
 * ensuring efficient execution of network-related tasks. It is typically used in
 * scenarios where network requests or other I/O-bound tasks need to run on a
 * suitable dispatcher.
 *
 * By default, this is set to `Dispatchers.IO`, which is designed for offloading blocking
 * I/O tasks to a shared pool of threads.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO

/**
 * Returns a localized formatted string from [Tolgee] cache or the application's package's
 * default string table, substituting the format arguments as defined in
 * [java.util.Formatter] and [java.lang.String.format].
 *
 * @param tolgee The [Tolgee] instance to get the cached string from.
 * @param resId Resource id for the format string
 * @param formatArgs The format arguments that will be used for
 *                   substitution.
 * @return The string data associated with the resource, formatted and
 *         stripped of styled text information.
 */
fun Context.getStringInstant(tolgee: Tolgee, @StringRes resId: Int, vararg formatArgs: Any): String {
    return (tolgee as? TolgeeAndroid)?.instant(this, resId, *formatArgs)
        ?: TolgeeAndroid.getKeyFromStringResource(this, resId)?.let {
            tolgee.instant(key = it, parameters = TolgeeMessageParams.Indexed(*formatArgs))
        } ?: this.getString(resId, *formatArgs)
}

/**
 * Returns a localized formatted string from [Tolgee] cache or the application's package's
 * default string table, substituting the format arguments as defined in
 * [java.util.Formatter] and [java.lang.String.format].
 *
 * @param resId Resource id for the format string
 * @param formatArgs The format arguments that will be used for
 *                   substitution.
 * @return The string data associated with the resource, formatted and
 *         stripped of styled text information.
 */
fun Context.getStringInstant(@StringRes resId: Int, vararg formatArgs: Any): String {
    val instance = Tolgee.instance ?: return this.getString(resId, *formatArgs)
    return this.getStringInstant(instance, resId, *formatArgs)
}

/**
 * Typealias representing a platform-specific implementation of the Tolgee class for Android.
 *
 * This alias maps `PlatformTolgee` to `TolgeeAndroid` on the Android platform, allowing platform
 * dependency abstraction in multi-platform projects. The actual implementation, `TolgeeAndroid`,
 * provides Android-specific utilities for managing translations and localization tasks using
 * string resources.
 */
actual typealias PlatformTolgee = TolgeeAndroid