package io.tolgee.tasks

import io.tolgee.cli.TolgeeCLI
import io.tolgee.common.tolgeeExtension
import io.tolgee.extension.PushExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

open class PushTranslationTask : BaseTolgeeTask() {

    @get:Optional
    @get:InputFile
    open val config: RegularFileProperty = project.objects.fileProperty()

    init {
        description = "Push your translations to Tolgee"
    }

    @TaskAction
    fun push() {
        val success = TolgeeCLI.push(
            config = config.orNull?.asFile,
            output = resolveCLIOutput(),
            logger = logger
        )
        if (!success) {
            throw GradleException("TolgeeCLI: Push failed")
        }
    }

    fun apply(project: Project, extension: PushExtension = project.tolgeeExtension.push) {
        this.apply(extension)

        config.set(extension.config)
    }

    companion object {
        internal const val NAME = "pushTranslation"
    }
}