import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm")
    kotlin("kapt")
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
    alias(libs.plugins.vanniktech.publish)
}

val libGroup = "dev.datlag.tolgee"
val libName = "compiler-plugin"

group = libGroup
version = libVersion

dokka {
    moduleName.set("Compiler Plugin")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(file("src"))
            remoteUrl("https://github.com/DatL4g/compose-tolgee/tree/master/compiler-plugin/src")
        }
    }
}

dependencies {
    compileOnly(libs.auto.service)
    kapt(libs.auto.service)

    compileOnly(libs.kotlin.compiler.embeddable)
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

        description.set("Compiler plugin for Tolgee translations.")
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