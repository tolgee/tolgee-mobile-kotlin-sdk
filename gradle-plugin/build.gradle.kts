plugins {
    `kotlin-dsl`
    kotlin("jvm")
    id("java-gradle-plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
}

val artifact = "dev.datlag.tolgee"
group = artifact
version = "0.1.2"

dependencies {
    implementation(libs.kotlin.gradle.plugin.api)

    implementation(libs.ktor.okhttp)
    implementation(libs.ktorfit)
}

gradlePlugin {
    plugins {
        website.set("https://github.com/DatL4g/compose-tolgee")
        vcsUrl.set("https://github.com/DatL4g/compose-tolgee")

        create("tolgeePlugin") {
            id = artifact
            implementationClass = "dev.datlag.tolgee.TolgeePlugin"
            displayName = "Tolgee Plugin"
            description = "Gradle Plugin for Tolgee"
        }
    }
}
