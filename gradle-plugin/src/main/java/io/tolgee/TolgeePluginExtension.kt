package io.tolgee

import io.tolgee.extension.CompilerPluginExtension
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.internal.Actions
import org.gradle.util.internal.ConfigureUtil

open class TolgeePluginExtension(objectFactory: ObjectFactory) {

    /**
     * Configuration for handling compiler plugin.
     */
    lateinit var compilerPlugin: CompilerPluginExtension
        private set

    /**
     * Change how the compiler plugin is handled.
     */
    fun compilerPlugin(closure: Closure<in CompilerPluginExtension>): CompilerPluginExtension {
        return compilerPlugin(ConfigureUtil.configureUsing(closure))
    }

    /**
     * Change how the compiler plugin is handled.
     */
    fun compilerPlugin(action: Action<in CompilerPluginExtension>): CompilerPluginExtension {
        return Actions.with(compilerPlugin, action)
    }

    fun setupConvention(project: Project) {
        compilerPlugin = CompilerPluginExtension(project.objects).also {
            it.setupConvention(project)
        }
    }
}