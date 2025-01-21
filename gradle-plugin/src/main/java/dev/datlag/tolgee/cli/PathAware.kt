package dev.datlag.tolgee.cli

import dev.datlag.tooling.Platform
import dev.datlag.tooling.scopeCatching
import dev.datlag.tooling.systemEnv
import java.io.File

open class PathAware {

    val systemPath: String? by lazy {
        systemEnv(PATH_ENV)?.trim()
    }

    val systemPathDelimiter: Char by lazy {
        if (Platform.isWindows) {
            ';'
        } else {
            ':'
        }
    }

    fun systemPathContains(path: String): Boolean {
        val allPaths = systemPath?.split(systemPathDelimiter) ?: return false
        return allPaths.any { existing ->
            existing == path || existing == path.let { p -> if (p.endsWith("/")) p else "$p/" }
        }
    }

    fun fullFilePath(file: File): String? = scopeCatching {
        file.canonicalPath
    }.getOrNull()?.ifBlank { null } ?: scopeCatching {
        file.absolutePath
    }.getOrNull()?.ifBlank { null }

    companion object {
        private const val PATH_ENV = "PATH"
    }

}