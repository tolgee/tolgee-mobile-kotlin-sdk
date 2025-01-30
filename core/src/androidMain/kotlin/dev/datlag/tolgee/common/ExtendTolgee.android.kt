package dev.datlag.tolgee.common

import io.ktor.client.*
import io.ktor.client.engine.android.*

internal actual val platformHttpClient: HttpClient? = HttpClient(Android) {
    followRedirects = true
}