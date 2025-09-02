package io.tolgee

import io.tolgee.common.tolgeeExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

open class TolgeePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tolgeeExtension

        // Unavailable right now, as it's broken after Kotlin 2.2.0 (and even on 2.1.20)
        // target.pluginManager.apply(TolgeeCompilerSubPlugin::class.java)
    }

    companion object {
        // Separate variable so it can easily be replaced by sed in CI/CD
        private const val PACKAGE_VERSION = "1.0.0-alpha02"

        val version: String
            get() = this::class.java.`package`?.implementationVersion?.ifBlank { null }
                ?: PACKAGE_VERSION
    }
}