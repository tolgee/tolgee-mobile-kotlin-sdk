package dev.datlag.tolgee

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.datlag.tolgee.common.mapNotNull
import dev.datlag.tolgee.model.TolgeeMessageParams

@Composable
fun Tolgee.stringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val key = remember(id) {
        TolgeeAndroid.keyFromStringResource(context, id)
    }

    val translationFlow = (this as? TolgeeAndroid)?.translation(context, id, *formatArgs)
        ?: (key ?: TolgeeAndroid.keyFromStringResource(context, id))?.let {
        this.translation(key = it, parameters = TolgeeMessageParams.Indexed(*formatArgs))
    }

    return translationFlow?.mapNotNull()?.collectAsState(
        initial = androidx.compose.ui.res.stringResource(id, *formatArgs)
    )?.value ?: androidx.compose.ui.res.stringResource(id, *formatArgs)
}

@Composable
fun stringResource(tolgee: Tolgee, @StringRes id: Int, vararg formatArgs: Any): String {
    return tolgee.stringResource(id, *formatArgs)
}

@Composable
fun stringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val instance = Tolgee.instance ?: return androidx.compose.ui.res.stringResource(id, *formatArgs)

    return stringResource(instance, id, *formatArgs)
}