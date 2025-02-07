package dev.datlag.tolgee.tasks

import dev.datlag.tolgee.extension.BaseTolgeeExtension
import dev.datlag.tolgee.model.CLIOutput
import dev.datlag.tolgee.model.Format
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

abstract class BaseTolgeeTask : DefaultTask() {

    @get:Optional
    @get:Input
    open val fallbackEnabled: Property<Boolean> = project.objects.property(Boolean::class.java)

    @get:Optional
    @get:Input
    open val cliOutput: Property<CLIOutput> = project.objects.property(CLIOutput::class.java)

    @get:Optional
    @get:Input
    open val apiUrl: Property<String> = project.objects.property(String::class.java)

    @get:Input
    open val projectId: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    open val apiKey: Property<String> = project.objects.property(String::class.java)

    @get:Input
    open val format: Property<Format> = project.objects.property(Format::class.java)

    init {
        group = "tolgee"
    }

    open fun resolveFallbackEnabled(): Boolean? {
        return fallbackEnabled.orNull
    }

    open fun resolveCLIOutput(): CLIOutput {
        return cliOutput.orNull ?: CLIOutput.Default
    }

    open fun resolveFallbackEnabled(default: Boolean): Boolean {
        return resolveFallbackEnabled() ?: default
    }

    open fun resolveApiUrl(): String? {
        return apiUrl.orNull?.ifBlank { null }?.let {
            if (it == BaseTolgeeExtension.DEFAULT_API_URL || "$it/" == BaseTolgeeExtension.DEFAULT_API_URL) {
                null // Let the CLI handle the base URL
            } else {
                it
            }
        }
    }

    open fun resolveApiUrl(default: String): String {
        return resolveApiUrl() ?: default
    }

    open fun resolveProjectId(): String? {
        return projectId.orNull?.ifBlank { null }
    }

    open fun resolveProjectId(default: String): String {
        return resolveProjectId() ?: default
    }

    open fun resolveApiKey(): String? {
        return apiKey.orNull?.ifBlank { null }
    }

    open fun resolveApiKey(default: String): String {
        return resolveApiKey() ?: default
    }

    open fun resolveFormat(): Format? {
        return format.orNull?.ifBlank { null }
    }

    open fun resolveFormat(default: Format): Format {
        return resolveFormat() ?: default
    }

    protected fun <T : BaseTolgeeExtension> apply(extension: T) {
        fallbackEnabled.set(extension.fallbackEnabled)
        cliOutput.set(extension.cliOutput)
        apiUrl.set(extension.apiUrl)
        projectId.set(extension.projectId)
        apiKey.set(extension.apiKey)
        format.set(extension.format)
    }
}