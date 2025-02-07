package dev.datlag.tolgee

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.forLocaleTag
import de.comahe.i18n4k.language
import dev.datlag.tolgee.api.TolgeeApi
import dev.datlag.tolgee.common.createPlatformTolgee
import dev.datlag.tolgee.common.mapNotNull
import dev.datlag.tolgee.common.platformHttpClient
import dev.datlag.tolgee.common.platformNetworkContext
import dev.datlag.tolgee.model.TolgeeMessageParams
import dev.datlag.tolgee.model.TolgeeProjectLanguage
import dev.datlag.tolgee.model.TolgeeTranslation
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import io.ktor.client.engine.*
import kotlinx.atomicfu.atomic
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
    private val cachedTranslation = atomic<TolgeeTranslation?>(null)

    /**
     * Determines which locale is used for String fetched from Tolgee.
     *
     * Created lazily to support overriding config.
     */
    private val localeFlow by lazy {
        MutableStateFlow(config.locale)
    }

    private suspend fun loadLanguages() = languagesMutex.withLock {
        cachedLanguages.ifEmpty { withContext(config.network.context) {
            TolgeeApi.getAllProjectLanguages(
                client = config.network.client,
                config = config
            ).also {
                cachedLanguages = it
            }
        } }
    }

    /**
     * Loads the translations from Tolgee atomically.
     *
     * @return [TolgeeTranslation]
     */
    private suspend fun loadTranslations(
        locale: Locale? = localeFlow.value
    ) = translationsMutex.withLock {
        currentTranslation(locale) ?: withContext(config.network.context) {
            TolgeeApi.getTranslations(
                client = config.network.client,
                config = config,
                currentLanguage = locale?.language?.ifBlank { null },
            ).also {
                cachedTranslation.value = it
            }
        }
    }

    private fun currentTranslation(
        locale: Locale? = localeFlow.value,
    ): TolgeeTranslation? {
        return cachedTranslation.value?.takeIf {
            if (locale != null) {
                it.hasLocale(locale)
            } else {
                true
            }
        }
    }

    suspend fun languages() = suspendCatching {
        loadLanguages()
    }.getOrNull() ?: cachedLanguages

    /**
     * Updating Tolgee translation for key with parameters.
     *
     * Respects locale changes from [setLocale].
     */
    @JvmOverloads
    @OptIn(ExperimentalCoroutinesApi::class)
    fun translation(
        key: CharSequence,
        parameters: TolgeeMessageParams = TolgeeMessageParams.None
    ): Flow<String> = localeFlow.mapLatest { locale ->
        val translation = currentTranslation() ?: suspendCatching {
            loadTranslations()
        }.getOrNull() ?: currentTranslation() ?: return@mapLatest null

        return@mapLatest translation.localized(key.toString(), parameters)?.toString(locale)
    }.mapNotNull()

    /**
     * Immediate Tolgee translation for key with parameters.
     *
     * **Requires** calling [preload] or [translation] before.
     * (Otherwise) May return null if no translations are loaded at time calling.
     *
     * Respects only the locale at time calling.
     */
    @JvmOverloads
    fun instant(
        key: CharSequence,
        parameters: TolgeeMessageParams = TolgeeMessageParams.None
    ): String? {
        val translation = currentTranslation() ?: return null

        return translation.localized(key.toString(), parameters)?.toString(localeFlow.value)
    }

    suspend fun preload() {
        suspendCatching { loadLanguages() }
        suspendCatching { loadTranslations() }
    }

    fun setLocale(locale: Locale) = localeFlow.updateAndGet { locale }
    fun setLocale(locale: String) = setLocale(forLocaleTag(locale))
    fun setLocale(language: TolgeeProjectLanguage) = setLocale(language.asLocale())

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