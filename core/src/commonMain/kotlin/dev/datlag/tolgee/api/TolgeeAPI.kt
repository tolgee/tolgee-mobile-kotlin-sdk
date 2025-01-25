package dev.datlag.tolgee.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.tolgee.model.PermittedProjects

internal interface TolgeeAPI {

    @GET("projects")
    suspend fun projects(
        @Header("X-API-Key") apiKey: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
    ): PermittedProjects
}