package dev.datlag.tolgee.cli

import com.kgit2.kommand.process.Stdio
import dev.datlag.tooling.scopeCatching
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import java.io.File

object TolgeeCLI : Node() {

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
     * @param apiKey Can be a Personal Access Token or a Project API Key. May be omitted if you are logged in.
     * @param projectId Project ID on the Tolgee server.
     * @param format Localization files format.
     * @param languages List of languages to pull. Leave unspecified to export all languages.
     * @param states List of translation states to include.
     * @return true if command was successful.
     */
    fun pull(
        apiKey: String?,
        projectId: String,
        path: File,
        format: String,
        languages: Set<String>,
        states: Set<String>,
    ): Boolean = installed && scopeCatching {
        NodeCommand(app)
            .arg("pull")
            .apply {
                if (!apiKey.isNullOrBlank()) {
                    args("--api-key", apiKey)
                }
            }
            .args("--project-id", projectId)
            .args("--path", path.path)
            .args("--format", format)
            .apply {
                if (languages.isNotEmpty()) {
                    args("--languages", languages.joinToString(" "))
                }
                if (states.isNotEmpty()) {
                    args("--states", states.joinToString(" "))
                }
            }
            .stdout(Stdio.Inherit)
            .spawn()
            .waitWithOutput()
    }.getOrNull()?.status == 0

}
