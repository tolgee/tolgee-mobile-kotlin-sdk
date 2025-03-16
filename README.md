# Kotlin Multiplatform and Android package for Tolgee

🚨🚨🚨This package is currently under heavy development and will be released under alpha versions until stable and properly tested by pilot users.🚨🚨🚨

[![Tolgee](https://img.shields.io/badge/Tolgee-f06695?style=for-the-badge)](https://tolgee.io/) ![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-Supported-green?style=for-the-badge) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Supported-green?style=for-the-badge)

A flexible Gradle plugin and runtime library for integrating [Tolgee translations](https://tolgee.io) into **Kotlin Multiplatform** and **Compose** projects.

## Gradle plugin

Comes with a convenient task to pull your latest translations directly into your resources folder.

### Setup

Using Version Catalog is highly recommended to keep your versions aligned.

```toml
[plugins]
tolgee = { id = "dev.datlag.tolgee", version.ref = "tolgee" }
```

**Configuration**

You can change the plugin behavior to your needs:

```kotlin
tolgee {
    // REQUIRED
    apiKey.set("<YOUR TOLGEE APIKEY WITH TRANSLATION READ ACCESS>") // or use the 'tolgee.apikey=' property instead
    
    // more options
    pull { ... }
    push { ... }
    
    // change compile time behavior
    compilerPlugin {
        android {
            // Replaces Context.getString occurrences with Context.getStringInstant
            replaceGetString.set(false) // default true
        }
    }
}
```

### Usage

Pull translations from Tolgee using the `pullTranslation` Gradle task.  
Push local translations to Tolgee using the `pushTranslation` Gradle task.

## Core

This Kotlin Multiplatform library provides runtime support for Tolgee translations in your app.  
No longer creating a new release just to update your strings.

###  Setup

Using Version Catalog is highly recommended to keep your versions aligned.

```toml
[libraries]
tolgee = { group = "dev.datlag.tolgee", name = "core", version.ref = "tolgee" }
```

### Usage

Simply create a `Tolgee` singleton or multiple instances, using an API Key and/or a content delivery url.

#### Content Delivery

Content Delivery supports JSON only and can be used with any formatting option.

```kotlin
/** Thread safe: Retrieve the current singleton or create one. */
val tolgee = Tolgee.instanceOrInit {
    apiKey = "<API KEY>"
    contentDelivery("<ContentDelivery URL>") {
        format(Tolgee.Formatter.ICU) // default formatting
        format(Tolgee.Formatter.Sprintf) // for sprintf or Java.format formatting
    }
}

/** Updates the text automatically when loaded from API or locale changed. */
val updatingText: Flow<String> = tolgee.translation("key")

/** Returns the text that's currently loaded from API. */
/** Requires `tolgee.preload` or `tolgee.translation` call else always null. */
val currentText: String? = tolgee.instant("key")
```

## Compose

###  Setup

Using Version Catalog is highly recommended to keep your versions aligned.

```toml
[libraries]
tolgee = { group = "dev.datlag.tolgee", name = "compose", version.ref = "tolgee" }
```

### Usage

```
@Composable
fun SimpleText() {
    Text(text = stringResource(tolgee, Res.string.about))
}

@Composable
fun ArgsSupported(vararg args: Any) {
    Text(text = stringResource(tolgee, Res.string.about, *args))
}
```

#### Jetpack Compose?

What if you are using Jetpack Compose (Android only) or some explicit strings in your android source?  
No problem! This is handled as well.

```kotlin
@Composable
fun AndroidOnly() {
    Text(text = i18n.stringResource(R.string.android_string))
}

@Composable
fun AndroidWithArgs(vararg args: Any) {
    Text(text = i18n.stringResource(R.string.android_string, *args))
}
```

## Support the project

[![Github-sponsors](https://img.shields.io/badge/sponsor-30363D?style=for-the-badge&logo=GitHub-Sponsors&logoColor=#EA4AAA)](https://github.com/sponsors/DATL4G)
[![PayPal](https://img.shields.io/badge/PayPal-00457C?style=for-the-badge&logo=paypal&logoColor=white)](https://paypal.me/datlag)

### This is a non-profit project!

Sponsoring to this project means sponsoring to all my projects!
So the further text is not to be attributed to this project, but to all my apps and libraries.

Supporting this project helps to keep it up-to-date. You can donate if you want or contribute to the project as well.
This shows that the library is used by people, and it's worth to maintain.
