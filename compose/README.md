# Tolgee Compose Module

This module provides seamless integration between Tolgee and Compose (both Jetpack Compose for Android and Compose Multiplatform).
It allows you to use Tolgee translations directly in your Compose UI with minimal effort.

## Features

- **Compose Integration**: Use Tolgee translations in your Compose UI with familiar APIs
- **Android Resource Support**: Seamless integration with Android string resources
- **Compose Multiplatform Support**: Works with both Jetpack Compose and Compose Multiplatform
- **Parameter Support**: Pass parameters to your translations
- **Plural Support**: Handles plural forms
- **Reactive Updates**: UI automatically updates when translations or locale change

## Setup

Using Version Catalog is highly recommended to keep your versions aligned.

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

Initialize Tolgee in your Application class (Android) or at the entry point of your application (Multiplatform):

#### Android (Jetpack Compose)

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

#### Multiplatform

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

#### Jetpack Compose (Android)

```kotlin
@Composable
fun SimpleText() {
    // Use the stringResource extension function
    Text(text = stringResource(R.string.welcome_message))
}

@Composable
fun TextWithParameters(name: String) {
    // Pass parameters to your translations
    Text(text = stringResource(R.string.welcome_user, name))
}

@Composable
fun PluralText(count: Int) {
    // Handle plural forms
    Text(text = pluralStringResource(R.plurals.item_count, count, count))
}
```

#### Compose Multiplatform

```kotlin
@Composable
fun SimpleText() {
    // Use the stringResource extension function with Res
    Text(text = stringResource(Res.string.welcome_message))
}

@Composable
fun TextWithParameters(name: String) {
    // Pass parameters to your translations
    Text(text = stringResource(Res.string.welcome_user, name))
}

@Composable
fun PluralText(count: Int) {
    // Handle plural forms
    Text(text = pluralStringResource(Res.plurals.item_count, count, count))
}
```

### Advanced Usage

#### Explicit Tolgee Instance

If you need to use a specific Tolgee instance (not the singleton), you can pass it explicitly:

```kotlin
@Composable
fun ExplicitInstance() {
    val tolgee = remember { /* your custom Tolgee instance */ }

    Text(text = stringResource(tolgee, Res.string.welcome_message))
}
```

#### Locale Switching

You can create a locale switcher component:

```kotlin
@Composable
fun LocaleSwitcher() {
    val tolgee = Tolgee.instance
    val currentLocale = tolgee.changeFlow.mapLatest {
        tolgee.getLocale()
    }.collectAsState(initial = tolgee.getLocale())

    Row {
        Text(text = stringResource(tolgee, R.string.selected_locale, currentLocale.toTag("-")))
        Button(onClick = { tolgee.setLocale("en") }) {
            Text("English")
        }
        Button(onClick = { tolgee.setLocale("fr") }) {
            Text("Français")
        }
        Button(onClick = { tolgee.setLocale("cs") }) {
            Text("Čeština")
        }
    }
}
```

#### Observing Locale Changes

```kotlin
@Composable
fun LocaleAwareComponent() {
    val tolgee = Tolgee.instance

    // This will cause recomposition when the locale changes
    val currentLocale = tolgee.changeFlow.mapLatest {
        tolgee.getLocale()
    }.collectAsState(initial = tolgee.getLocale())

    // Your UI that depends on the current locale
    Text(text = currentLocale.toTag("-"))
}
```

## Troubleshooting

### Translations Not Updating

- Ensure you're using the `stringResource` and `pluralStringResource` functions from the Tolgee package
- Check that your Tolgee instance is properly initialized
- Verify that the translations are loaded correctly

### Android Resource Integration Issues

- Check that your Android resources are properly structured - Tolgee is using `resources.getResourceEntryName` to find key for the resource
- Ensure that the Tolgee instance has access to the Android context

### Compose Multiplatform Issues

- Verify that your resource files are correctly set up and all keys present in resources are present in a Tolgee platform too
- Ensure that you're using the correct resource references

## Example Projects

For complete examples of how to use the Tolgee Compose module, check out the demo projects:

- [Example Jetpack](../demo/examplejetpack) - Jetpack Compose example
- [Multiplatform Compose](../demo/multiplatform-compose) - Compose Multiplatform example