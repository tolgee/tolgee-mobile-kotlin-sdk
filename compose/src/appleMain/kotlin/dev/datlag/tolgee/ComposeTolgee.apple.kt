package dev.datlag.tolgee

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dev.datlag.tolgee.model.TolgeeMessageParams

@Composable
fun stringResource(tolgee: Tolgee, key: String, default: String?, table: String? = null): String {
    val translationFlow = (tolgee as? TolgeeApple)?.translation(key, default, table) ?: tolgee.translation(key)

    return translationFlow.collectAsState(
        initial = TolgeeApple.getLocalizedStringFromBundle(key, default, table) ?: default?.ifBlank { null } ?: ""
    ).value
}

@Composable
fun stringResource(key: String, default: String?, table: String? = null): String {
    val instance = Tolgee.instance ?: return TolgeeApple.getLocalizedStringFromBundle(key, default, table) ?: default?.ifBlank { null } ?: ""

    return stringResource(instance, key, default, table)
}

@Composable
fun stringResource(tolgee: Tolgee, key: String, default: String?, table: String? = null, vararg args: Any): String {
    val translationFlow = (tolgee as? TolgeeApple)?.translation(key, default, table, *args)
        ?: tolgee.translation(key, TolgeeMessageParams.Indexed(*args))

    return translationFlow.collectAsState(
        initial = TolgeeApple.getLocalizedStringFromBundleFormatted(key, default, table, *args) ?: ""
    ).value
}

@Composable
fun stringResource(key: String, default: String?, table: String? = null, vararg args: Any): String {
    val instance = Tolgee.instance ?: return TolgeeApple.getLocalizedStringFromBundleFormatted(key, default, table, *args) ?: ""

    return stringResource(instance, key, default, table, *args)
}