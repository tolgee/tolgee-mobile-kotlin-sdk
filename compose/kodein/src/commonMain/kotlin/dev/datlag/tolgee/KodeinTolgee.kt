package dev.datlag.tolgee

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Composable
fun kodeinStringResource(res: StringResource): String {
    val instance by localDI().instanceOrNull<Tolgee>()

    return (instance ?: Tolgee.instance)?.let {
        stringResource(it, res)
    } ?: stringResource(res)
}

@Composable
fun kodeinStringResource(res: StringResource, vararg formatArgs: Any): String {
    val instance by localDI().instanceOrNull<Tolgee>()

    return (instance ?: Tolgee.instance)?.let {
        stringResource(it, res, *formatArgs)
    } ?: stringResource(res, *formatArgs)
}