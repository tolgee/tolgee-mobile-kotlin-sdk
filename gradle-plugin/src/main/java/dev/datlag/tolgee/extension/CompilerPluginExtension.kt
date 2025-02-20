package dev.datlag.tolgee.extension

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.internal.Actions
import org.gradle.util.internal.ConfigureUtil

/**
 * Gradle extension for configuring the Tolgee Kotlin compiler plugin.
 *
 * This extension allows users to configure the plugin via their `build.gradle(.kts)` file.
 * It provides an [android] configuration block for Android-specific settings.
 *
 * @param objectFactory The Gradle [ObjectFactory] used to create properties.
 */
open class CompilerPluginExtension(objectFactory: ObjectFactory) {

    /**
     * Android-specific configuration for the compiler plugin.
     *
     * This field is initialized in [setupConvention] and provides options
     * related to Android projects.
     */
    lateinit var android: AndroidExtension
        private set

    /**
     * Configures the Android extension using a Groovy-style DSL closure.
     *
     * This method allows users to configure Android settings in `build.gradle` using Groovy.
     *
     * @param closure The configuration closure.
     * @return The configured [AndroidExtension] instance.
     */
    fun android(closure: Closure<in AndroidExtension>): AndroidExtension {
        return android(ConfigureUtil.configureUsing(closure))
    }

    /**
     * Configures the Android extension using a Kotlin-style [Action].
     *
     * This method allows users to configure Android settings in `build.gradle.kts` using Kotlin DSL.
     *
     * @param action The configuration action.
     * @return The configured [AndroidExtension] instance.
     */
    fun android(action: Action<in AndroidExtension>): AndroidExtension {
        return Actions.with(android, action)
    }

    /**
     * Initializes the Android extension and applies default conventions.
     *
     * This method is called internally to set up default values for Android-related options.
     *
     * @param project The Gradle project instance.
     */
    internal fun setupConvention(project: Project) {
        android = AndroidExtension(project.objects).also {
            it.setupConvention(project)
        }
    }

    /**
     * Android-specific configuration options for the compiler plugin.
     *
     * This class provides options that affect how the Kotlin compiler plugin interacts with
     * Android projects, such as whether `getString` replacements should be enabled.
     *
     * @param objectFactory The Gradle [ObjectFactory] used to create properties.
     */
    open class AndroidExtension(objectFactory: ObjectFactory) {

        /**
         * Controls whether `Context.getString` calls should be replaced.
         *
         * - `true` (default): Replaces `getString` calls with Tolgee localization method.
         * - `false`: Leaves `getString` calls unchanged.
         */
        open val replaceGetString: Property<Boolean> = objectFactory.property(Boolean::class.java)

        /**
         * Sets up default values for the Android extension.
         *
         * This method ensures that `replaceGetString` is enabled by default.
         *
         * @param project The Gradle project instance.
         */
        internal fun setupConvention(project: Project) {
            replaceGetString.convention(true)
        }

    }

}