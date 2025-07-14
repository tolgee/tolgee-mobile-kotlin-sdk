package io.tolgee.storage

import android.content.Context
import java.io.File

class TolgeeStorageProviderAndroid(val context: Context, val path: String = "tolgee/localization-cache") : TolgeeStorageProvider {
  val cacheDir get() = File(context.filesDir, path)

  override fun put(name: String, data: ByteArray) {
    val dir = cacheDir
    dir.mkdirs()
    val file = File(dir, escape(name))
    file.writeBytes(data)
  }

  override fun get(name: String): ByteArray? {
    val file = File(cacheDir, escape(name))
    if (!file.exists()) {
      return null
    }
    return file.readBytes()
  }

  private fun escape(name: String): String {
    return name.replace("/", "_")
  }
}