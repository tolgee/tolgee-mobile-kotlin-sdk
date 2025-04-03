package dev.datlag.tolgee

import android.content.Context
import android.content.res.Resources
import dev.datlag.tolgee.common.getStringInstant

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
        return baseContext.getStringInstant(tolgee, id)
    }

    override fun getString(id: Int, vararg formatArgs: Any?): String {
        return baseContext.getStringInstant(tolgee, id, *formatArgs.filterNotNull().toTypedArray())
    }
}