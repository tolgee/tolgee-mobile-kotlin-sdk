package dev.datlag.tolgee.extension

import dev.datlag.tolgee.common.isAndroidOnly
import dev.datlag.tolgee.model.Format
import dev.datlag.tooling.scopeCatching
import dev.datlag.tooling.systemEnv
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.io.File
import java.util.*

open class BaseTolgeeExtension(objectFactory: ObjectFactory) {

    open val fallbackEnabled: Property<Boolean> = objectFactory.property(Boolean::class.java)

    open val apiUrl: Property<String> = objectFactory.property(String::class.java)

    open val projectId: Property<String> = objectFactory.property(String::class.java)

    open val apiKey: Property<String> = objectFactory.property(String::class.java)

    open val format: Property<Format> = objectFactory.property(Format::class.java)

    @JvmOverloads
    open fun setupConvention(project: Project, inherit: BaseTolgeeExtension? = null) {
        if (inherit == null) {
            fallbackEnabled.convention(true)
            apiUrl.convention(DEFAULT_API_URL)
            apiKey.convention(project.provider {
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
            format.convention(project.provider {
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

    companion object {
        const val DEFAULT_API_URL = "https://app.tolgee.io/v2/"
    }
}