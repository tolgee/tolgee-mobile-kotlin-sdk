package io.tolgee.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a paginated response structure used by the Tolgee platform.
 * This data class encapsulates the necessary attributes for handling
 * paginated data and associated metadata, such as pagination details,
 * links, and language selection.
 *
 * @param T The type of the embedded data contained within the paginated response.
 * @property embedded The main content or data object embedded in the paginated response.
 * @property links An optional object containing navigation links related to the current response.
 * @property page An optional object with information about the paging state, like page size and total elements.
 * @property selectedLanguages An optional set of selected languages applicable to the current response.
 * @property nextCursor An optional string that represents the cursor for the next set of paginated data.
 */
@Serializable
internal data class TolgeePagedResponse<T>(
    @SerialName("_embedded") @Contextual val embedded: T,
    @SerialName("_links") val links: Links? = null,
    @SerialName("page") val page: Page? = null,
    @SerialName("selectedLanguages") val selectedLanguages: Set<TolgeeProjectLanguage>? = null,
    @SerialName("nextCursor") val nextCursor: String? = null,
) {

    /**
     * Represents a collection of hypermedia links related to a resource.
     *
     * This data class defines a structure for handling HATEOAS-compliant links,
     * allowing for navigation and resource discovery in APIs.
     *
     * @property self The link to the resource itself, encapsulated in a [Link] object.
     */
    @Serializable
    data class Links(
        @SerialName("self") val self: Link? = null,
    ) {

        /**
         * Represents a hyperlink with a specified target URL.
         *
         * The `Link` data class is primarily used to encapsulate a single hyperlink, including its
         * destination (href). It is typically employed in contexts where hyperlinks need to be
         * serialized or deserialized, such as within the API responses or data transfer objects.
         *
         * @property href The URL to which the link points.
         */
        @Serializable
        data class Link(
            @SerialName("href") val href: String,
        )
    }

    /**
     * Represents pagination details for a paginated response.
     *
     * This class encapsulates information about the pagination status, such as the size of each page,
     * the total number of elements and pages, and the current page number.
     *
     * @property size The number of elements on a single page.
     * @property totalElements The total number of elements across all pages.
     * @property totalPages The total number of pages available.
     * @property number The current page number (zero-based index).
     */
    @Serializable
    data class Page(
        @SerialName("size") val size: Int,
        @SerialName("totalElements") val totalElements: Int,
        @SerialName("totalPages") val totalPages: Int,
        @SerialName("number") val number: Int,
    )
}
