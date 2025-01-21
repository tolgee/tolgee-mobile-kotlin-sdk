package dev.datlag.tolgee.cli

import com.kgit2.kommand.process.Command
import com.kgit2.kommand.process.Stdio
import dev.datlag.tolgee.common.fullPathSafely
import dev.datlag.tooling.getOriginalFile
import dev.datlag.tooling.scopeCatching
import java.io.File

open class Node : PathAware() {

    protected fun NodeCommand(name: String): Command {
        val resolved = NPM.executable(name) ?: Yarn.executable(name) ?: PNPM.executable(name)

        return Command(resolved ?: name)
    }

    interface PackageManager {
        val globalPath: String?

        fun executable(name: String): String? {
            val packageFile = File(globalPath, name)
            val resolvedFile = packageFile.getOriginalFile()

            return resolvedFile.fullPathSafely() ?: packageFile.fullPathSafely()
        }
    }

    private object NPM : PathAware(), PackageManager {
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

            prefix?.trim()?.let { "$it/bin" } ?: scopeCatching {
                Command("npm")
                    .args("bin", "--global")
                    .stdout(Stdio.Pipe)
                    .stderr(Stdio.Null)
                    .output()
                    .stdout
            }.getOrNull()?.ifBlank { null }?.trim()
        }
    }

    private object Yarn : PathAware(), PackageManager {
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

    private object PNPM : PathAware(), PackageManager {
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