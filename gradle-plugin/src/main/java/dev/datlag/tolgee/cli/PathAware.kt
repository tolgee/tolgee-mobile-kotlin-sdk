package dev.datlag.tolgee.cli

import dev.datlag.tolgee.common.fullPathSafely
import dev.datlag.tooling.Platform
import dev.datlag.tooling.scopeCatching
import dev.datlag.tooling.systemEnv
import dev.datlag.tooling.systemProperty
import java.io.File

open class PathAware {

    val systemPath: String? by lazy {
        systemEnv(PATH_ENV)?.trim()
    }

    val systemPathDelimiter: Char by lazy {
        scopeCatching {
            File.pathSeparatorChar
        }.getOrNull() ?: if (Platform.isWindows) {
            ';'
        } else {
            ':'
        }
    }

    val filePathDelimiter: Char by lazy {
        scopeCatching {
            File.separatorChar
        }.getOrNull() ?: systemProperty(FILE_SEPARATOR_PROPERTY)?.singleOrNull() ?: if (Platform.isWindows) {
            '\\'
        } else {
            '/'
        }
    }

    val systemPaths: Set<String> by lazy {
        systemPath?.split(systemPathDelimiter)?.toSet() ?: emptySet()
    }

    fun systemPathContains(path: String): Boolean {
        val allPaths = systemPaths.ifEmpty { return false }
        return allPaths.any { existing ->
            existing == path || existing == path.let { p -> if (p.endsWith(filePathDelimiter)) p else "$p$filePathDelimiter" }
        }
    }

    fun systemPathExecutable(name: String): String? {
        return systemPaths.firstNotNullOfOrNull {
            File(it, name).fullPathSafely()
        }
    }

    companion object {
        private const val PATH_ENV = "PATH"
        private const val FILE_SEPARATOR_PROPERTY = "file.separator"
    }

}