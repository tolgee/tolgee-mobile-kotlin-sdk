package dev.datlag.tolgee

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dev.datlag.tolgee.common.mapNotNull
import dev.datlag.tolgee.model.TolgeeMessageParams
import org.jetbrains.compose.resources.StringResource

@Composable
fun stringResource(tolgee: Tolgee, resource: StringResource): String {
    return tolgee.translation(
        key = resource.key,
        parameters = TolgeeMessageParams.Indexed()
    ).mapNotNull().collectAsState(
        initial = org.jetbrains.compose.resources.stringResource(resource)
    ).value
}

@Composable
fun stringResource(resource: StringResource): String {
    val tolgee = Tolgee.instance ?: return org.jetbrains.compose.resources.stringResource(resource)
    return stringResource(tolgee, resource)
}

@Composable
fun stringResource(tolgee: Tolgee, resource: StringResource, vararg formatArgs: Any): String {
    return tolgee.translation(
        key = resource.key,
        parameters = TolgeeMessageParams.Indexed(*formatArgs)
    ).mapNotNull().collectAsState(
        initial = org.jetbrains.compose.resources.stringResource(resource, *formatArgs)
    ).value
}

@Composable
fun stringResource(resource: StringResource, vararg formatArgs: Any): String {
    val tolgee = Tolgee.instance ?: return org.jetbrains.compose.resources.stringResource(resource, *formatArgs)
    return stringResource(tolgee, resource, *formatArgs)
}
