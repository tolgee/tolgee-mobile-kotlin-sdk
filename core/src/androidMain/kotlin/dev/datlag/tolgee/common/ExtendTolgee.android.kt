package dev.datlag.tolgee.common

import android.content.Context
import androidx.annotation.StringRes
import dev.datlag.tolgee.Tolgee
import dev.datlag.tolgee.TolgeeAndroid
import io.ktor.client.*
import io.ktor.client.engine.android.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal actual val platformHttpClient: HttpClient = HttpClient(Android) {
    followRedirects = true
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
fun Context.getString(tolgee: Tolgee, @StringRes resId: Int, vararg formatArgs: Any): String {
    return (tolgee as? TolgeeAndroid)?.getStringFromCache(this, resId, *formatArgs)
        ?: TolgeeAndroid.getKeyFromRes(this, resId)?.let {
        tolgee.getTranslationFromCache(key = it, args = formatArgs)
    } ?: this.getString(resId, *formatArgs)
}