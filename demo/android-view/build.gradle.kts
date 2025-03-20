plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android)
}

android {
    namespace = "io.tolgee.demo.view"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.tolgee.demo.view"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.android)
    implementation(libs.activity)

    implementation(libs.coroutines.android)
    implementation(project(":core"))
}