package io.tolgee.cli

import com.kgit2.kommand.process.Command
import com.kgit2.kommand.process.Stdio
import io.tolgee.common.fullPathSafely
import dev.datlag.tooling.getOriginalFile
import dev.datlag.tooling.async.scopeCatching
import java.io.File

internal open class Node : PathAware() {

    private val packageManagers = setOf(
        NPM,
        Yarn,
        PNPM
    )

    /**
     * Tries to find the executable in package managers on system.
     *
     * Prefers executables available in system PATH and falls back to any package manager and finally locally.
     */
    protected fun NodeCommand(name: String): Command {
        val resolved = packageManagers.firstNotNullOfOrNull {
            if (it.globalPathInSystemPath) {
                it.executable(name)
            } else {
                null
            }
        } ?: systemPathExecutable(name) ?: packageManagers.firstNotNullOfOrNull { it.executable(name) }

        return Command(resolved ?: name)
    }

    abstract class PackageManager : PathAware() {
        abstract val globalPath: String?
        val globalPathInSystemPath: Boolean by lazy {
            globalPath?.let { systemPathContains(it) } ?: false
        }

        fun executable(name: String): String? {
            val packageFile = File(globalPath, name)
            val resolvedFile = packageFile.getOriginalFile()

            return resolvedFile.fullPathSafely() ?: packageFile.fullPathSafely()
        }
    }

    private object NPM : PackageManager() {
        override val globalPath: String? by lazy {
            val prefix = scopeCatching {
                Command("npm")
                    .args("config", "get", "prefix")
                    .stdout(Stdio.Pipe)
                    .stderr(Stdio.Null)
                    .output()
                    .stdout
            }.getOrNull()?.ifBlank { null } ?: scopeCatching {
                Command("npm")
                    .args("get", "prefix")
                    .stdout(Stdio.Pipe)
                    .stderr(Stdio.Null)
                    .output()
                    .stdout
            }.getOrNull()?.ifBlank { null }

            prefix?.trim()?.let { "$it${filePathDelimiter}bin" } ?: scopeCatching {
                Command("npm")
                    .args("bin", "--global")
                    .stdout(Stdio.Pipe)
                    .stderr(Stdio.Null)
                    .output()
                    .stdout
            }.getOrNull()?.ifBlank { null }?.trim()
        }
    }

    private object Yarn : PackageManager() {
        override val globalPath: String? by lazy {
            scopeCatching {
                Command("yarn")
                    .args("global", "bin")
                    .stdout(Stdio.Pipe)
                    .stderr(Stdio.Null)
                    .output()
                    .stdout
            }.getOrNull()?.ifBlank { null }?.trim()
        }
    }

    private object PNPM : PackageManager() {
        override val globalPath: String? by lazy {
            scopeCatching {
                Command("pnpm")
                    .args("bin", "--global")
                    .stdout(Stdio.Pipe)
                    .stderr(Stdio.Null)
                    .output()
                    .stdout
            }.getOrNull()?.ifBlank { null }?.trim()
        }
    }
}