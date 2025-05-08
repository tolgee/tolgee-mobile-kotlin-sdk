package io.tolgee.model

internal data class Config(
    val android: Android,
    val compose: Compose
) {
    data class Android(
        val getStringReplacement: Boolean,
        val pluralStringReplacement: Boolean
    )

    data class Compose(
        val stringResourceReplacement: Boolean,
        val pluralStringReplacement: Boolean
    )
}
