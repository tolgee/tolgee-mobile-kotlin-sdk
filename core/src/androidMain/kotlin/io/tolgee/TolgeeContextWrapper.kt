package io.tolgee

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.view.LayoutInflater
import kotlinx.atomicfu.atomic

/**
 * A custom [ContextWrapper] that overrides string retrieval to fetch translations from the Tolgee platform.
 *
 * This class intercepts calls to [getString] and attempts to load the string from Tolgee.
 * If no translation is loaded or found, it falls back to the default [Context.getString] implementation.
 * Calls to [getText] are also intercepted, if the string is found in Tolgee. The returned value is not formatted.
 * (behaves like [Context.getText])
 *
 * Additionally, this wrapper installs a [TolgeeLayoutInflaterFactory] to automatically translate
 * text attributes during layout inflation.
 *
 * @param base The base [Context] to wrap.
 * @param tolgee The Tolgee translation service used for retrieving localized strings.
 */
class TolgeeContextWrapper(
    val base: Context,
    val tolgee: Tolgee,
    val interceptGetString: Boolean = true,
    val interceptGetText: Boolean = true,
    val argumentLayoutInflater: Boolean = true
) : ContextWrapper(base) {

    private var baseRes by atomic<Resources?>(null)
    private var res by atomic<Resources?>(baseRes)
    private var layoutInflater by atomic<LayoutInflater?>(null)

    override fun getResources(): Resources? {
        val base = super.getResources() ?: return res

        if ((res == null || baseRes != base) && (interceptGetString || interceptGetText)) {
            res = TolgeeResources(base, tolgee, interceptGetString, interceptGetText)
            baseRes = base
        }

        return res ?: base
    }

    override fun getSystemService(name: String): Any? {
        if (LAYOUT_INFLATER_SERVICE == name) {
            if (layoutInflater == null && tolgee is TolgeeAndroid && argumentLayoutInflater) {
                val baseInflater = super.getSystemService(name) as? LayoutInflater
                baseInflater?.let {
                    val cloned = it.cloneInContext(this)
                    installFactory2(cloned, tolgee)
                    layoutInflater = cloned
                }
            }
            return layoutInflater ?: super.getSystemService(name)
        }
        return super.getSystemService(name)
    }

    /**
     * Installs the [TolgeeLayoutInflaterFactory] on the given LayoutInflater.
     */
    private fun installFactory2(inflater: LayoutInflater, tolgee: TolgeeAndroid) {
        val existingFactory = inflater.factory
        val existingFactory2 = inflater.factory2
        inflater.factory2 = TolgeeLayoutInflaterFactory(tolgee, existingFactory, existingFactory2)
    }

    companion object {
        /**
         * Wraps the given [base] context with a [TolgeeContextWrapper] that uses the global singleton instance of Tolgee.
         *
         * This method is a convenience function for cases where `attachBaseContext` provides a nullable context.
         * If [base] is `null`, it falls back to returning a regular [ContextWrapper].
         *
         * @param base The context to wrap, which may be `null`.
         * @return A wrapped [ContextWrapper] with Tolgee support, or a regular [ContextWrapper] if Tolgee is unavailable.
         */
        @JvmStatic
        fun wrap(base: Context?): ContextWrapper {
            val tolgee = Tolgee.instanceOrNull ?: return ContextWrapper(base)
            return wrap(base, tolgee)
        }

        /**
         * Wraps the given [base] context with a [TolgeeContextWrapper] using the provided [tolgee] instance.
         *
         * This method allows explicitly specifying a Tolgee instance for cases where dependency injection
         * or multiple Tolgee instances are needed.
         * If [base] is `null`, it falls back to returning a regular [ContextWrapper].
         *
         * @param base The context to wrap, which may be `null`.
         * @param tolgee The Tolgee translation service instance to use for retrieving localized strings.
         * @return A wrapped [ContextWrapper] with Tolgee support, or a regular [ContextWrapper] if [base] is `null`.
         */
        @JvmStatic
        fun wrap(base: Context?, tolgee: Tolgee): ContextWrapper {
            if (base == null) {
                return ContextWrapper(base)
            }
            return TolgeeContextWrapper(base, tolgee)
        }
    }
}