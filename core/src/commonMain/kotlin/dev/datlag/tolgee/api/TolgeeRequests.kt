package dev.datlag.tolgee.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.*

interface TolgeeRequests {

    @GET("projects/languages")
    suspend fun allProjectLanguages(
        @Header("X-Api-Key") apiKey: String,
    ): HttpResponse

    @GET("projects/{projectId}/languages")
    suspend fun allProjectLanguages(
        @Header("X-Api-Key") apiKey: String,
        @Path("projectId") projectId: String,
    ): HttpResponse

    @GET("projects/translations")
    suspend fun translations(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String?,
        @Query("languages") languages: String,
    ): HttpResponse


}