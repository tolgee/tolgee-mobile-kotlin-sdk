package io.tolgee

import io.tolgee.common.tolgeeExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

open class TolgeePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tolgeeExtension
        target.pluginManager.apply(TolgeeCompilerSubPlugin::class.java)
    }

    companion object {
        // Separate variable so it can easily be replaced by sed in CI/CD
        private const val PACKAGE_VERSION = "1.0.0-alpha01"

        val version: String
            get() = this::class.java.`package`?.implementationVersion?.ifBlank { null }
                ?: PACKAGE_VERSION
    }
}