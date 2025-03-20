package dev.datlag.tolgee.common

import dev.datlag.tooling.scopeCatching
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

/**
 * Simple wrapper to achieve common behavior for `localizedStringForKey`
 */
internal fun NSBundle.localizedString(
    key: String,
    default: String?,
    table: String?
): String? {
    return scopeCatching {
        this.localizedStringForKey(key, default?.ifBlank { null }, table)
    }.getOrNull().takeUnless {
        it == key
    }?.ifBlank { null }
}

/**
 * Retrieve a [NSBundle] without crashing from resources.
 */
internal fun NSBundle.Companion.fromRes(res: String?, ofType: String?): NSBundle? {
    return res?.let {
        val path = scopeCatching {
            NSBundle.mainBundle.pathForResource(res, ofType)
        }.getOrNull() ?: return null

        val bundle = scopeCatching {
            NSBundle(path = path)
        }.getOrNull() ?: scopeCatching {
            NSBundle(NSURL(fileURLWithPath = path))
        }.getOrNull()

        return bundle
    }
}