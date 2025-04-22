package io.tolgee

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.tolgee.common.mapNotNull
import io.tolgee.model.TolgeeMessageParams
import dev.datlag.tooling.async.scopeCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * A specialized implementation of the Tolgee class for Android that provides methods for
 * retrieving and formatting translations associated with string resources within an
 * Android application. The class uses Kotlin Flows and instant methods for localized
 * text retrieval and dynamic text formatting based on string resources.
 *
 * @param config The configuration object providing settings for Tolgee.
 */
@ConsistentCopyVisibility
data class TolgeeAndroid internal constructor(
    override val config: Config
) : Tolgee(config) {

    /**
     * Retrieves a translation for the given string resource ID and emits the localized text as a [Flow].
     *
     * It initially emits the string from the Android app's resources. If a translation key is obtained
     * from the string resource ID, it emits subsequent translations fetched from Tolgee integration.
     *
     * @param context The application context used to access the string resources.
     * @param id The resource ID of the string to be translated.
     * @return A [Flow] emitting localized text, starting with the Android string resource and
     *         followed by the corresponding Tolgee translations, if available.
     */
    fun translation(context: Context, @StringRes id: Int): Flow<String> = flow {
        emit(context.getString(id))

        getKeyFromStringResource(context, id)?.let { key ->
            emitAll(translation(key, TolgeeMessageParams.None).mapNotNull())
        }
    }

    /**
     * Provides a flow of translated strings for a given string resource ID with optional formatting arguments.
     * The method first emits the string resolved from the context's resources. If the key corresponding
     * to the string resource ID exists, it emits translations for the key using the provided formatting arguments.
     *
     * @param context the Android context used to resolve the string resource
     * @param id the string resource ID
     * @param formatArgs optional arguments to format the string resource
     * @return a flow of translated strings
     */
    fun translation(context: Context, @StringRes id: Int, vararg formatArgs: Any): Flow<String> = flow {
        emit(context.getString(id, *formatArgs))

        getKeyFromStringResource(context, id)?.let { key ->
            emitAll(translation(key, TolgeeMessageParams.Indexed(*formatArgs)))
        }
    }

    /**
     * Provides an immediate translation for the given string resource ID within the given context.
     * If a translation key is derived from the string resource, it retrieves the translation using Tolgee.
     * Otherwise, it falls back to returning the default string resource value.
     *
     * @param context The context used to access resources and provide localization settings.
     * @param id The resource ID of the string to be translated.
     * @return The translated string if a key-based translation is found; otherwise, the default string resource value.
     */
    fun instant(context: Context, @StringRes id: Int): String {
        return getKeyFromStringResource(context, id)?.let { key ->
            instant(key)
        } ?: context.getString(id)
    }

    /**
     * Provides an immediate translation for a given string resource ID with optional format arguments.
     * If the translation key is found in the string resource, it retrieves the translation; otherwise,
     * the default string resource value is returned.
     *
     * @param context The context used to access resources and string values.
     * @param id The resource ID of the string to be translated.
     * @param formatArgs Optional arguments for formatting the string.
     * @return The translated string if a translation key is found, or the default string resource value.
     */
    fun instant(context: Context, @StringRes id: Int, vararg formatArgs: Any): String {
        return getKeyFromStringResource(context, id)?.let { key ->
            instant(key, TolgeeMessageParams.Indexed(*formatArgs))
        } ?: context.getString(id, *formatArgs)
    }

    /**
     * Preloads the required languages and their translations for the current Tolgee instance.
     *
     * This method ensures that both the list of available project languages and their
     * corresponding translations are loaded into memory. It performs these operations atomically
     * by utilizing mutex locks to prevent concurrent modifications.
     *
     * Must be called before accessing translation functionalities such as [instant] to ensure
     * that translations are available and up-to-date.
     *
     * This method is coroutine-safe and utilizes structured concurrency to manage asynchronous
     * operations.
     *
     * @param lifecycleOwner any [LifecycleOwner] to launch the coroutine from, e.g. Activity or Fragment
     */
    fun preload(lifecycleOwner: LifecycleOwner) = lifecycleOwner.lifecycleScope.launch {
        preload()
    }

    /**
     * A companion object for utility functions related to string resources and key retrieval.
     */
    companion object {
        /**
         * Retrieves the resource entry name corresponding to the provided string resource ID.
         * The result is trimmed and null is returned if the resulting string is blank.
         *
         * @param context The context from which to retrieve the resources.
         * @param id The resource ID of the string resource to retrieve.
         * @return The resource entry name as a string, or null if the result is blank.
         */
        fun getKeyFromStringResource(context: Context, @StringRes id: Int): String? {
            return scopeCatching {
                context.resources.getResourceEntryName(id)
            }.getOrNull()?.trim()?.ifBlank { null }
        }
    }
}