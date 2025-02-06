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

internal actual val platformHttpClient: HttpClient = HttpClient(Android) {
    followRedirects = true
    install(HttpCache)
}

internal actual fun createPlatformTolgee(config: Tolgee.Config): Tolgee {
    return TolgeeAndroid(config)
}

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