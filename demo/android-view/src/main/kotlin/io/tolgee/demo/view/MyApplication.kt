package io.tolgee.demo.view

import android.app.Application
import io.tolgee.Tolgee

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    Tolgee.init {
      contentDelivery {
//        url =
      }
    }
  }
}