package dev.datlag.tolgee.common

import io.ktor.client.*
import io.ktor.client.engine.darwin.Darwin

internal actual val platformHttpClient: HttpClient? = HttpClient(Darwin) {
    followRedirects = true
}