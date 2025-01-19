package dev.datlag.tolgee.tasks

import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.tolgee.TolgeePluginExtension
import dev.datlag.tolgee.TolgeePluginExtension.Companion.COMMON_RESOURCES_PATH
import dev.datlag.tolgee.api.createTolgee
import dev.datlag.tolgee.common.androidResources
import dev.datlag.tolgee.common.isAndroidOnly
import dev.datlag.tolgee.common.tolgeeExtension
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
    open val baseUrl: Property<String> = project.objects.property(String::class.java)

    @get:Input
    open val projectId: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    open val languages: SetProperty<String> = project.objects.setProperty(String::class.java)

    @get:Optional
    @get:Input
    open val filterState: SetProperty<TolgeePluginExtension.FilterState> = project.objects.setProperty(TolgeePluginExtension.FilterState::class.java)

    @get:Input
    open val apiKey: Property<String> = project.objects.property(String::class.java)

    @get:Input
    open val type: Property<TolgeePluginExtension.PullType> = project.objects.property(TolgeePluginExtension.PullType::class.java)

    @get:Optional
    @get:InputDirectory
    open val destination: DirectoryProperty = project.objects.directoryProperty()

    @get:Inject
    open val projectLayout = project.layout

    init {
        group = "tolgee"
        description = "Pulls the translations from Tolgee"
    }

    @TaskAction
    fun pull() {
        val id = projectId.orNull?.ifBlank { null } ?: return
        val key = apiKey.orNull?.ifBlank { null } ?: return
        val lang = languages.orNull?.mapNotNull { it?.ifBlank { null } }?.ifEmpty { null }
        val filter = filterState.orNull?.mapNotNull { it?.value }?.ifEmpty { null }
        val ktor = ktorfit {
            baseUrl(baseUrl.getOrElse(TolgeePluginExtension.DEFAULT_URL))
            httpClient(OkHttp) {
                followRedirects = true
            }
        }
        val tolgee = ktor.createTolgee()
        val outputDir = projectLayout.buildDirectory.dir("tolgee")
        val format = type.getOrElse(TolgeePluginExtension.PullType.ComposeXML)

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
                scopeCatching {
                    outputDirFile.mkdirsSafely()
                }

                val outputFile = File(outputDirFile, "translation.zip")

                suspendCatching {
                    outputFile.writeBytes(response.readRawBytes())
                }.onFailure {
                    logger.warn("Could not read translation zip file.")
                    return@runBlocking
                }

                val composeResDir = destination.orNull?.asFile ?: projectLayout.projectDirectory.dir(COMMON_RESOURCES_PATH).asFile
                scopeCatching {
                    composeResDir.mkdirsSafely()
                }

                unzipTo(composeResDir, outputFile)

                scopeCatching {
                    outputFile.deleteSafely()
                }
            } else {
                logger.warn("Translation zip could not be downloaded")
            }
        }
    }

    fun apply(project: Project, extension: TolgeePluginExtension = project.tolgeeExtension) {
        baseUrl.set(extension.baseUrl)
        projectId.set(extension.projectId)
        languages.set(extension.languages)
        filterState.set(extension.filterState)
        apiKey.set(extension.apiKey)
        type.set(extension.pullType)
        destination.set(extension.pullDestination)
    }

    companion object {
        internal const val NAME = "pullTranslation"
    }
}