plugins {
    `kotlin-dsl` version "5.1.2"
    kotlin("jvm")
    id("java-gradle-plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
}

dependencies {
    implementation(libs.kotlin.gradle.plugin.api)

    implementation(libs.ktor)
    implementation(libs.ktorfit)
}