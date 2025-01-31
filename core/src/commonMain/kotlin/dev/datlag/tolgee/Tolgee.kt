package dev.datlag.tolgee

import de.comahe.i18n4k.Locale
import dev.datlag.tolgee.common.createPlatformTolgee
import dev.datlag.tolgee.common.platformHttpClient
import dev.datlag.tolgee.common.platformNetworkContext
import io.ktor.client.*
import io.ktor.client.engine.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

open class Tolgee(
    open val config: Config
) {

    @JvmOverloads
    open suspend fun getTranslation(
        key: String,
        locale: Locale? = config.locale,
        vararg args: Any
    ): String? = withContext(config.network.context) {
        // restrict fetching to locale (api 'languages' query) or no restriction
        // use locale in toString method, falls back to systemLocale anyway
        return@withContext null
    }

    @JvmOverloads
    open fun getTranslationFromCache(
        key: String,
        locale: Locale? = config.locale,
        vararg args: Any
    ): String? {
        return null
    }

    @ConsistentCopyVisibility
    data class Config internal constructor(
        val apiKey: String,
        val apiUrl: String,
        val projectId: String?,
        val locale: Locale?,
        val network: Network
    ) {

        class Builder {
            lateinit var apiKey: String

            var apiUrl: String = DEFAULT_API_URL
                set(value) {
                    field = value.trim().ifBlank { null } ?: DEFAULT_API_URL
                }

            var projectId: String? = null
                set(value) {
                    field = value?.trim()?.ifBlank { null }
                }

            var locale: Locale? = null

            var network: Network = Network()

            fun apiKey(apiKey: String) = apply {
                this.apiKey = apiKey
            }

            fun apiUrl(url: String) = apply {
                this.apiUrl = url
            }

            fun projectId(projectId: String?) = apply {
                this.projectId = projectId
            }

            fun locale(locale: Locale) = apply {
                this.locale = locale
            }

            fun network(network: Network) = apply {
                this.network = network
            }

            fun network(builder: Network.Builder.() -> Unit) = apply {
                this.network = Network.Builder().apply(builder).build()
            }

            fun build(): Config = Config(
                apiKey = apiKey,
                apiUrl = apiUrl,
                projectId = projectId,
                locale = locale,
                network = network
            )
        }

        @ConsistentCopyVisibility
        data class Network internal constructor(
            val client: HttpClient = platformHttpClient,
            val context: CoroutineContext = platformNetworkContext
        ) {

            class Builder {
                var client: HttpClient = platformHttpClient
                var context: CoroutineContext = platformNetworkContext

                fun client(client: HttpClient) = apply {
                    this.client = client
                }

                fun client(engine: HttpClientEngine) = client(HttpClient(engine))

                fun <T : HttpClientEngineConfig> client(engineFactory: HttpClientEngineFactory<T>) = client(HttpClient(engineFactory))

                fun client(config: HttpClientConfig<*>.() -> Unit) = client(HttpClient(config))

                fun client(
                    engine: HttpClientEngine,
                    config: HttpClientConfig<*>.() -> Unit
                ) = client(HttpClient(engine, config))

                fun <T : HttpClientEngineConfig> client(
                    engineFactory: HttpClientEngineFactory<T>,
                    config: HttpClientConfig<T>.() -> Unit
                ) = client(HttpClient(engineFactory, config))

                fun context(context: CoroutineContext) = apply {
                    this.context = context
                }

                fun build(): Network = Network(
                    client = client,
                    context = context
                )
            }
        }

        companion object {
            internal const val DEFAULT_API_URL = "https://app.tolgee.io/v2/"
        }
    }

    companion object {
        @JvmStatic
        val systemLocale
            get() = de.comahe.i18n4k.systemLocale

        private val _instance = atomic<Tolgee?>(null)

        @JvmStatic
        val instance: Tolgee?
            get() = _instance.value

        @JvmStatic
        @JvmOverloads
        fun init(
            global: Boolean = _instance.value == null,
            config: Config
        ) = createPlatformTolgee(config).also {
            if (global) {
                _instance.value = it
            }
        }

        @JvmStatic
        @JvmOverloads
        fun init(
            global: Boolean = _instance.value == null,
            builder: Config.Builder.() -> Unit
        ) = init(global, Config.Builder().apply(builder).build())

        @JvmStatic
        @JvmOverloads
        fun instanceOrInit(
            global: Boolean = _instance.value == null,
            config: Config
        ) = instance ?: init(global, config)

        @JvmStatic
        @JvmOverloads
        fun instanceOrInit(
            global: Boolean = _instance.value == null,
            builder: Config.Builder.() -> Unit
        ) = instance ?: init(global, builder)
    }
}