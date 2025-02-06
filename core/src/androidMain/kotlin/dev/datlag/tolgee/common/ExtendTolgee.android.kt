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
