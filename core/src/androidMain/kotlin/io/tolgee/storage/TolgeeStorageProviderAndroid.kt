package io.tolgee.storage

import android.content.Context
import dev.datlag.tooling.canReadSafely
import dev.datlag.tooling.canWriteSafely
import dev.datlag.tooling.existsSafely
import dev.datlag.tooling.mkdirsSafely
import dev.datlag.tooling.scopeCatching
import java.io.File

class TolgeeStorageProviderAndroid(
    private val context: Context,
    private val versionCode: Int,
    private val path: String = "tolgee/localization-cache",
) : TolgeeStorageProvider {
    private val cacheDir get() = context.filesDir / path / versionCode.toString()

    override fun put(name: String, data: ByteArray) {
        val dir = cacheDir
        dir.mkdirsSafely()
        val file = dir / escape(name)
        if (!file.canWriteSafely()) {
            return
        }
        scopeCatching {
            file.writeBytes(data)
        }
    }

    override fun get(name: String): ByteArray? {
        val file = cacheDir / escape(name)
        if (!file.existsSafely() || !file.canReadSafely()) {
            return null
        }

        return scopeCatching {
            file.readBytes()
        }.getOrNull()
    }

    private fun escape(name: String): String {
        return name.replace("/", "_")
    }

    internal companion object {
        private infix operator fun File.div(other: String) = File(this, other)
    }
}