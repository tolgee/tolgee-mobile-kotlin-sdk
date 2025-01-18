package dev.datlag.tolgee.kormatter.utils.internal

import dev.datlag.tooling.Platform
import kotlin.native.Platform as NativePlatform
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
internal actual val lineSeparator: String
    get() = if (NativePlatform.osFamily == OsFamily.WINDOWS || Platform.isWindows) {
        "\r\n"
    } else {
        "\n"
    }