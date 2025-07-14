package io.tolgee.storage

interface TolgeeStorageProvider {
  fun put(name: String, data: ByteArray)
  fun get(name: String): ByteArray?
}