package dev.datlag.tolgee.cli

import dev.datlag.tolgee.common.fullPathSafely
import dev.datlag.tooling.Platform
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

    val systemPaths: Set<String> by lazy {
        systemPath?.split(systemPathDelimiter)?.toSet() ?: emptySet()
    }

    fun systemPathContains(path: String): Boolean {
        val allPaths = systemPaths.ifEmpty { return false }
        return allPaths.any { existing ->
            existing == path || existing == path.let { p -> if (p.endsWith("/")) p else "$p/" }
        }
    }

    fun systemPathExecutable(name: String): String? {
        return systemPaths.firstNotNullOfOrNull {
            File(it, name).fullPathSafely()
        }
    }

    companion object {
        private const val PATH_ENV = "PATH"
    }

}