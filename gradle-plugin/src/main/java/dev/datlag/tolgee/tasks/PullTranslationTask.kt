package dev.datlag.tolgee.tasks

import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.tolgee.TolgeePluginExtension
import dev.datlag.tolgee.TolgeePluginExtension.Companion.COMMON_RESOURCES_PATH
import dev.datlag.tolgee.api.createTolgee
import dev.datlag.tolgee.cli.TolgeeCLI
import dev.datlag.tolgee.common.androidResources
import dev.datlag.tolgee.common.isAndroidOnly
import dev.datlag.tolgee.common.tolgeeExtension
import dev.datlag.tolgee.extension.BaseTolgeeExtension
import dev.datlag.tolgee.extension.PullExtension
import dev.datlag.tolgee.model.Format
import dev.datlag.tolgee.model.pull.State
import dev.datlag.tooling.async.scopeCatching
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.deleteSafely
import dev.datlag.tooling.existsSafely
import dev.datlag.tooling.mkdirsSafely
import io.ktor.client.engine.okhttp.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.support.unzipTo
import java.io.File
import javax.inject.Inject

open class PullTranslationTask : DefaultTask() {

    @get:Input
    open val apiUrl: Property<String> = project.objects.property(String::class.java)

    @get:Input
    open val projectId: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    open val apiKey: Property<String> = project.objects.property(String::class.java)

    @get:Input
    open val format: Property<Format> = project.objects.property(Format::class.java)

    @get:Optional
    @get:InputDirectory
    open val path: DirectoryProperty = project.objects.directoryProperty()

    @get:Optional
    @get:Input
    open val languages: SetProperty<String> = project.objects.setProperty(String::class.java)

    @get:Optional
    @get:Input
    open val states: SetProperty<State> = project.objects.setProperty(State::class.java)

    @get:Inject
    open val projectLayout = project.layout

    init {
        group = "tolgee"
        description = "Pulls the translations from Tolgee"
    }

    @TaskAction
    fun pull() {
        val apiUrl = apiUrl.getOrElse(BaseTolgeeExtension.DEFAULT_API_URL)
        val projectId = projectId.orNull?.ifBlank { null } ?: return
        val apiKey = apiKey.orNull?.ifBlank { null }
        val format = format.getOrElse(Format.ComposeXML)
        val path = path.orNull?.asFile ?: projectLayout.projectDirectory.dir(PullExtension.COMMON_RESOURCES_PATH).asFile
        val languages = languages.orNull?.mapNotNull { it?.ifBlank { null } }?.ifEmpty { null }
        val states = states.orNull?.mapNotNull { it?.value }?.ifEmpty { null }

        if (TolgeeCLI.installed) {

        } else {

        }
        val ktor = ktorfit {
            baseUrl(apiUrl)
            httpClient(OkHttp) {
                followRedirects = true
            }
        }
        val tolgee = ktor.createTolgee()
        val outputDir = projectLayout.buildDirectory.dir("tolgee")

        runBlocking {
            val response = tolgee.export(
                apiKey = key,
                id = id,
                format = format.value,
                languages = lang,
                filterState = filter,
                zip = true
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

                val composeResDir = destination.orNull?.asFile ?: projectLayout.projectDirectory.dir(COMMON_RESOURCES_PATH).asFile

                composeResDir.mkdirsSafely()
                unzipTo(composeResDir, outputFile)
                outputFile.deleteSafely()
            } else {
                logger.warn("Translation zip could not be downloaded")
            }
        }
    }

    private fun cliPulling(
        apiUrl: String?,
        projectId: String,
        apiKey: String?,
        format: Format
    ) {
        TolgeeCLI.pull(
            apiKey = apiUrl,
            projectId = projectId,
            path = ,
            format = format.value,
            languages =
        )
    }

    fun apply(project: Project, extension: PullExtension = project.tolgeeExtension.pull) {
        apiUrl.set(extension.apiUrl)
        projectId.set(extension.projectId)
        apiKey.set(extension.apiKey)
        format.set(extension.format)
        languages.set(extension.languages)
        states.set(extension.states)
        path.set(extension.path)
    }

    companion object {
        internal const val NAME = "pullTranslation"
    }
}