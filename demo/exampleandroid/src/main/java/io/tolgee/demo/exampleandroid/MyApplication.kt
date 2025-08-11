package io.tolgee.demo.exampleandroid

import android.app.Application
import io.tolgee.Tolgee
import io.tolgee.storage.TolgeeStorageProviderAndroid

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    Tolgee.init {
      contentDelivery {
        url = "https://cdn.tolg.ee/96eacb8b07382b60c3f94b30405cc49b"
        storage = TolgeeStorageProviderAndroid(this@MyApplication, BuildConfig.VERSION_CODE)
      }
    }
  }
}