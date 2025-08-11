# Tolgee Core Module

This Kotlin Multiplatform library provides runtime support for Tolgee translations in your app.
With Tolgee, you can update your translations over-the-air without releasing a new app version.

## Features

- **Over-the-air updates**: Update your translations without releasing a new app version
- **Multiple format support**:
  - Sprintf (Android SDK) formatting
  - ICU (Tolgee Native Flat JSON) formatting
- **Kotlin Multiplatform**: Designed with multiplatform support in mind
- **Android integration**: Seamless integration with Android resources
- **Dynamic locale switching**: Change languages at runtime

## Setup

Using Version Catalog is highly recommended to keep your versions aligned.

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

### Android specific steps

Create a network security config file `network_security.xml` in your `res/xml` folder:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config xmlns:android="http://schemas.android.com/apk/res/android">
    <domain-config>
        <domain includeSubdomains="true">tolgee.io</domain>
        <domain includeSubdomains="true">tolg.ee</domain>
    </domain-config>
</network-security-config>
```

Add network security config to your `AndroidManifest.xml`:

```xml
<application
        android:networkSecurityConfig="@xml/network_security"> <!-- Add this line to your existing application tag -->
</application>
```

## Usage

### Initialization

#### Android

Initialize Tolgee in your Application class:

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

#### Other Platforms

For non-Android platforms, initialization is similar but without the Android-specific storage provider:

```kotlin
fun initTolgee() {
    Tolgee.init {
        contentDelivery {
            url = "https://cdn.tolg.ee/your-cdn-url-prefix"
            // Create a custom storage provider for caching the latest translations from CDN if needed
        }
    }
}
```

### Basic Usage

#### Getting Translations

```kotlin
// Get the Tolgee instance
val tolgee = Tolgee.instance

// Get a translation (returns null if not loaded yet)
val text: String? = tolgee.t("key")

// Get a translation with parameters
val textWithParams: String? = tolgee.t("key_with_param", mapOf("param" to "value"))

// Get a translation as a Flow (updates automatically when translations change)
val textFlow: Flow<String> = tolgee.tFlow("key")
textFlow.collect { text ->
    // Use the text (e.g., update UI)
}
```

#### Android-Specific Usage

```kotlin
// Get a translation with fallback to Android resources
val text = tolgee.t(context, R.string.string_key)

// Get a translation with fallback to Android resources with parameters
val textWithParams = tolgee.t(context, R.string.string_with_params, "param1", "param2")

// Get a translation with fallback to Android resources as a Flow
val textFlow = tolgee.tFlow(context, R.string.string_key)
```

### Locale Management

```kotlin
// Set locale
tolgee.setLocale("en")

// Get current locale
val locale = tolgee.getLocale()

// Listen for changes
tolgee.changeFlow.collect {
    // Locale or available translations changed, update UI if needed
}
```

### Advanced Configuration

#### Formatter Configuration

```kotlin
Tolgee.init {
    contentDelivery {
        url = "https://cdn.tolg.ee/your-cdn-url-prefix"
        // Configure formatters for parsing translations from CDN
        format(Tolgee.Formatter.Sprintf) // Android SDK formatting (default)
        // format(Tolgee.Formatter.ICU) // Tolgee Native Flat JSON formatting
    }
}
```

#### Preloading Translations

```kotlin
// Preload translations for the current locale from Activity
override fun onStart() {
  super.onStart()
  tolgee.preload(this)
}
```

## Troubleshooting

### Translations Not Loading

- Ensure you have the correct Content Delivery URL
- Check that your storage provider is properly configured
- Make sure you're calling `Tolgee.init` before accessing translations
- Use `tolgee.preload` or `tolgee.tFlow` before calling `tolgee.t` to ensure translations are loaded

### Locale Issues

- Verify that your project supports the locale code you're using
- Check that translations for the selected locale exist in your Tolgee project
- Use `tolgee.changeFlow` to monitor locale changes and update your UI accordingly

### Android Integration Issues

- Ensure that the `TolgeeStorageProviderAndroid` is properly initialized with the context and version code which changes with each app update
- Check that your Android resources are properly structured - Tolgee is using `resources.getResourceEntryName` to find key for the resource

## Example Projects

For complete examples of how to use the Tolgee Core module, check out the demo projects:

- [Example Android](../demo/exampleandroid) - Traditional Android Views example