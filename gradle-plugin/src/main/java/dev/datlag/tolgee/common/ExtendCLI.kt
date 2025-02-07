package dev.datlag.tolgee.common

import com.kgit2.kommand.io.Output
import dev.datlag.tolgee.model.CLIOutput
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

internal fun Output.handleOutput(config: CLIOutput, logger: Logger): Output = this.also {
    val stdout = it.stdout?.ifBlank { null }
    val stderr = it.stderr?.ifBlank { null }

    when (config) {
        is CLIOutput.Print -> {
            if (!stdout.isNullOrBlank()) {
                println(stdout)
                println()
            }
            if (!stderr.isNullOrBlank()) {
                println(stderr)
                println()
            }
        }
        is CLIOutput.Log -> {
            if (!stdout.isNullOrBlank()) {
                logger.log(config.level, stdout)
            }
            if (!stderr.isNullOrBlank()) {
                val errorLevel = if (config.overrideError) config.level else LogLevel.ERROR

                logger.log(errorLevel, stderr)
            }
        }
        else -> { }
    }
}