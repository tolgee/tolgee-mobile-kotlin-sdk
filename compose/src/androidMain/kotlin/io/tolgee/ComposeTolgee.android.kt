package io.tolgee

import androidx.annotation.ArrayRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.tolgee.model.TolgeeMessageParams
import kotlinx.coroutines.flow.flowOf

/**
 * Retrieves a localized string resource based on the provided resource ID using Tolgee for translations.
 * If a translation is available from Tolgee, it is used; otherwise, the default string resource is returned.
 *
 * @param tolgee An instance of the Tolgee translation library for managing translations.
 * @param id The resource ID of the string to be translated or retrieved.
 * @return The translated or default string resource as a String.
 */
@Composable
fun stringResource(tolgee: Tolgee, @StringRes id: Int): String {
    val context = LocalContext.current
    val key = remember(id) {
        TolgeeAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (tolgee as? TolgeeAndroid)?.tFlow(context, id)
        ?: (key ?: TolgeeAndroid.getKeyFromResources(context, id))?.let {
            tolgee.tFlow(key = it)
        } ?: flowOf(androidx.compose.ui.res.stringResource(id))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.stringResource(id)
    ).value
}

/**
 * Returns a localized string from the given resource ID.
 *
 * This function is a wrapper around the stringResource that integrates with
 * the Tolgee instance for localization. If Tolgee is not initialized, it falls
 * back to the standard Compose `stringResource`.
 *
 * @param id The resource ID of the string to retrieve.
 * @return The localized string associated with the provided resource ID.
 */
@Composable
fun stringResource(@StringRes id: Int): String {
    val instance = Tolgee.instanceOrNull ?: return androidx.compose.ui.res.stringResource(id)

    return stringResource(instance, id)
}

/**
 * Retrieves a string resource based on the given parameters, supporting dynamic translation
 * with Tolgee integration.
 *
 * @param tolgee The Tolgee instance used for translations.
 * @param id The resource ID of the string to be retrieved.
 * @param formatArgs Optional format arguments to replace placeholders in the string.
 * @return The translated or resource string corresponding to the specified resource ID.
 */
@Composable
fun stringResource(tolgee: Tolgee, @StringRes id: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val key = remember(id) {
        TolgeeAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (tolgee as? TolgeeAndroid)?.tFlow(context, id, *formatArgs)
        ?: (key ?: TolgeeAndroid.getKeyFromResources(context, id))?.let {
            tolgee.tFlow(key = it, parameters = TolgeeMessageParams.Indexed(*formatArgs))
        } ?: flowOf(androidx.compose.ui.res.stringResource(id, *formatArgs))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.stringResource(id, *formatArgs)
    ).value
}

/**
 * Retrieves a localized string resource. If a `Tolgee` instance is available, it uses it to fetch
 * the translated string; otherwise, falls back to the default Compose string resource.
 *
 * @param id The resource ID of the string.
 * @param formatArgs The arguments to be used for formatting the string resource.
 * @return The localized string based on the provided resource ID and arguments.
 */
@Composable
fun stringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val instance = Tolgee.instanceOrNull ?: return androidx.compose.ui.res.stringResource(id, *formatArgs)

    return stringResource(instance, id, *formatArgs)
}

@Composable
fun pluralStringResource(tolgee: Tolgee, @PluralsRes id: Int, quantity: Int): String {
    val context = LocalContext.current
    val key = remember(id) {
        TolgeeAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (tolgee as? TolgeeAndroid)?.tPluralFlow(context.resources, id, quantity)
        ?: (key ?: TolgeeAndroid.getKeyFromResources(context, id))?.let {
            tolgee.tFlow(key = it, parameters = TolgeeMessageParams.Indexed(quantity))
        } ?: flowOf(androidx.compose.ui.res.pluralStringResource(id, quantity))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.pluralStringResource(id, quantity)
    ).value
}

@Composable
fun pluralStringResource(@PluralsRes id: Int, quantity: Int): String {
    val instance = Tolgee.instanceOrNull ?: return androidx.compose.ui.res.pluralStringResource(id, quantity)

    return pluralStringResource(instance, id, quantity)
}

@Composable
fun pluralStringResource(tolgee: Tolgee, @PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val key = remember(id) {
        TolgeeAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (tolgee as? TolgeeAndroid)?.tPluralFlow(context.resources, id, quantity, *formatArgs)
        ?: (key ?: TolgeeAndroid.getKeyFromResources(context, id))?.let {
            tolgee.tFlow(key = it, parameters = TolgeeMessageParams.Indexed(quantity, *formatArgs))
        } ?: flowOf(androidx.compose.ui.res.pluralStringResource(id, quantity, *formatArgs))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.pluralStringResource(id, quantity, *formatArgs)
    ).value
}

@Composable
fun stringArrayResource(tolgee: Tolgee, @ArrayRes id: Int): Array<String> {
    val context = LocalContext.current
    val key = remember(id) {
        TolgeeAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (tolgee as? TolgeeAndroid)?.tArrayFlow(context.resources, id)
        ?: (key ?: TolgeeAndroid.getKeyFromResources(context, id))?.let {
            tolgee.tArrayFlow(key = it)
        } ?: flowOf(androidx.compose.ui.res.stringArrayResource(id).toList())

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.stringArrayResource(id).toList()
    ).value.toTypedArray()
}

@Composable
fun stringArrayResource(@ArrayRes id: Int): Array<String> {
    val instance = Tolgee.instanceOrNull ?: return androidx.compose.ui.res.stringArrayResource(id)
    return stringArrayResource(instance, id)
}