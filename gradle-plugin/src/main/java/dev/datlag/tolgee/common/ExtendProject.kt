package dev.datlag.tolgee.common

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension as AndroidBaseExtension
import dev.datlag.tolgee.TolgeePluginExtension
import dev.datlag.tooling.existsSafely
import dev.datlag.tooling.scopeCatching
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File

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

private val Project.kotlinProjectExtension: KotlinProjectExtension?
    get() = this.extensions.findByType<KotlinProjectExtension>()
        ?: (this.extensions.findByName("kotlin") as? KotlinProjectExtension)

private val Project.kotlinBaseExtension: KotlinBaseExtension?
    get() = this.extensions.findByType<KotlinBaseExtension>()
        ?: (this.extensions.findByName("kotlin") as? KotlinBaseExtension)

private val Project.kotlinMultiPlatformExtension: KotlinMultiplatformExtension?
    get() = this.extensions.findByType<KotlinMultiplatformExtension>()
        ?: (this.extensions.findByName("kotlin") as? KotlinMultiplatformExtension)

private val Project.kotlinJvmExtension: KotlinJvmExtension?
    get() = this.extensions.findByType<KotlinJvmExtension>()
        ?: this.extensions.findByName("kotlin") as? KotlinJvmExtension

private val Project.kotlinJvmProjectExtension: KotlinJvmProjectExtension?
    get() = this.extensions.findByType<KotlinJvmProjectExtension>()
        ?: this.extensions.findByName("kotlin") as? KotlinJvmProjectExtension

private val Project.kotlinAndroidExtension: KotlinAndroidExtension?
    get() = this.extensions.findByType<KotlinAndroidExtension>()
        ?: this.extensions.findByName("kotlin") as? KotlinAndroidExtension

private val Project.kotlinAndroidProjectExtension: KotlinAndroidProjectExtension?
    get() = this.extensions.findByType<KotlinAndroidProjectExtension>()
        ?: this.extensions.findByName("kotlin") as? KotlinAndroidProjectExtension

private val Project.kotlinJsExtension: KotlinJsProjectExtension?
    get() = this.extensions.findByType<KotlinJsProjectExtension>()
        ?: this.extensions.findByName("kotlin") as? KotlinJsProjectExtension

private val Project.kotlinJs2Extension: Kotlin2JsProjectExtension?
    get() = this.extensions.findByType<Kotlin2JsProjectExtension>()
        ?: this.extensions.findByName("kotlin") as? Kotlin2JsProjectExtension

private val KotlinProjectExtension.allTargets: List<KotlinTarget>
    get() = when (this) {
        is KotlinSingleTargetExtension<*> -> listOfNotNull(this.target)
        is KotlinMultiplatformExtension -> this.targets.toList()
        else -> emptyList()
    }

internal val Project.isAndroidOnly: Boolean
    get() {
        if (kotlinProjectExtension is KotlinMultiplatformExtension || kotlinMultiPlatformExtension != null) {
            return false
        }

        val singleTarget = kotlinProjectExtension is KotlinSingleTargetExtension<*> || kotlinProjectExtension is KotlinSingleJavaTargetExtension

        return if (singleTarget) {
            val usingJs = kotlinJsExtension != null || kotlinJs2Extension != null

            if (!usingJs) {
                val usingJvm = kotlinJvmExtension != null || kotlinJvmProjectExtension != null
                val usingAndroid = kotlinAndroidExtension != null || kotlinAndroidProjectExtension != null

                usingAndroid xor usingJvm
            } else {
                false
            }
        } else {
            false
        }
    }

private typealias AndroidCommonExtension = CommonExtension<*, *, *, *, *, *>

internal val Project.androidResources: List<File>
    get() {
        val baseExtension = (kotlinAndroidExtension as? AndroidBaseExtension)
            ?: (kotlinAndroidProjectExtension as? AndroidBaseExtension)
            ?: extensions.findByType<AndroidBaseExtension>()
            ?: (extensions.findByName("android") as? AndroidBaseExtension)
            ?: (extensions.findByName("main") as? AndroidBaseExtension)
            ?: (extensions.findByName("androidMain") as? AndroidBaseExtension)

        val baseSourceSet = baseExtension?.sourceSets?.findByName("main")
            ?: baseExtension?.sourceSets?.findByName("android")
            ?: baseExtension?.sourceSets?.findByName("androidMain")

        return listOfNotNull(
            baseSourceSet?.res?.srcDirs,
            baseSourceSet?.resources?.srcDirs
        ).flatten().mapNotNull {
            if (it.existsSafely()) {
                it
            } else {
                null
            }
        }
    }