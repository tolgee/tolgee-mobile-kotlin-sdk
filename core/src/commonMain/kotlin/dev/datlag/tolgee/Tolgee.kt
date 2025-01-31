package dev.datlag.tolgee

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.language
import dev.datlag.tolgee.api.TolgeeApi
import dev.datlag.tolgee.common.createPlatformTolgee
import dev.datlag.tolgee.common.platformHttpClient
import dev.datlag.tolgee.common.platformNetworkContext
import dev.datlag.tolgee.model.TolgeeProjectLanguage
import dev.datlag.tolgee.model.TolgeeTranslation
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import io.ktor.client.engine.*
import kotlinx.atomicfu.atomic
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

open class Tolgee(
    open val config: Config
) {

    private val languagesMutex = Mutex()
    private var cachedLanguages: ImmutableSet<TolgeeProjectLanguage> = persistentSetOf()

    private val translationsMutex = Mutex()
    private var cachedTranslation: TolgeeTranslation? = null

    private suspend fun loadLanguages() = languagesMutex.withLock {
        cachedLanguages.ifEmpty {
            TolgeeApi.getAllProjectLanguages(
                client = config.network.client,
                config = config
            ).also {
                cachedLanguages = it
            }
        }
    }

    suspend fun allLanguages() = withContext(config.network.context) {
        return@withContext cachedLanguages.ifEmpty {
            suspendCatching {
                loadLanguages()
            }.getOrNull() ?: cachedLanguages
        }
    }

    fun allLanguagesFromCache() = cachedLanguages

    private suspend fun loadTranslations() = translationsMutex.withLock {
        cachedTranslation ?: TolgeeApi.getTranslations(
            client = config.network.client,
            config = config,
            currentLanguage = config.locale?.language?.ifBlank { null },
        ).also {
            cachedTranslation = it
        }
    }

    suspend fun translation(
        key: String,
        locale: Locale? = config.locale,
        vararg formatArgs: Any
    ): String? = withContext(config.network.context) {
        val translation = cachedTranslation ?: suspendCatching {
            loadTranslations()
        }.getOrNull() ?: cachedTranslation ?: return@withContext null

        return@withContext translation.localized(key, *formatArgs)?.toString(locale)
    }

    suspend fun translation(
        key: String,
        vararg formatArgs: Any,
    ) = translation(key, config.locale, *formatArgs)

    fun translationFromCache(
        key: String,
        locale: Locale? = config.locale,
        vararg formatArgs: Any
    ): String? {
        val translation = cachedTranslation ?: return null

        return translation.localized(key, *formatArgs)?.toString(locale)
    }

    fun translationFromCache(
        key: String,
        vararg formatArgs: Any,
    ) = translationFromCache(key, config.locale, *formatArgs)

    suspend fun preload() {
        suspendCatching { loadLanguages() }
        suspendCatching { loadTranslations() }
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