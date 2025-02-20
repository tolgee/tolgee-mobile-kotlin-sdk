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
}