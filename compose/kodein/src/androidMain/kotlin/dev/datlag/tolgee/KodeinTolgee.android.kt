package dev.datlag.tolgee

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Composable
fun kodeinStringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val instance by localDI().instanceOrNull<Tolgee>()

    return (instance ?: Tolgee.instance)?.let {
        stringResource(it, id, *formatArgs)
    } ?: stringResource(id, *formatArgs)
}