package io.tolgee.common

import io.tolgee.TolgeePluginExtension
import dev.datlag.tooling.async.scopeCatching
import org.gradle.api.Project

internal val Project.tolgeeExtension: TolgeePluginExtension
    get() = this.extensions.findByType(TolgeePluginExtension::class.java)
        ?: scopeCatching { createTolgeeExtension() }.getOrNull()
        ?: this.extensions.getByType(TolgeePluginExtension::class.java)

@Throws(IllegalArgumentException::class)
private fun Project.createTolgeeExtension(): TolgeePluginExtension {
    return this@createTolgeeExtension.extensions.create(
        "tolgee",
        TolgeePluginExtension::class.java
    ).apply { setupConvention(this@createTolgeeExtension) }
}