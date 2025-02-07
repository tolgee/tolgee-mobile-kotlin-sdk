import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.serialization)
    alias(libs.plugins.vanniktech.publish)
    `maven-publish`
    signing
}

val libGroup = "dev.datlag.tolgee"
val libName = "core"

group = libGroup
version = libVersion

dokka {
    moduleName.set("Core")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(file("src"))
            remoteUrl("https://github.com/DatL4g/compose-tolgee/tree/master/core/src")
        }
    }
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

mavenPublishing {
    publishToMavenCentral(host = SonatypeHost.S01, automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = libGroup,
        artifactId = libName,
        version = libVersion
    )

    pom {
        name.set(libName)

        description.set("Kotlin Multiplatform localization wrapper for Tolgee")
        url.set("https://github.com/DatL4g/compose-tolgee")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        scm {
            url.set("https://github.com/DatL4g/compose-tolgee")
            connection.set("scm:git:git://github.com/DatL4g/compose-tolgee.git")
        }

        developers {
            developer {
                id.set("DatL4g")
                name.set("Jeff Retz (DatLag)")
                url.set("https://github.com/DatL4g")
            }
        }
    }
}