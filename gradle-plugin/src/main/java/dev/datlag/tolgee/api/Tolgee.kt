package dev.datlag.tolgee.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.*

internal interface Tolgee {

    @GET("projects/{projectId}/export")
    suspend fun export(
        @Header("X-API-Key") apiKey: String,
        @Path("projectId") projectId: String,
        @Query("format") format: String,
        @Query("languages") languages: String?,
        @Query("filterState") states: String?,
        @Query("filterNamespace") namespaces: String?,
        @Query("zip") zip: Boolean
    ): HttpResponse
}