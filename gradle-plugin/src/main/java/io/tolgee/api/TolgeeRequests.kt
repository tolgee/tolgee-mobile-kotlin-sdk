package io.tolgee.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.*

internal interface TolgeeRequests {

    @Headers("sdkType: Compose Multiplatform")
    @GET("projects/{projectId}/export")
    suspend fun export(
        @Header("X-API-Key") apiKey: String,
        @Header("sdkVersion") sdkVersion: String,
        @Path("projectId") projectId: String,
        @Query("format") format: String,
        @Query("languages") languages: String?,
        @Query("filterState") states: String?,
        @Query("filterNamespace") namespaces: String?,
        @Query("filterTagIn") tags: String?,
        @Query("filterTagNotIn") excludeTags: String?,
        @Query("zip") zip: Boolean
    ): HttpResponse

    @Headers("sdkType: Compose Multiplatform")
    @GET("projects/export")
    suspend fun export(
        @Header("X-API-Key") apiKey: String,
        @Header("sdkVersion") sdkVersion: String,
        @Query("format") format: String,
        @Query("languages") languages: String?,
        @Query("filterState") states: String?,
        @Query("filterNamespace") namespaces: String?,
        @Query("filterTagIn") tags: String?,
        @Query("filterTagNotIn") excludeTags: String?,
        @Query("zip") zip: Boolean
    ): HttpResponse
}