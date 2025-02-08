package dev.datlag.tolgee

import dev.datlag.tolgee.common.mapNotNull
import dev.datlag.tolgee.model.TolgeeMessageParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.localizedStringWithFormat

data class TolgeeApple internal constructor(
    override val config: Config
) : Tolgee(config) {

    fun translation(key: String, default: String?, table: String? = null): Flow<String> = flow {
        emit(getLocalizedStringFromBundle(key, default, table) ?: default?.ifBlank { null })

        emitAll(translation(key, TolgeeMessageParams.None))
    }.mapNotNull()

    fun translation(key: String, default: String?, table: String? = null, vararg args: Any): Flow<String> = flow {
        emit(getLocalizedStringFromBundleFormatted(key, default, table, *args))

        emitAll(translation(key, TolgeeMessageParams.Indexed(*args)))
    }.mapNotNull()

    fun instant(key: String, default: String?, table: String? = null): String? {
        return instant(key) ?: getLocalizedStringFromBundle(key, default, table) ?: default?.ifBlank { null }
    }

    fun instant(key: String, default: String?, table: String? = null, vararg args: Any): String? {
        return instant(key, TolgeeMessageParams.Indexed(*args))
            ?: getLocalizedStringFromBundleFormatted(key, default, table, *args)
    }

    companion object {
        fun getLocalizedStringFromBundle(key: String, default: String?, table: String?): String? {
            return NSBundle.mainBundle.localizedStringForKey(key, default?.ifBlank { null }, table).takeUnless { it == key }?.ifBlank { null }
                ?: NSBundle.mainBundle.pathForResource("Base", "lproj")?.let {
                    NSBundle(NSURL(fileURLWithPath = it))
                }?.localizedStringForKey(key, default?.ifBlank { null }, table).takeUnless { it == key }?.ifBlank { null }
        }

        fun getLocalizedStringFromBundleFormatted(key: String, default: String?, table: String?, vararg args: Any): String? {
            return (getLocalizedStringFromBundle(key, default, table) ?: default?.ifBlank { null })?.let { format ->
                NSString.localizedStringWithFormat(format, *args.toList().toTypedArray())
            }
        }
    }

}
