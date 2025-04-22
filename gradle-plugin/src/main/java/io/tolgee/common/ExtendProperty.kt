package io.tolgee.common

import org.gradle.api.Project
import org.gradle.api.provider.HasMultipleValues
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

/**
 * Sets the value of the property to the elements of the given iterable, and replaces any existing value.
 * This property will query the elements of the iterable each time the value of this property is queried.
 *
 * The convention for this property, if any, will be used to provide the value instead.
 *
 * @param elements The elements, can be null.
 */
fun <T> HasMultipleValues<T>.set(vararg elements: T & Any) {
    this.set(elements.toList())
}

internal fun <T, R> Property<T>.lazyMap(
    project: Project,
    map: (T?) -> R?,
    fallback: (T?) -> R? = { null }
): Provider<R> {
    return project.provider {
        val config = this@lazyMap.orNull
        map(config) ?: fallback(config)
    }
}