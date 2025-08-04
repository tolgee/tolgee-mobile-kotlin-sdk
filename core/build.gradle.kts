import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.native.coroutines)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.dokka)
    alias(libs.plugins.serialization)
    alias(libs.plugins.vanniktech.publish)
    `maven-publish`
    signing
}

val libGroup = "io.tolgee"
val libName = "core"
val appleFramework = "Tolgee"

group = libGroup
version = libVersion

dokka {
    moduleName.set("Core")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(file("src"))
            remoteUrl("https://github.com/tolgee/tolgee-mobile-kotlin-sdk/tree/master/core/src")
        }
    }
}

nativeCoroutines {
    k2Mode = true
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
    jvmToolchain(21)

    val xcf = XCFramework(appleFramework)
    cocoapods {
        name = appleFramework
        version = libVersion
        license = "Apache License 2.0"
        homepage = "https://github.com/tolgee/tolgee-mobile-kotlin-sdk"
        summary = "Kotlin Multiplatform localization wrapper for Tolgee"

        framework {
            baseName = appleFramework
        }
    }

    iosX64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    iosArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    iosSimulatorArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }

    tvosX64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    tvosArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    tvosSimulatorArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }

    watchosX64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    watchosArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    watchosArm32 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    watchosSimulatorArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }

    macosX64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    macosArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }

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
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }

        commonMain.dependencies {
            implementation(libs.immutable)
            implementation(libs.ktor)
            implementation(libs.serialization.json)
            implementation(libs.tooling)

            // Does not support androidNative and linuxArm64 yet (https://github.com/comahe-de/i18n4k/pull/75)
            api(libs.i18n4k)
            implementation(libs.i18n4k.plural)
            implementation(libs.datetime)
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
    namespace = "io.tolgee.core"

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_21
    }
}

mavenPublishing {
    publishToMavenCentral(host = SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = libGroup,
        artifactId = libName,
        version = libVersion
    )

    pom {
        name.set(libName)

        description.set("Kotlin Multiplatform localization wrapper for Tolgee")
        url.set("https://github.com/tolgee/tolgee-mobile-kotlin-sdk")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        scm {
            url.set("https://github.com/tolgee/tolgee-mobile-kotlin-sdk")
            connection.set("scm:git:git://github.com/tolgee/tolgee-mobile-kotlin-sdk.git")
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