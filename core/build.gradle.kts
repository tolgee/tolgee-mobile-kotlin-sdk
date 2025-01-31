import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }

    /*androidNativeX64()
    androidNativeX86()
    androidNativeArm64()
    androidNativeArm32()*/

    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()

    watchosX64()
    watchosArm64()
    watchosArm32()
    watchosSimulatorArm64()

    macosX64()
    macosArm64()

    linuxX64()
    // linuxArm64()

    mingwX64()

    js(IR) {
        nodejs()
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        browser()
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.immutable)
            implementation(libs.ktor)
            implementation(libs.ktorfit)
            implementation(libs.serialization.json)
            implementation(libs.tooling)

            // Does not support androidNative and linuxArm64 yet (https://github.com/comahe-de/i18n4k/pull/75)
            implementation(libs.i18n4k)
        }

        androidMain.dependencies {
            implementation(libs.android)

            implementation(libs.ktor.android)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.java)
        }

        appleMain.dependencies {
            implementation(libs.ktor.darwin)
        }

        linuxMain.dependencies {
            implementation(libs.ktor.curl)
        }

        mingwMain.dependencies {
            implementation(libs.ktor.winhttp)
        }

        jsMain.dependencies {
            implementation(libs.ktor.js)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.js)
        }
    }
}

android {
    compileSdk = 35
    namespace = "dev.datlag.tolgee.core"

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_17
    }
}