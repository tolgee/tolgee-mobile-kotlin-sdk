package dev.datlag.tolgee.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TolgeeKey(
    @SerialName("keyId") val keyId: Int,
    @SerialName("keyName") val keyName: String,
    @SerialName("keyDescription") val keyDescription: String? = null,
    @SerialName("translations") val translations: Map<String, Translation>
) {

    internal fun translationForOrFirst(language: String?) = language?.ifBlank { null }?.let(translations::get)
        ?: translations.firstNotNullOfOrNull { it.value }

    @Serializable
    internal data class Translation(
        @SerialName("text") val text: String?,
    )

    @Serializable
    data class PagedWrapper(
        @SerialName("keys") val keys: List<TolgeeKey>
    )
}
