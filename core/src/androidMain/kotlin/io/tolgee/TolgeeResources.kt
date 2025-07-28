package io.tolgee

import android.content.Context
import android.content.res.Resources
import android.util.Log
import io.tolgee.common.getQuantityStringT
import io.tolgee.common.getStringArrayT
import io.tolgee.common.getStringT

/**
 * Ignore Deprecation: Resources constructor is not really deprecated, apps should just not create
 * an instance like this.
 * Since we are extending the resources class, we are not affected.
 */
@Suppress("DEPRECATION")
internal class TolgeeResources(
    val baseContext: Context,
    val base: Resources,
    val tolgee: Tolgee
) : Resources(base.assets, base.displayMetrics, base.configuration) {

    override fun getString(id: Int): String {
        return baseContext.getStringT(tolgee, id)
    }

    override fun getString(id: Int, vararg formatArgs: Any?): String {
        return baseContext.getStringT(tolgee, id, *formatArgs.filterNotNull().toTypedArray())
    }

    override fun getQuantityString(id: Int, quantity: Int): String {
        return baseContext.resources.getQuantityStringT(tolgee, id, quantity)
    }

    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any?): String {
        return baseContext.resources.getQuantityStringT(tolgee, id, quantity, *formatArgs.filterNotNull().toTypedArray())
    }

    override fun getStringArray(id: Int): Array<out String?> {
        return baseContext.resources.getStringArrayT(tolgee, id)
    }

    override fun getText(id: Int): CharSequence {
        return baseContext.getStringT(tolgee, id)
    }
}