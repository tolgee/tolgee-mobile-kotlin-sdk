package dev.datlag.tolgee.api.responses

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TolgeePagedResponse<T>(
    @SerialName("_embedded") @Contextual val embedded: T,
    @SerialName("_links") val links: Links? = null,
    @SerialName("page") val page: Page? = null,
    @SerialName("nextCursor") val nextCursor: String? = null,
) {

    @Serializable
    data class Links(
        @SerialName("self") val self: Link? = null,
    ) {

        @Serializable
        data class Link(
            @SerialName("href") val href: String,
        )
    }

    @Serializable
    data class Page(
        @SerialName("size") val size: Int,
        @SerialName("totalElements") val totalElements: Int,
        @SerialName("totalPages") val totalPages: Int,
        @SerialName("number") val number: Int,
    )
}
