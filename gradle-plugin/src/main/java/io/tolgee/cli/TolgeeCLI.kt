package io.tolgee.cli

import io.tolgee.common.fullPathSafely
import io.tolgee.common.handleOutput
import io.tolgee.model.CLIOutput
import dev.datlag.tooling.async.scopeCatching
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import org.gradle.api.logging.Logger
import java.io.File

internal object TolgeeCLI : Node() {

    private const val app = "tolgee"

    val supportedMinVersion = "2.0.0".toVersion()
    val installed: Boolean by lazy {
        version()?.let { v -> v >= supportedMinVersion } ?: false
    }

    fun version(): Version? = scopeCatching {
        NodeCommand(app)
            .arg("--version")
            .output()
            .stdout
    }.getOrNull()?.ifBlank { null }?.trim()?.toVersionOrNull(strict = false)

    /**
     * Pull translations with Tolgee CLI.
     *
     * @return true if the command was successful.
     */
    fun pull(
        config: File?,
        output: CLIOutput,
        logger: Logger
    ): Boolean = installed && scopeCatching {
        NodeCommand(app)
            .arg("pull")
            .apply {
                config?.fullPathSafely()?.let {
                    args("--config", it)
                }
            }
            .stdout(output.io)
            .spawn()
            .waitWithOutput()
            .handleOutput(output, logger)
    }.getOrNull()?.status == 0

    fun push(
        config: File?,
        output: CLIOutput,
        logger: Logger
    ): Boolean = installed && scopeCatching {
        NodeCommand(app)
            .arg("push")
            .apply {
                config?.fullPathSafely()?.let {
                    args("--config", it)
                }
            }
            .stdout(output.io)
            .spawn()
            .waitWithOutput()
            .handleOutput(output, logger)
    }.getOrNull()?.status == 0

}
