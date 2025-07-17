package io.tolgee.demo.view

import android.app.Application
import io.tolgee.Tolgee

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    Tolgee.init {
      contentDelivery {
        url = "https://cdn.tolg.ee/96eacb8b07382b60c3f94b30405cc49b"
      }
    }
  }
}