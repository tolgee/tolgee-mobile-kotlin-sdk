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
                when (args.size) {
                    0 -> NSString.localizedStringWithFormat(format)
                    1 -> NSString.localizedStringWithFormat(format, args[0])
                    2 -> NSString.localizedStringWithFormat(format, args[0], args[1])
                    3 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2])
                    4 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3])
                    5 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4])
                    6 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5])
                    7 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5], args[6])
                    8 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7])
                    9 -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8])
                    else -> NSString.localizedStringWithFormat(format, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9])
                }
            }
        }
    }

}
