package io.tolgee.model

internal data class Config(
    val android: Android,
    val compose: Compose
) {
    data class Android(
        val getStringReplacement: Boolean
    )

    data class Compose(
        val stringResourceReplacement: Boolean
    )
}
