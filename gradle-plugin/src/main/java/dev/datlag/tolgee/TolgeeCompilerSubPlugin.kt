package dev.datlag.tolgee

import dev.datlag.tolgee.common.tolgeeExtension
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class TolgeeCompilerSubPlugin : KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider {
            val config = kotlinCompilation.target.project.tolgeeExtension.compilerPlugin

            listOf(
                SubpluginOption("tolgee.android.getString", config.android.replaceGetString.getOrElse(true).toString())
            )
        }
    }

    override fun getCompilerPluginId(): String = PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = GROUP_NAME,
            artifactId = ARTIFACT
        )
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    companion object {
        private const val GROUP_NAME = "dev.datlag.tolgee"
        private const val ARTIFACT = "compiler-plugin"
        private const val PLUGIN_ID = "tolgee"
    }
}