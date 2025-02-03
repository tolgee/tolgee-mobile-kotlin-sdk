package dev.datlag.tolgee

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import org.jetbrains.compose.resources.StringResource

@Composable
fun Tolgee.stringResource(resource: StringResource, vararg formatArgs: Any): String {
    return produceState(
        this.translationFromCache(resource.key, *formatArgs) ?: org.jetbrains.compose.resources.stringResource(resource, *formatArgs)
    ) {
        this.value = this@stringResource.translation(resource.key, *formatArgs)
            ?: this@stringResource.translationFromCache(resource.key, *formatArgs) ?: value
    }.value
}

@Composable
fun stringResource(tolgee: Tolgee, resource: StringResource, vararg formatArgs: Any): String {
    return tolgee.stringResource(resource, *formatArgs)
}

@Composable
fun stringResource(resource: StringResource, vararg formatArgs: Any): String {
    val tolgee = Tolgee.instance ?: return org.jetbrains.compose.resources.stringResource(resource, *formatArgs)
    return stringResource(tolgee, resource, *formatArgs)
}
