package io.tolgee.api

import io.tolgee.model.Format
import io.tolgee.model.pull.State
import io.ktor.client.statement.*
import io.tolgee.TolgeePlugin

internal object TolgeeApi {

    suspend fun pull(
        api: TolgeeRequests,
        apiKey: String,
        projectId: String?,
        format: Format,
        languages: Collection<String>?,
        states: Collection<State>?,
        namespaces: Collection<String>?,
        tags: Collection<String>?,
        excludeTags: Collection<String>?,
    ): HttpResponse {
        val normalizedLanguages = languages?.joinToString(separator = ",")?.ifBlank { null }
        val normalizedStates = states?.joinToString(separator = ",") { it.value }?.ifBlank { null }
        val normalizedNamespaces = namespaces?.joinToString(separator = ",")?.ifBlank { null }
        val normalizedTags = tags?.joinToString(separator = ",")?.ifBlank { null }
        val normalizedExcludeTags = excludeTags?.joinToString(separator = ",")?.ifBlank { null }

        return projectId?.let {
            api.export(
                apiKey = apiKey,
                sdkVersion = TolgeePlugin.version,
                projectId = it,
                format = format.value,
                languages = normalizedLanguages,
                states = normalizedStates,
                namespaces = normalizedNamespaces,
                tags = normalizedTags,
                excludeTags = normalizedExcludeTags,
                zip = true
            )
        } ?: api.export(
            apiKey = apiKey,
            sdkVersion = TolgeePlugin.version,
            format = format.value,
            languages = normalizedLanguages,
            states = normalizedStates,
            namespaces = normalizedNamespaces,
            tags = normalizedTags,
            excludeTags = normalizedExcludeTags,
            zip = true
        )
    }
}