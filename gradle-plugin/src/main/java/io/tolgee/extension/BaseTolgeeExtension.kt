package io.tolgee.extension

import io.tolgee.model.CLIOutput
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

/**
 * Base class for configuring global options for the Tolgee Gradle plugin.
 *
 * This class provides common configuration properties that can be shared across
 * Gradle tasks or extensions inheriting from it. It allows setting up essential
 * options for interacting with the Tolgee CLI.
 */
open class BaseTolgeeExtension(objectFactory: ObjectFactory) {

    open val config: RegularFileProperty = objectFactory.fileProperty()

    open val cliOutput: Property<CLIOutput> = objectFactory.property(CLIOutput::class.java)

    /**
     * Configures default values for the extensions properties by inheriting from another instance
     * or reading available information from the Gradle project.
     *
     * @param project The Gradle [Project] instance used to extract information, such as properties
     *                or environment variables, if no inheritance instance is provided.
     * @param inherit An optional [BaseTolgeeExtension] instance from which default values can be inherited.
     *                If `null`, defaults will be derived from the [project].
     */
    open fun setupConvention(project: Project, inherit: BaseTolgeeExtension? = null) {
        config.convention(null)

        if (inherit == null) {
            cliOutput.convention(CLIOutput.Default)
            return
        }

        cliOutput.convention(inherit.cliOutput)
    }
}