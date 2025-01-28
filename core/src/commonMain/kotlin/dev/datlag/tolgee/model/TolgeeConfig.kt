package dev.datlag.tolgee.model

import kotlinx.serialization.Serializable

@Serializable
data class TolgeeConfig(
    val apiKey: String,
    val apiUrl: String = DEFAULT_API_URL,
    val projectId: String? = null,
    val cdnUrl: String? = null,
    val useCDN: Boolean = false,
) {
    companion object {
        internal const val DEFAULT_API_URL = "https://app.tolgee.io/v2/"
    }
}
