package io.tolgee.tasks

import io.tolgee.extension.BaseTolgeeExtension
import io.tolgee.model.CLIOutput
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

abstract class BaseTolgeeTask : DefaultTask() {

    @get:Optional
    @get:Input
    open val cliOutput: Property<CLIOutput> = project.objects.property(CLIOutput::class.java)

    init {
        group = "tolgee"
    }

    open fun resolveCLIOutput(): CLIOutput {
        return cliOutput.orNull ?: CLIOutput.Default
    }

    protected fun <T : BaseTolgeeExtension> apply(extension: T) {
        cliOutput.set(extension.cliOutput)
    }
}