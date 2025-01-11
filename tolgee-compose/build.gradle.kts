import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget()
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

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

            androidMain.orNull?.dependsOn(this)
            jvmMain.orNull?.dependsOn(this)
            iosMain.orNull?.dependsOn(this)

            dependencies {
                implementation(libs.locale)
            }
        }
    }
}

android {
    compileSdk = 35
    namespace = "dev.datlag.tolgee.compose"

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_17
    }
}