package io.tolgee.tasks

import de.jensklingenberg.ktorfit.ktorfit
import io.tolgee.api.TolgeeApi
import io.tolgee.cli.TolgeeCLI
import io.tolgee.common.tolgeeExtension
import io.tolgee.extension.BaseTolgeeExtension
import io.tolgee.extension.PullExtension
import io.tolgee.model.pull.State
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.deleteSafely
import dev.datlag.tooling.mkdirsSafely
import io.ktor.client.engine.okhttp.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.tolgee.api.createTolgeeRequests
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.support.unzipTo
import java.io.File
import javax.inject.Inject

open class PullTranslationTask : BaseTolgeeTask() {

    @get:Optional
    @get:InputFile
    open val config: RegularFileProperty = project.objects.fileProperty()

    @get:Optional
    @get:InputDirectory
    open val path: DirectoryProperty = project.objects.directoryProperty()

    @get:Optional
    @get:Input
    open val languages: SetProperty<String> = project.objects.setProperty(String::class.java)

    @get:Optional
    @get:Input
    open val states: SetProperty<State> = project.objects.setProperty(State::class.java)

    @get:Optional
    @get:Input
    open val namespaces: SetProperty<String> = project.objects.setProperty(String::class.java)

    @get:Optional
    @get:Input
    open val tags: SetProperty<String> = project.objects.setProperty(String::class.java)

    @get:Optional
    @get:Input
    open val excludeTags: SetProperty<String> = project.objects.setProperty(String::class.java)

    @get:Inject
    open val projectLayout = project.layout

    init {
        description = "Pulls the translations from Tolgee"
    }

    @TaskAction
    fun pull() {
        val apiUrl = resolveApiUrl()
        val projectId = resolveProjectId()
        val apiKey = resolveApiKey()
        val format = resolveFormat()
        val path = path.orNull?.asFile ?: projectLayout.projectDirectory.dir(PullExtension.COMMON_RESOURCES_PATH).asFile
        val languages = languages.orNull?.mapNotNull { it?.ifBlank { null } }
        val states = states.orNull?.filterNotNull()
        val namespaces = namespaces.orNull?.mapNotNull { it?.ifBlank { null } }
        val tags = tags.orNull?.mapNotNull { it?.ifBlank { null } }
        val excludeTags = excludeTags.orNull?.mapNotNull { it?.ifBlank { null } }

        val cliSuccessful = TolgeeCLI.pull(
            apiUrl = apiUrl,
            projectId = projectId,
            apiKey = apiKey,
            format = format,
            path = path,
            config = config.orNull?.asFile,
            languages = languages,
            states = states,
            namespaces = namespaces,
            tags = tags,
            excludeTags = excludeTags,
            output = resolveCLIOutput(),
            logger = logger
        )
        val useFallback = resolveFallbackEnabled(true) && !cliSuccessful

        if (useFallback) {
            logger.warn("Could not use CLI, falling back to REST API.")
            val requiredApiKey = apiKey ?: return logger.error("No API Key provided.")
            val requiredApiUrl = apiUrl ?: BaseTolgeeExtension.DEFAULT_API_URL
            val ktor = ktorfit {
                baseUrl(requiredApiUrl)
                httpClient(OkHttp) {
                    followRedirects = true
                }
            }
            val tolgee = ktor.createTolgeeRequests()
            val outputDir = projectLayout.buildDirectory.dir("tolgee")

            runBlocking {
                val response = TolgeeApi.pull(
                    api = tolgee,
                    apiKey = requiredApiKey,
                    projectId = projectId,
                    format = format ?: return@runBlocking logger.error("Please specify a format to use pulling."),
                    languages = languages,
                    states = states,
                    namespaces = namespaces,
                    tags = tags,
                    excludeTags = excludeTags,
                )

                if (response.status.isSuccess()) {
                    val outputDirFile = outputDir.get().asFile
                    outputDirFile.mkdirsSafely()

                    val outputFile = File(outputDirFile, "translation.zip")
                    outputFile.deleteSafely()

                    suspendCatching {
                        outputFile.writeBytes(response.readRawBytes())
                    }.onFailure {
                        logger.warn("Could not read translation zip file.")
                        return@runBlocking
                    }

                    path.mkdirsSafely()
                    unzipTo(path, outputFile)
                    outputFile.deleteSafely()
                } else {
                    logger.warn("Translation zip could not be downloaded")
                }
            }
        }
    }

    fun apply(project: Project, extension: PullExtension = project.tolgeeExtension.pull) {
        this.apply(extension)

        config.set(extension.config)
        path.set(extension.path)
        languages.set(extension.languages)
        states.set(extension.states)
        namespaces.set(extension.namespaces)
        tags.set(extension.tags)
        excludeTags.set(extension.excludeTags)
    }

    companion object {
        internal const val NAME = "pullTranslation"
    }
}