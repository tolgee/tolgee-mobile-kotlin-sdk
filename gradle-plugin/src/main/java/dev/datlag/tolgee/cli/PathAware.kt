package dev.datlag.tolgee.cli

import dev.datlag.tooling.Platform
import dev.datlag.tooling.systemEnv

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

    companion object {
        private const val PATH_ENV = "PATH"
    }

}