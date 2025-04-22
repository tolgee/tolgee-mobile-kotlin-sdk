package io.tolgee.tasks

import io.tolgee.cli.TolgeeCLI
import io.tolgee.common.tolgeeExtension
import io.tolgee.extension.PushExtension
import io.tolgee.model.push.Mode
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

open class PushTranslationTask : BaseTolgeeTask() {

    @get:Optional
    @get:InputFile
    open val config: RegularFileProperty = project.objects.fileProperty()

    @get:Optional
    @get:Input
    open val forceMode: Property<Mode> = project.objects.property(Mode::class.java)

    @get:Optional
    @get:Input
    open val languages: SetProperty<String> = project.objects.setProperty(String::class.java)

    @get:Optional
    @get:Input
    open val namespaces: SetProperty<String> = project.objects.setProperty(String::class.java)

    init {
        description = "Push your translations to Tolgee"
    }

    @TaskAction
    fun push() {
        val apiUrl = resolveApiUrl()
        val projectId = resolveProjectId()
        val apiKey = resolveApiKey()
        val format = resolveFormat()
        val mode = forceMode.getOrElse(Mode.NoForce)
        val languages = languages.orNull?.mapNotNull { it?.ifBlank { null } }
        val namespaces = namespaces.orNull?.mapNotNull { it?.ifBlank { null } }

        val cliSuccessful = TolgeeCLI.push(
            apiUrl = apiUrl,
            projectId = projectId,
            apiKey = apiKey,
            format = format,
            mode = mode,
            config = config.orNull?.asFile,
            languages = languages,
            namespaces = namespaces,
            output = resolveCLIOutput(),
            logger = logger
        )
        val useFallback = resolveFallbackEnabled(true) && !cliSuccessful

        if (useFallback) {
            logger.error("Could not use CLI and REST API fallback isn't implemented yet.")
        }
    }

    fun apply(project: Project, extension: PushExtension = project.tolgeeExtension.push) {
        this.apply(extension)

        config.set(extension.config)
        forceMode.set(extension.forceMode)
        languages.set(extension.languages)
        namespaces.set(extension.namespaces)
    }

    companion object {
        internal const val NAME = "pushTranslation"
    }

}