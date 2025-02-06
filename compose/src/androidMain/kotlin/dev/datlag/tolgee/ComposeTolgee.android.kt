package dev.datlag.tolgee

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.datlag.tolgee.model.TolgeeMessageParams
import kotlinx.coroutines.flow.flowOf

@Composable
fun stringResource(tolgee: Tolgee, @StringRes id: Int): String {
    val context = LocalContext.current
    val key = remember(id) {
        TolgeeAndroid.getKeyFromStringResource(context, id)
    }

    val translationFlow = (tolgee as? TolgeeAndroid)?.translation(context, id)
        ?: (key ?: TolgeeAndroid.getKeyFromStringResource(context, id))?.let {
            tolgee.translation(key = it)
        } ?: flowOf(androidx.compose.ui.res.stringResource(id))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.stringResource(id)
    ).value
}

@Composable
fun stringResource(@StringRes id: Int): String {
    val instance = Tolgee.instance ?: return androidx.compose.ui.res.stringResource(id)

    return stringResource(instance, id)
}

@Composable
fun stringResource(tolgee: Tolgee, @StringRes id: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val key = remember(id) {
        TolgeeAndroid.getKeyFromStringResource(context, id)
    }

    val translationFlow = (tolgee as? TolgeeAndroid)?.translation(context, id, *formatArgs)
        ?: (key ?: TolgeeAndroid.getKeyFromStringResource(context, id))?.let {
            tolgee.translation(key = it, parameters = TolgeeMessageParams.Indexed(*formatArgs))
        } ?: flowOf(androidx.compose.ui.res.stringResource(id, *formatArgs))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.stringResource(id, *formatArgs)
    ).value
}

@Composable
fun stringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val instance = Tolgee.instance ?: return androidx.compose.ui.res.stringResource(id, *formatArgs)

    return stringResource(instance, id, *formatArgs)
}