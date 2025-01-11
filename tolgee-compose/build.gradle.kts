import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()

    js(IR) {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.components.resources)

            implementation(libs.immutable)
            implementation(libs.ktor)
            implementation(libs.serialization)
            implementation(libs.tooling)
        }

        val localeMain by creating {
            dependsOn(commonMain.get())

            jvmMain.orNull?.dependsOn(this)
            iosMain.orNull?.dependsOn(this)

            dependencies {
                implementation(libs.locale)
            }
        }
    }
}