package dev.datlag.tolgee.model

import de.comahe.i18n4k.forLocaleTag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TolgeeProjectLanguage(
    @SerialName("name") val name: String,
    @SerialName("tag") val tag: String,
    @SerialName("originalName") val originalName: String? = null,
    @SerialName("flagEmoji") val flagEmoji: String? = null,
    @SerialName("base") val base: Boolean
) {

    fun asLocale() = forLocaleTag(tag)

    @Serializable
    internal data class PagedWrapper(
        @SerialName("languages") val languages: List<TolgeeProjectLanguage>
    )
}
