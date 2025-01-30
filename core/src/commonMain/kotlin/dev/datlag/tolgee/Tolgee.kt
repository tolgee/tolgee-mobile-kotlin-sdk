package dev.datlag.tolgee

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.systemLocale
import dev.datlag.tolgee.common.createPlatformTolgee
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

open class Tolgee(
    open val config: Config
) {

    @JvmOverloads
    open fun getTranslation(
        key: String,
        locale: Locale = systemLocale,
        vararg args: Any
    ): String? {
        return null
    }

    @ExposedCopyVisibility
    data class Config internal constructor(
        val apiKey: String,
        val apiUrl: String = DEFAULT_API_URL,
        val projectId: String? = null,
    ) {

        class Builder {
            lateinit var apiKey: String
            var apiUrl: String = DEFAULT_API_URL
            var projectId: String? = null

            fun apiKey(apiKey: String) = apply {
                this.apiKey = apiKey
            }

            fun apiUrl(url: String) = apply {
                this.apiUrl = url
            }

            fun projectId(projectId: String?) = apply {
                this.projectId = projectId
            }

            fun build(): Config = Config(
                apiKey = apiKey,
                apiUrl = apiUrl,
                projectId = projectId,
            )
        }

        companion object {
            internal const val DEFAULT_API_URL = "https://app.tolgee.io/v2/"
        }
    }

    companion object {
        @JvmStatic
        fun init(config: Config) = createPlatformTolgee(config)

        @JvmStatic
        fun init(builder: Config.Builder.() -> Unit) = init(Config.Builder().apply(builder).build())
    }
}