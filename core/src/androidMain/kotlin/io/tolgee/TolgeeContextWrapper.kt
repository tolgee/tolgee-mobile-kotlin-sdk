package io.tolgee

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import kotlinx.atomicfu.atomic

/**
 * A custom [ContextWrapper] that overrides string retrieval to fetch translations from the Tolgee platform.
 *
 * This class intercepts calls to [getString] and attempts to load the string from Tolgee.
 * If no translation is loaded or found, it falls back to the default [Context.getString] implementation.
 *
 * @param base The base [Context] to wrap.
 * @param tolgee The Tolgee translation service used for retrieving localized strings.
 */
class TolgeeContextWrapper(
    val base: Context,
    val tolgee: Tolgee
) : ContextWrapper(base) {

    private val res = atomic<Resources?>(null)

    override fun getResources(): Resources? {
        if (res.value == null) {
            val superResources: Resources? = super.getResources()

            if (superResources != null) {
                res.compareAndSet(null, TolgeeResources(
                    baseContext = base,
                    base = superResources,
                    tolgee = tolgee
                ))
            }

        }
        return res.value ?: super.getResources()
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
            val tolgee = Tolgee.instance ?: return ContextWrapper(base)
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