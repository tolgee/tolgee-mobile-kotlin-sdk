package dev.datlag.tolgee.common

import dev.datlag.tolgee.Tolgee
import io.ktor.client.*

internal expect fun createPlatformTolgee(config: Tolgee.Config): Tolgee
internal expect val platformHttpClient: HttpClient?