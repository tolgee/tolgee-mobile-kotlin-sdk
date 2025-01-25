package dev.datlag.tolgee

import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.tolgee.api.TolgeeAPI
import dev.datlag.tolgee.model.PermittedProjects
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

data class Tolgee internal constructor(
    internal val info: Info,
    internal val api: TolgeeAPI
) {

    @Serializable
    internal data class Info(
        internal val apiUrl: String?,
        private val apiKey: String,
        private val projectId: String?,
    ) {
        @Transient
        private var permittedProjects: PermittedProjects? = null

        @Transient
        private val mutex = Mutex()

        suspend fun loadPermittedProjects(api: TolgeeAPI): PermittedProjects? = suspendCatching {
            if (permittedProjects != null) {
                return@suspendCatching permittedProjects
            }

            mutex.withLock {
                if (permittedProjects != null) {
                    return@withLock permittedProjects
                }

                permittedProjects = suspendCatching {
                    api.projects(
                        apiKey = apiKey
                    )
                }.getOrNull()
                return@withLock permittedProjects
            }
        }.getOrNull()

        suspend fun getProjectId(api: TolgeeAPI): String {
            return projectId ?: loadPermittedProjects(api)?.embedded?.projects?.firstOrNull()?.id ?: throw IllegalStateException("No project found.")
        }
    }

    class Builder {
        private var apiUrl: String? = null
        private lateinit var apiKey: String
        private var projectId: String? = null

        fun apiUrl(value: String) = apply {
            this.apiUrl = value
        }

        fun apiKey(value: String) = apply {
            this.apiKey = value
        }

        fun projectId(value: String) = apply {
            this.projectId = value
        }

        fun build(): Tolgee = {
            val info = Info(
                apiUrl = apiUrl?.ifBlank { null },
                apiKey = apiKey,
                projectId = projectId?.ifBlank { null },
            )

            return Tolgee(
                info = info,
                api = ktorfit {
                    baseUrl(url = info.apiUrl ?: API_DEFAULT_URL)


                }.createTolgeeAPI()
            )
        }
    }

    companion object {
        private val API_DEFAULT_URL = "https://app.tolgee.io/v2/"
    }
}