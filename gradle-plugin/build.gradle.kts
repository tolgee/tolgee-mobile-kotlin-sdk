import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `kotlin-dsl`
    kotlin("jvm")
    id("java-gradle-plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.vanniktech.publish)
    `maven-publish`
    signing
}

val libGroup = "dev.datlag.tolgee"
val libName = "gradle-plugin"
val libVersion = "0.1.3"

group = libGroup
version = libVersion

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.android)

    implementation(libs.ktor.okhttp)
    implementation(libs.ktorfit)
    implementation(libs.tooling)
}

gradlePlugin {
    plugins {
        website.set("https://github.com/DatL4g/compose-tolgee")
        vcsUrl.set("https://github.com/DatL4g/compose-tolgee")

        create("tolgeePlugin") {
            id = libGroup
            implementationClass = "dev.datlag.tolgee.TolgeePlugin"
            displayName = "Tolgee Plugin"
            description = "Gradle Plugin for Tolgee"
        }
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

        description.set("Compose Multiplatform localization wrapper for Tolgee")
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