package dev.datlag.tolgee.api

import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.tolgee.api.responses.TolgeePagedResponse
import dev.datlag.tolgee.model.TolgeeConfig
import dev.datlag.tolgee.model.TolgeeProjectLanguage
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

internal data object TolgeeApi {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getAllProjectLanguages(client: HttpClient, config: TolgeeConfig): List<TolgeeProjectLanguage> {
        val api = requestsInstance(client, config)
        val response = suspendCatching {
            config.projectId?.let {
                api.allProjectLanguages(
                    apiKey = config.apiKey,
                    projectId = it
                ).takeIf { res ->
                    res.status.isSuccess()
                }
            }
        }.getOrNull() ?: suspendCatching {
            api.allProjectLanguages(apiKey = config.apiKey).takeIf { res -> res.status.isSuccess() }
        }.getOrNull() ?: return emptyList()

        return suspendCatching {
            json.decodeFromString<TolgeePagedResponse<TolgeeProjectLanguage.PagedWrapper>>(
                response.bodyAsText()
            )
        }.getOrNull()?.embedded?.languages ?: emptyList()
    }

    private fun requestsInstance(client: HttpClient, config: TolgeeConfig): TolgeeRequests {
        return ktorfit {
            baseUrl(config.apiUrl)

            httpClient(client)
        }.createTolgeeRequests()
    }
}