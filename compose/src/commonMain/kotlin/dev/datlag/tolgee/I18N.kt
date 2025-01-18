package dev.datlag.tolgee

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import dev.datlag.tolgee.format.sprintf
import dev.datlag.tooling.async.scopeCatching
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import org.jetbrains.compose.resources.StringResource
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmOverloads

/**
 * Class for handling localization.
 *
 * @param contentDelivery a [ContentDelivery] feature.
 * @param locale the users or used [Locale].
 */
@ConsistentCopyVisibility
data class I18N internal constructor(
    private val contentDelivery: ContentDelivery?,
    val locale: Locale?,
    private val client: HttpClient?,
    private val networkDispatcher: CoroutineContext,
    private val initialTranslationCache: ImmutableMap<String, String>?
) : AutoCloseable {

    private var translationCache: ImmutableMap<String, String> = initialTranslationCache ?: persistentMapOf()

    private var fetchRequired: Boolean = true
    private val fetchTranslationMutex = Mutex()

    /**
     * Retrieve the translation for the given [key] from cache.
     */
    fun getTranslation(key: String): String? = translationCache[key]

    /**
     * Retrieve the translation for the given [key] from cache.
     */
    operator fun get(key: StringResource) = getTranslation(key.key)

    private suspend fun fetchTranslation(): Map<String, String>? = fetchTranslationMutex.withLock {
        if (!fetchRequired && translationCache.isNotEmpty()) {
            return@withLock null
        }

        return@withLock this.contentDelivery?.fetch(
            client = this.client ?: return@withLock null,
            locale = this.locale ?: return@withLock null
        )
    }

    internal suspend fun translation(key: String): String? = withContext(networkDispatcher) {
        suspendCatching {
            fetchTranslation()
        }.getOrNull()?.ifEmpty { initialTranslationCache }?.let {
            this@I18N.translationCache = it.toImmutableMap()
            this@I18N.fetchRequired = false
        }

        getTranslation(key)
    }

    internal suspend fun translation(key: StringResource): String? = translation(key.key)

    /**
     * Reset translation cache to your initial value.
     *
     * Re-fetches your translations on demand.
     */
    fun resetTranslationCache() {
        this.fetchRequired = true
        this.translationCache = initialTranslationCache ?: persistentMapOf()
    }

    /**
     * Fully clear translation cache, you should use [resetTranslationCache] instead.
     *
     * Re-fetches your translations on demand.
     */
    fun clearTranslationCache() {
        this.fetchRequired = true
        this.translationCache = persistentMapOf()
    }

    /**
     * Clears the translation cache.
     *
     * @see clearTranslationCache
     */
    override fun close() {
        scopeCatching {
            fetchTranslationMutex.unlock()
        }
        clearTranslationCache()
    }

    /**
     * Retrieves the translation from cache or resources by default and updates if new translations are available.
     *
     * @param res the [StringResource] used by default.
     * @return [String] from [ContentDelivery] or default [StringResource].
     */
    @Composable
    fun stringResource(res: StringResource): String {
        return produceState<String>(
            get(res) ?: org.jetbrains.compose.resources.stringResource(res)
        ) {
            value = translation(res) ?: get(res) ?: value
        }.value
    }

    /**
     * Retrieves the translation from cache or resources by default and updates if new translations are available.
     *
     * @param res the [StringResource] used by default.
     * @param formatArgs arguments for formatting. (Mostly Java-format and C-sprintf compatible)
     * @return [String] from [ContentDelivery] or default [StringResource].
     */
    @Composable
    fun stringResource(res: StringResource, vararg formatArgs: Any): String {
        return produceState<String>(
            scopeCatching {
                get(res)?.sprintf(*formatArgs)
            }.getOrNull() ?: org.jetbrains.compose.resources.stringResource(res, *formatArgs)
        ) {
            value = suspendCatching {
                translation(res)?.sprintf(*formatArgs)
            }.getOrNull() ?: suspendCatching {
                get(res)?.sprintf(*formatArgs)
            }.getOrNull() ?: value
        }.value
    }

    /**
     * Content delivery feature.
     *
     * @param url the full url to the content delivery (without localization).
     */
    @ConsistentCopyVisibility
    data class ContentDelivery internal constructor(
        val url: String,
        private val json: Json,
    ): CharSequence {

        override val length: Int
            get() = url.length

        override operator fun get(index: Int): Char = url[index]

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = url.subSequence(startIndex, endIndex)

        internal suspend fun fetch(client: HttpClient, locale: Locale): Map<String, String>? {
            suspend fun response(url: String) = suspendCatching {
                client.get(url)
            }.getOrNull()?.let {
                if (it.status.isSuccess()) {
                    it
                } else {
                    null
                }
            }

            val urls = setOfNotNull(
                combineUrlParts(this.url, "${locale.localization}.json"),
                locale.regionCode?.let { combineUrlParts(this.url, "${locale.languageCode}-$it.json") },
                combineUrlParts(this.url, "${locale.languageCode}.json")
            )

            val response = urls.firstNotNullOfOrNull { response(it) } ?: return null
            return suspendCatching {
                response.body<Map<String, String>>()
            }.getOrNull() ?: run {
                val source = suspendCatching {
                    response.readRawBytes().decodeToString()
                }.getOrNull() ?: return null

                suspendCatching {
                    json.decodeFromString<Map<String, String>>(source)
                }.getOrNull()
            }
        }

        class Builder {
            private var fullUrl: String? = null
            private var baseUrl: String = DEFAULT_BASE_URL
            private lateinit var id: String
            private var json: Json = defaultJson

            /**
             * The full url to your content delivery.
             *
             * @param value the url.
             * @return the updated [Builder] instance.
             */
            fun url(value: String) = apply {
                this.fullUrl = value
            }

            /**
             * The base url to your content delivery.
             *
             * Default: [DEFAULT_BASE_URL]
             *
             * Required: [id]
             *
             * @param value the url.
             * @return the updated [Builder] instance.
             */
            fun baseUrl(value: String) = apply {
                this.baseUrl = value
            }

            /**
             * The id of your content delivery.
             * Required if you are using [baseUrl].
             *
             * @param value the url.
             * @return the updated [Builder] instance.
             */
            fun id(value: String) = apply {
                this.id = value
            }

            fun json(value: Json) = apply {
                this.json = value
            }

            fun json(from: Json = defaultJson, builderAction: JsonBuilder.() -> Unit) = apply {
                json(Json(from, builderAction))
            }

            fun build(): ContentDelivery = ContentDelivery(
                url = fullUrl ?: combineUrlParts(baseUrl, id),
                json = json,
            )
        }

        companion object {
            const val DEFAULT_BASE_URL = "https://cdn.tolg.ee/"

            internal val defaultJson = Json {
                isLenient = true
                ignoreUnknownKeys = true
            }
        }
    }

    /**
     * Localization class holding info about the user or used localization.
     *
     * @param localization full localization (like "en-US")
     * @param languageCode code for the language (like "en")
     * @param regionCode code for the region (like "US")
     */
    @Serializable
    @ConsistentCopyVisibility
    data class Locale internal constructor(
        val localization: String,
        val languageCode: String,
        val regionCode: String?
    ) : JvmSerializable {
        class Builder {
            private lateinit var _localization: String
            private lateinit var _languageCode: String
            private var regionCode: String? = null

            /**
             * Full localization.
             *
             * @see Locale.localization
             * @return the updated [Builder] instance.
             */
            fun localization(value: String) = apply {
                this._localization = value

                if (!this::_languageCode.isInitialized) {
                    this._languageCode = value.substringBefore('-').substringBefore('_')
                }
            }

            /**
             * Language code.
             *
             * @see Locale.languageCode
             * @return the updated [Builder] instance.
             */
            fun languageCode(value: String) = apply {
                this._languageCode = value

                if (!this::_localization.isInitialized) {
                    this._localization = value
                }
            }

            /**
             * Region code.
             *
             * @see Locale.regionCode
             * @return the updated [Builder] instance.
             */
            fun regionCode(value: String) = apply {
                this.regionCode = value
            }

            fun build() = Locale(
                localization = _localization,
                languageCode = _languageCode,
                regionCode = regionCode
            )
        }

        companion object
    }

    class Builder {
        private var contentDelivery: ContentDelivery? = null
        private var locale: Locale? = I18N.defaultLocale
        private var client: HttpClient? = null
        private var networkContext: CoroutineContext = I18N.networkDispatcher
        private var translationCache: ImmutableMap<String, String>? = null

        /**
         * Translation content delivery feature.
         *
         * @param value your [ContentDelivery] configuration.
         * @see ContentDelivery
         * @return the updated [Builder] instance.
         */
        fun contentDelivery(value: ContentDelivery) = apply {
            this.contentDelivery = value
        }

        /**
         * Translation content delivery feature.
         *
         * @param block specify your [ContentDelivery] configuration.
         * @see ContentDelivery
         * @return the updated [Builder] instance.
         */
        fun contentDelivery(block: ContentDelivery.Builder.() -> Unit) = apply {
            contentDelivery(ContentDelivery.Builder().apply(block).build())
        }

        /**
         * Translation content delivery feature.
         *
         * @param url specify your [ContentDelivery] full url.
         * @see ContentDelivery
         * @return the updated [Builder] instance.
         */
        @JvmOverloads
        fun contentDelivery(url: String, block: ContentDelivery.Builder.() -> Unit = { }) = apply {
            contentDelivery(ContentDelivery.Builder().url(url).apply(block).build())
        }

        /**
         * The users or used locale, also used for [ContentDelivery].
         *
         * @param value specified locale information.
         * @return the updated [Builder] instance.
         */
        fun locale(value: Locale) = apply {
            this.locale = value
        }

        /**
         * The users or used locale, also used for [ContentDelivery].
         *
         * @param block specify locale information.
         * @return the updated [Builder] instance.
         */
        fun locale(block: Locale.Builder.() -> Unit) = apply {
            locale(Locale.Builder().apply(block).build())
        }

        /**
         * [HttpClient] used for every network request.
         *
         * @param value the Http client to be used.
         * @return the updated [Builder] instance.
         */
        fun client(value: HttpClient) = apply {
            this.client = value
        }

        /**
         * Build [HttpClient] by passing an [HttpClientEngine].
         *
         * @param engine the engine used for the [HttpClient].
         * @see [client]
         * @return the updated [Builder] instance.
         */
        fun client(engine: HttpClientEngine) = client(HttpClient(engine))

        /**
         * Build [HttpClient] by passing an [HttpClientEngineFactory].
         *
         * @param engineFactory the engine factory used for the [HttpClient].
         * @see [client]
         * @return the updated [Builder] instance.
         */
        fun <T : HttpClientEngineConfig> client(engineFactory: HttpClientEngineFactory<T>) = client(HttpClient(engineFactory))

        /**
         * Build [HttpClient] by passing an [HttpClientConfig].
         *
         * @param config the config used for the [HttpClient].
         * @see [client]
         * @return the updated [Builder] instance.
         */
        fun client(config: HttpClientConfig<*>.() -> Unit) = client(HttpClient(config))

        /**
         * Build and configure a [HttpClient].
         *
         * @param engine the engine used for the [HttpClient].
         * @param config the configuration used for the [HttpClient].
         * @see [client]
         * @return the updated [Builder] instance.
         */
        fun client(
            engine: HttpClientEngine,
            config: HttpClientConfig<*>.() -> Unit
        ) = client(HttpClient(engine, config))

        /**
         * Build and configure a [HttpClient].
         *
         * @param engineFactory the engine factory used for the [HttpClient].
         * @param config the configuration used for the [HttpClient].
         * @see [client]
         * @return the updated [Builder] instance.
         */
        fun <T : HttpClientEngineConfig> client(
            engineFactory: HttpClientEngineFactory<T>,
            config: HttpClientConfig<T>.() -> Unit
        ) = client(HttpClient(engineFactory, config))

        /**
         * [CoroutineDispatcher] used for every network request.
         *
         * @param value the [CoroutineDispatcher] used for network requests.
         * @return the updated [Builder] instance.
         */
        fun networkContext(value: CoroutineContext) = apply {
            this.networkContext = value
        }

        /**
         * The initial translation cache.
         *
         * If you fetched some translations before, you can load them into the cache here.
         *
         * @param value the initial [Map] with key-value translation pairs.
         * @return the updated [Builder] instance.
         */
        fun translationCache(value: Map<String, String>) = apply {
            this.translationCache = value.toImmutableMap()
        }

        fun build(): I18N = I18N(
            contentDelivery = contentDelivery,
            locale = locale,
            client = client,
            networkDispatcher = networkDispatcher,
            initialTranslationCache = translationCache
        )
    }

    companion object {
        private fun combineUrlParts(one: String, two: String): String {
            val start = if (one.endsWith('/')) one else "$one/"
            val end = if (two.startsWith('/')) two.substring(1) else two
            return "$start$end"
        }
    }
}
