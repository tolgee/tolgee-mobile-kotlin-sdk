package io.tolgee.demo.compose

import androidx.compose.ui.window.singleWindowApplication

fun main() {
    singleWindowApplication(
        title = "Tolgee Compose Multiplatform Demo"
    ) {
        App()
    }
}