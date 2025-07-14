package io.tolgee.storage

import android.content.Context
import java.io.File

class TolgeeStorageProviderAndroid(val context: Context, val versionCode: Int, val path: String = "tolgee/localization-cache") : TolgeeStorageProvider {
  val cacheDir get() = context.filesDir / path / versionCode.toString()

  override fun put(name: String, data: ByteArray) {
    val dir = cacheDir
    dir.mkdirs()
    val file = dir / escape(name)
    file.writeBytes(data)
  }

  override fun get(name: String): ByteArray? {
    val file = cacheDir / escape(name)
    if (!file.exists()) {
      return null
    }
    return file.readBytes()
  }

  private fun escape(name: String): String {
    return name.replace("/", "_")
  }

  companion object {
    private infix operator fun File.div(other: String) = File(this, other)
  }
}