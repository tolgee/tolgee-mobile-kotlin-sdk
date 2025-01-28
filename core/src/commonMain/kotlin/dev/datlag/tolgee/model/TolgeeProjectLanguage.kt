package dev.datlag.tolgee.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TolgeeProjectLanguage(
    val name: String,
    val tag: String,
    val originalName: String? = null,
    val flagEmoji: String? = null,
    val base: Boolean
) {

    @Serializable
    data class PagedWrapper(
        @SerialName("languages") val languages: List<TolgeeProjectLanguage>
    )
}
