package dev.datlag.tolgee.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PermittedProjects(
    @SerialName("_embedded") val embedded: Embedded,
    @SerialName("page") val page: Page
) {
    @Serializable
    internal data class Embedded(
        @SerialName("projects") val projects: Set<Project>
    ) {

        @Serializable
        internal data class Project(
            @SerialName("id") val id: String,
            @SerialName("name") val name: String,
        )
    }

    @Serializable
    internal data class Page(
        @SerialName("size") val size: Int = 0,
        @SerialName("totalElements") val totalElements: Int = 0,
        @SerialName("totalPages") val totalPages: Int = 0,
        @SerialName("number") val number: Int = 0,
    )
}
