package dev.datlag.tolgee.model

internal data class Config(
    val android: Android
) {
    data class Android(
        val getStringReplacement: Boolean
    )
}
