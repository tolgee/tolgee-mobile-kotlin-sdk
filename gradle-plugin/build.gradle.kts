import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `kotlin-dsl`
    kotlin("jvm")
    id("java-gradle-plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.dokka)
    alias(libs.plugins.serialization)
    alias(libs.plugins.vanniktech.publish)
    `maven-publish`
    signing
}

val libGroup = "io.tolgee"
val libName = "gradle-plugin"

group = libGroup
version = libVersion

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = libVersion
    }
}

dokka {
    moduleName.set("Gradle Plugin")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(file("src"))
            remoteUrl("https://github.com/tolgee/tolgee-mobile-kotlin-sdk/tree/master/gradle-plugin/src")
        }
    }
}

ktorfit {
    kotlinVersion.set("-")
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    testImplementation(kotlin("test"))
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.android.tools)

    implementation(libs.kommand)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktorfit)
    implementation(libs.semver)
    implementation(libs.serialization)
    implementation(libs.serialization.json)
    implementation(libs.serialization.kaml)
    implementation(libs.tooling)
}

gradlePlugin {
    plugins {
        website.set("https://github.com/tolgee/tolgee-mobile-kotlin-sdk")
        vcsUrl.set("https://github.com/tolgee/tolgee-mobile-kotlin-sdk")

        create("tolgeePlugin") {
            id = libGroup
            implementationClass = "io.tolgee.TolgeePlugin"
            displayName = "Tolgee Plugin"
            description = "Gradle Plugin for Tolgee"
        }
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

        description.set("Compose Multiplatform localization wrapper for Tolgee")
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