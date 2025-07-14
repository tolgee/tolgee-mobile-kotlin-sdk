package io.tolgee.common

import io.tolgee.TolgeePluginExtension
import dev.datlag.tooling.async.scopeCatching
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType

internal val Project.tolgeeExtension: TolgeePluginExtension
    get() = this.extensions.findByType<TolgeePluginExtension>()
        ?: scopeCatching { createTolgeeExtension() }.getOrNull()
        ?: this.extensions.getByType<TolgeePluginExtension>()

@Throws(IllegalArgumentException::class)
private fun Project.createTolgeeExtension(): TolgeePluginExtension {
    return this@createTolgeeExtension.extensions.create(
        name = "tolgee",
        type = TolgeePluginExtension::class
    ).apply { setupConvention(this@createTolgeeExtension) }
}