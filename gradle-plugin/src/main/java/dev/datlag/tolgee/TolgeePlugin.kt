package dev.datlag.tolgee

import dev.datlag.tolgee.common.tolgeeExtension
import dev.datlag.tolgee.tasks.PullTranslationTask
import dev.datlag.tolgee.tasks.PushTranslationTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.maybeCreate

open class TolgeePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.tolgeeExtension

        target.tasks.maybeCreate(PullTranslationTask.NAME, PullTranslationTask::class).also { task ->
            task.apply(target, extension.pull)
        }
        target.tasks.maybeCreate(PushTranslationTask.NAME, PushTranslationTask::class).also { task ->
            task.apply(target, extension.push)
        }

        target.pluginManager.apply(TolgeeCompilerSubPlugin::class.java)
    }

    companion object {
        // Separate variable so it can easily replaced by sed in CI/CD
        private const val PACKAGE_VERSION = "0.1.0"

        val version: String
            get() = this::class.java.`package`?.implementationVersion?.ifBlank { null }
                ?: PACKAGE_VERSION
    }
}