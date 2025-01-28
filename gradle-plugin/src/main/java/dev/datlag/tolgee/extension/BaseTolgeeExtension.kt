package dev.datlag.tolgee.extension

import dev.datlag.tolgee.common.isAndroidOnly
import dev.datlag.tolgee.common.lazyMap
import dev.datlag.tolgee.model.Configuration
import dev.datlag.tolgee.model.Format
import dev.datlag.tooling.existsSafely
import dev.datlag.tooling.scopeCatching
import dev.datlag.tooling.systemEnv
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.io.File
import java.util.*

/**
 * Base class for configuring global options for the Tolgee Gradle plugin.
 *
 * This class provides common configuration properties that can be shared across
 * Gradle tasks or extensions inheriting from it. It allows setting up essential
 * options for interacting with the Tolgee CLI or REST API.
 *
 * @property fallbackEnabled Determines whether the REST API is used as a fallback if the CLI fails.
 *                           Default is `true`.
 * @property apiUrl The base URL for the Tolgee API.
 * @property projectId The unique identifier for the Tolgee project.
 * @property apiKey The API key used to authenticate with Tolgee. The plugin will attempt to resolve
 *                  this value using default properties or a system environment variable if not explicitly provided.
 * @property format The format used by Tolgee for localization files. For example: `COMPOSE_XML` or `ANDROID_XML`.
 */
open class BaseTolgeeExtension(objectFactory: ObjectFactory) {

    open val config: RegularFileProperty = objectFactory.fileProperty()

    internal val configuration: Property<Configuration> = objectFactory.property(Configuration::class.java)

    open val fallbackEnabled: Property<Boolean> = objectFactory.property(Boolean::class.java)

    open val apiUrl: Property<String> = objectFactory.property(String::class.java)

    open val projectId: Property<String> = objectFactory.property(String::class.java)

    open val apiKey: Property<String> = objectFactory.property(String::class.java)

    open val format: Property<Format> = objectFactory.property(Format::class.java)

    /**
     * Configures default values for the extensions properties by inheriting from another instance
     * or reading available information from the Gradle project.
     *
     * @param project The Gradle [Project] instance used to extract information, such as properties
     *                or environment variables, if no inheritance instance is provided.
     * @param inherit An optional [BaseTolgeeExtension] instance from which default values can be inherited.
     *                If `null`, defaults will be derived from the [project].
     */
    @JvmOverloads
    open fun setupConvention(project: Project, inherit: BaseTolgeeExtension? = null) {
        if (inherit == null) {
            config.convention(project.provider { defaultConfigurationFile(project) })
            configuration.convention(config.lazyMap(
                project = project,
                map = { it?.let(Configuration::from) }
            ))
            fallbackEnabled.convention(true)
            apiUrl.convention(configuration.map { it.apiUrl ?: "" })
            projectId.convention(configuration.map { it.projectId ?: "" })
            apiKey.convention(configuration.lazyMap(
                project = project,
                map = { it?.apiKey }
            ) {
                project.findProperty("tolgee.apikey")?.toString()?.ifBlank { null }
                    ?: project.findProperty("tolgee.apiKey")?.toString()?.ifBlank { null }
                    ?: project.findProperty("tolgee.api-key")?.toString()?.ifBlank { null }
                    ?: systemEnv("TOLGEE_API_KEY")?.ifBlank { null }
                    ?: run {
                        val projectLocal = project.layout.projectDirectory.file("local.properties").asFile
                        val rootLocal = project.rootProject.layout.projectDirectory.file("local.properties").asFile

                        loadProperties(projectLocal)?.let { props ->
                            props.getProperty("tolgee.apikey")?.ifBlank { null }
                                ?: props.getProperty("tolgee.apiKey")?.ifBlank { null }
                                ?: props.getProperty("tolgee.api-key")?.ifBlank { null }
                        } ?: loadProperties(rootLocal)?.let { props ->
                            props.getProperty("tolgee.apikey")?.ifBlank { null }
                                ?: props.getProperty("tolgee.apiKey")?.ifBlank { null }
                                ?: props.getProperty("tolgee.api-key")?.ifBlank { null }
                        }
                    }
            })
            format.convention(configuration.lazyMap(
                project = project,
                map = { it?.format }
            ) {
                if (project.isAndroidOnly) {
                    Format.AndroidXML
                } else {
                    Format.ComposeXML
                }
            })
        } else {
            setupConvention(inherit)
        }
    }

    private fun setupConvention(inherit: BaseTolgeeExtension) {
        configuration.convention(inherit.configuration)
        fallbackEnabled.convention(inherit.fallbackEnabled)
        apiUrl.convention(inherit.apiUrl)
        projectId.convention(inherit.projectId)
        apiKey.convention(inherit.apiKey)
        format.convention(inherit.format)
    }

    protected fun loadProperties(file: File): Properties? {
        val stream = scopeCatching {
            file.inputStream()
        }.getOrNull() ?: return null

        return scopeCatching {
            stream.use { stream ->
                Properties().apply { load(stream) }
            }
        }.getOrNull().also {
            scopeCatching {
                stream.close()
            }
        }
    }

    private fun defaultConfigurationFile(project: Project): RegularFile? {
        val supportedNames = setOf(
            ".tolgeerc",
            ".tolgeerc.json",
            ".tolgeerc.yaml",
            ".tolgeerc.yml",

            ".config/tolgeerc",
            ".config/tolgeerc.json",
            ".config/tolgeerc.yaml",
            ".config/tolgeerc.yml",

            "tolgee.config"
        )

        return supportedNames.map { name ->
            project.layout.projectDirectory.file(name)
        }.firstOrNull { it.asFile.existsSafely() } ?: supportedNames.map { name ->
            project.rootProject.layout.projectDirectory.file(name)
        }.firstOrNull { it.asFile.existsSafely() }
    }

    companion object {
        const val DEFAULT_API_URL = "https://app.tolgee.io/v2/"
    }
}