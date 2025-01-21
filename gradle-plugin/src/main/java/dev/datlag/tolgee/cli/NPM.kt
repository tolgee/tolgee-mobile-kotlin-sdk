package dev.datlag.tolgee.cli

import com.kgit2.kommand.process.Command
import com.kgit2.kommand.process.Stdio
import dev.datlag.tooling.existsSafely
import dev.datlag.tooling.getOriginalFile
import dev.datlag.tooling.scopeCatching
import java.io.File

open class NPM : PathAware() {

    val globalBinPath: String? by lazy {
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
        }.getOrNull()?.ifBlank { null } ?: scopeCatching {
            Command("npm")
                .args("bin", "--global")
                .stdout(Stdio.Pipe)
                .stderr(Stdio.Null)
                .output()
                .stdout
        }.getOrNull()?.ifBlank { null }

        prefix?.trim()?.let { "$it/bin" }
    }

    val globalBinInSystemPath: Boolean by lazy {
        globalBinPath?.let {
            systemPathContains(it)
        } ?: false
    }

    protected fun NPMCommand(name: String): Command {
        return if (globalBinInSystemPath) {
            Command(name)
        } else {
            val packageFile = File(globalBinPath, name)
            val resolvedFile = packageFile.getOriginalFile()

            val pathWithName = if (resolvedFile.existsSafely()) {
                fullFilePath(resolvedFile) ?: fullFilePath(packageFile) ?: name
            } else if (packageFile.existsSafely()) {
                fullFilePath(packageFile) ?: name
            } else {
                name
            }

            Command(pathWithName)
        }
    }
}