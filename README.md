# Tolgee Mobile Kotlin SDK (Alpha) 🐁

🚨🚨🚨This package is still under development and the API is not yet stable.
Feel free to use it, but there might be API breakage between releases.🚨🚨🚨

[![Tolgee](https://img.shields.io/badge/Tolgee-f06695)](https://tolgee.io/)
![Android](https://img.shields.io/badge/Android-Supported-green?logo=android)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Supported-green?logo=jetpackcompose)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-Supported-green?logo=kotlin)
![language](https://img.shields.io/github/languages/top/tolgee/tolgee-mobile-kotlin-sdk)
[![github release](https://img.shields.io/github/v/release/tolgee/tolgee-mobile-kotlin-sdk?label=GitHub%20Release)](https://github.com/tolgee/tolgee-mobile-kotlin-sdk/releases/latest)
[![licence](https://img.shields.io/badge/license-Apache%202%20-blue)](https://github.com/tolgee/tolgee-mobile-kotlin-sdk/blob/master/LICENSE)
[![github stars](https://img.shields.io/github/stars/tolgee/tolgee-mobile-kotlin-sdk?style=social&label=Tolgee%20Mobile%20Kotlin%20SDK)](https://github.com/tolgee/tolgee-mobile-kotlin-sdk)
[![github stars](https://img.shields.io/github/stars/tolgee/tolgee-platform?style=social&label=Tolgee%20Platform)](https://github.com/tolgee/tolgee-platform)
[![Github discussions](https://img.shields.io/github/discussions/tolgee/tolgee-platform)](https://github.com/tolgee/tolgee-platform/discussions)
[![Dev.to](https://img.shields.io/badge/Dev.to-tolgee_i18n?logo=devdotto&logoColor=white)](https://dev.to/tolgee_i18n)
[![Read the Docs](https://img.shields.io/badge/Read%20the%20Docs-8CA1AF?logo=readthedocs&logoColor=fff)](https://docs.tolgee.io/)
[![Slack](https://img.shields.io/badge/Slack-4A154B?logo=slack&logoColor=fff)](https://tolg.ee/slack)
[![YouTube](https://img.shields.io/badge/YouTube-%23FF0000.svg?logo=YouTube&logoColor=white)](https://www.youtube.com/@tolgee)
[![LinkedIn](https://custom-icon-badges.demolab.com/badge/LinkedIn-0A66C2?logo=linkedin-white&logoColor=fff)](https://www.linkedin.com/company/tolgee/)
[![X](https://img.shields.io/badge/X-%23000000.svg?logo=X&logoColor=white)](https://x.com/Tolgee_i18n)

## What is Tolgee?

[Tolgee](https://tolgee.io/) is a powerful localization platform that simplifies the translation process for your applications.
This SDK provides integration for Kotlin-based projects, with a primary focus on Android.

Currently, Android is fully supported, but any Kotlin-based codebase can in theory use this library.

## Features

- **Over-the-air updates**: Update your translations without releasing a new app version
- **Multiple format support**:
  - Sprintf (Android SDK) formatting
  - ICU (Tolgee Native Flat JSON) formatting
- **Compose integration**: Full integration with Jetpack Compose and Compose Multiplatform
- **Compiler plugin**: Automatically transform existing code to use Tolgee without manual changes
- **Kotlin Multiplatform**: Designed with multiplatform support in mind

## Modules

The SDK is split into multiple modules, each serving a specific purpose:

- **[Core](./core/README.md)**: Base library for fetching translations from CDN and querying them
- **[Compose](./compose/README.md)**: Extension for using the library with Jetpack Compose or Compose Multiplatform
- **[Gradle Plugin](./gradle-plugin/README.md)**: Gradle plugin for integrating and configuring the compiler plugin

## Which Module Should I Use?

- If you are using **traditional Android Views**, use the [Core](./core/README.md) module
- If you are using **Jetpack Compose** or **Compose Multiplatform**, use the [Compose](./compose/README.md) module
- If you want to **automatically transform existing code** to use Tolgee, add the [Gradle Plugin](./gradle-plugin/README.md)

## Installation

Using Version Catalog is highly recommended to keep your versions aligned.

### Core Module (Traditional Android)

```toml
# gradle/libs.versions.toml
[libraries]
tolgee = { group = "io.tolgee.mobile-kotlin-sdk", name = "core", version.ref = "tolgee" }
```

```kotlin
// build.gradle.kts
dependencies {
    implementation(libs.tolgee)
}
```

### Compose Module (Jetpack Compose or Compose Multiplatform)

```toml
# gradle/libs.versions.toml
[libraries]
tolgee = { group = "io.tolgee.mobile-kotlin-sdk", name = "compose", version.ref = "tolgee" }
```

```kotlin
// build.gradle.kts
dependencies {
    implementation(libs.tolgee)
}
```

### Gradle Plugin (Compiler Plugin)

```toml
# gradle/libs.versions.toml
[plugins]
tolgee = { id = "io.tolgee.mobile-kotlin-sdk", version.ref = "tolgee" }
```

```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.tolgee)
}
```

## Basic Usage

For detailed usage instructions, please refer to the module-specific documentation:

- [Core Module Documentation](./core/README.md)—For traditional Android and base functionality
- [Compose Module Documentation](./compose/README.md)—For Jetpack Compose and Compose Multiplatform
- [Gradle Plugin Documentation](./gradle-plugin/README.md)—For compiler plugin configuration

### Quick Start

Here's a quick example of initializing Tolgee in an Android application:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Tolgee.init {
            contentDelivery {
                url = "https://cdn.tolg.ee/your-cdn-url-prefix"
                storage = TolgeeStorageProviderAndroid(this@MyApplication, BuildConfig.VERSION_CODE)
            }
        }
    }
}
```

## Example Projects

For complete examples of how to use the Tolgee SDK, check out the demo projects:

- [Example Android](./demo/exampleandroid)—Traditional Android Views example
- [Example Jetpack](./demo/examplejetpack)—Jetpack Compose example
- [Multiplatform Compose](./demo/multiplatform-compose)—Compose Multiplatform example

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0—see the [LICENSE](LICENSE) file for details.

----
🧀
