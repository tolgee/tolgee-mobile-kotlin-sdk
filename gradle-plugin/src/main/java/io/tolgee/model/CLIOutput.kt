package io.tolgee.model

import com.kgit2.kommand.process.Stdio
import org.gradle.api.logging.LogLevel

/**
 * Configure the output behavior of CLI tasks.
 */
sealed interface CLIOutput {

    val io: Stdio

    /**
     * Inherits the default behavior of your environment.
     *
     * May print result but not always.
     */
    object Default : CLIOutput {
        override val io: Stdio = Stdio.Inherit
    }

    /**
     * Explicitly prints the result.
     */
    object Print : CLIOutput {
        override val io: Stdio = Stdio.Pipe
    }

    /**
     * Explicitly logs the result.
     */
    data class Log @JvmOverloads constructor(
        val level: LogLevel,
        val overrideError: Boolean = false,
    ) : CLIOutput {
        override val io: Stdio = Stdio.Pipe
    }

    /**
     * Explicitly clears result from printing.
     */
    object None : CLIOutput {
        override val io: Stdio = Stdio.Null
    }

}