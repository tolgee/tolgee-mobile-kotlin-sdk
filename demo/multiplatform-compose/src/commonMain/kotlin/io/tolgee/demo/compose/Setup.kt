package io.tolgee.demo.compose

import io.tolgee.Tolgee

object Setup {

  fun init() {
    Tolgee.init {
      contentDelivery {
        url = "https://cdn.tolg.ee/96eacb8b07382b60c3f94b30405cc49b"
      }
    }
  }
}