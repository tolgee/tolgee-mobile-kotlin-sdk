package dev.datlag.tolgee

import dev.datlag.tolgee.model.PermittedProjects
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

data class Tolgee internal constructor(
    internal val info: Info,
    internal val network: Network
) {

    @Serializable
    internal data class Info(
        private val _apiUrl: String,
        private val apiKey: String,
        private val projectId: String?,
    ) {
        @Transient
        private val apiUrl: String = _apiUrl.let {
            if (it.endsWith('/')) {
                it
            } else {
                "$it/"
            }
        }

        @Transient
        private var permittedProjects: PermittedProjects? = null

        @Transient
        private val mutex = Mutex()

        suspend fun loadPermittedProjects(client: HttpClient): PermittedProjects? = suspendCatching {
            if (permittedProjects != null) {
                return@suspendCatching permittedProjects
            }

            mutex.withLock {
                if (permittedProjects != null) {
                    return@withLock permittedProjects
                }

                permittedProjects = suspendCatching {
                    val response = client.get("${apiUrl}projects") {
                        header("X-API-Key", apiKey)
                        header("Accept", "application/json")
                        parameter("page", 0)
                        parameter("size", 1)
                    }

                    suspendCatching {
                        response.body<PermittedProjects>()
                    }.getOrNull() ?: suspendCatching {
                        json.decodeFromString<PermittedProjects>(response.bodyAsText())
                    }.getOrNull()
                }.getOrNull() ?: permittedProjects
                return@withLock permittedProjects
            }
        }.getOrNull()

        suspend fun getProjectId(client: HttpClient): String {
            return projectId ?: loadPermittedProjects(client)?.embedded?.projects?.firstOrNull()?.id ?: throw IllegalStateException("No project found.")
        }
    }

    data class Network internal constructor(
        internal val client: HttpClient,
        internal val context: CoroutineContext
    ) {
        class Builder {
            private lateinit var client: HttpClient
            private var context: CoroutineContext = Dispatchers.Default

            fun client(client: HttpClient) = apply {
                this.client = client
            }

            fun context(context: CoroutineContext) = apply {
                this.context = context
            }

            fun build(): Network = Network(client, context)
        }
    }

    class Builder {
        private var apiUrl: String = API_DEFAULT_URL
        private lateinit var apiKey: String
        private var projectId: String? = null

        private lateinit var network: Network

        fun apiUrl(value: String) = apply {
            this.apiUrl = value
        }

        fun apiKey(value: String) = apply {
            this.apiKey = value
        }

        fun projectId(value: String) = apply {
            this.projectId = value
        }

        fun network(value: Network) = apply {
            this.network = value
        }

        fun network(builder: Network.Builder.() -> Unit) = apply {
            this.network = Network.Builder().apply(builder).build()
        }

        @JvmOverloads
        fun build(global: Boolean = getInstance() == null): Tolgee = Tolgee(
            info = Info(
                _apiUrl = apiUrl.ifBlank { null } ?: API_DEFAULT_URL,
                apiKey = apiKey,
                projectId = projectId?.ifBlank { null },
            ),
            network = network
        ).also {
            if (global) {
                instance.value = it
            }
        }
    }

    companion object {
        private val API_DEFAULT_URL = "https://app.tolgee.io/v2/"

        private var instance = atomic<Tolgee?>(null)
        private val instanceLock = SynchronizedObject()

        @JvmStatic
        fun getInstance(): Tolgee? {
            return instance.value ?: synchronized(instanceLock) {
                instance.value
            }
        }

        @JvmStatic
        @JvmOverloads
        fun getInstanceOr(
            global: Boolean = getInstance() == null,
            builder: Builder.() -> Unit
        ): Tolgee {
            return getInstance() ?: Builder().apply(builder).build(global)
        }

        @JvmStatic
        fun getInstanceOrDefault(default: Tolgee): Tolgee {
            return getInstance() ?: default
        }

        private val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
}

@JvmOverloads
fun Tolgee(
    global: Boolean = Tolgee.getInstance() == null,
    builder: Tolgee.Builder.() -> Unit
): Tolgee = Tolgee.Builder().apply(builder).build(global)