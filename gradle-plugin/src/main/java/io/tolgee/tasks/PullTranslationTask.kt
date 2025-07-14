package io.tolgee.tasks

import io.tolgee.cli.TolgeeCLI
import io.tolgee.common.tolgeeExtension
import io.tolgee.extension.PullExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

open class PullTranslationTask : BaseTolgeeTask() {

    @get:Optional
    @get:InputFile
    open val config: RegularFileProperty = project.objects.fileProperty()

    init {
        description = "Pulls the translations from Tolgee"
    }

    @TaskAction
    fun pull() {
        val success = TolgeeCLI.pull(
            config = config.orNull?.asFile,
            output = resolveCLIOutput(),
            logger = logger
        )
        if (!success) {
            throw GradleException("TolgeeCLI: Pull failed")
        }
    }

    fun apply(project: Project, extension: PullExtension = project.tolgeeExtension.pull) {
        this.apply(extension)

        config.set(extension.config)
    }

    companion object {
        internal const val NAME = "pullTranslation"
    }
}