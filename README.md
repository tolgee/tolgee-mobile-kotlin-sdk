# Compose Tolgee

[![Tolgee](https://img.shields.io/badge/Tolgee-f06695?style=for-the-badge)](https://tolgee.io/) ![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-Supported-green?style=for-the-badge) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Supported-green?style=for-the-badge)

A flexable Gradle plugin and runtime library for integrating [Tolgee translations](https://tolgee.io) into **Compose Multiplatform** (and Jetpack Compose) and other Gradle based projects.

## Gradle plugin

You are **NOT REQUIRED** to use the Gradle plugin, it just comes with a convenient task to pull your latest translations directly into your resources folder.  
This is also compose **independent**, means you can use it in any project which uses Gradle.

### Setup

**Version Catalog**

Don't forget to apply it in your module.

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
    projectId.set("<YOUR TOLGEE PROJECT ID>")
    
    // and more options
}
```

### Usage

After properly configuring the plugin you just call the `pullTranslation` Gradle task and your resources will be updated.

## Compose

This library provides runtime support for Tolgee translations in your app.  
No longer creating a new release just to update your strings, thanks to Tolgee content delivery.

### Requirements

You have to enable content delivery in your project on Tolgee.  

#### Supported formats

- Structured Json
  - Java `String.format` (C-sprintf should work as well)
  - Other formats require your own `I18N.Format.Custom` implementation
- Flat Json
  - Formatting requires your own `I18N.Format.Custom` implementation

###  Setup

```toml
[libraries]
tolgee-compose = { group = "dev.datlag.tolgee", name = "compose", version.ref = "tolgee" }
```

### Usage

Simply call the `I18N.stringResource` instead of the default `stringResource` method and the library takes care of fetching, caching and even formatting.  
Of course it uses your local resources as default and fallback if fetching fails.

```kotlin
val i18n = I18N {
    config {
        client(ktorClient)
    }
    contentDelivery("<YOUR TOLGEE CONTENT DELIVERY URL>")
}

@Composable
fun SimpleText() {
    Text(text = i18n.stringResource(Res.string.about))
}

@Composable
fun ArgsSupported(vararg args: Any) {
    Text(text = i18n.stringResource(Res.string.about, *args))
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

## Kodein

Are you using [Kodein](https://github.com/kosi-libs/Kodein) as dependency injection? Perfect!  

###  Setup

```toml
[libraries]
tolgee-kodein = { group = "dev.datlag.tolgee", name = "compose-kodein", version.ref = "tolgee" }
```

### Usage

Just bind an instance in your container (preferably singleton to take advantage of caching) and let the library handle everything else.

```kotlin
@Composable
fun SimpleText() {
    Text(text = kodeinStringResource(Res.string.about))
}

@Composable
fun ArgsSupported(vararg args: Any) {
    Text(text = kodeinStringResource(Res.string.about, *args))
}
```

> [!NOTE]
> Why `kodeinStringResource` instead of `i18nStringResource`?
>
> Calling `kodeinStringResource` may be confusing first but is based on the decision to be compatible to other dependecy injections.
> If you migrate from one DI to another it's hard to tell which one is used when both expose an `i18nStringResource`.

## Support the project

[![Github-sponsors](https://img.shields.io/badge/sponsor-30363D?style=for-the-badge&logo=GitHub-Sponsors&logoColor=#EA4AAA)](https://github.com/sponsors/DATL4G)
[![PayPal](https://img.shields.io/badge/PayPal-00457C?style=for-the-badge&logo=paypal&logoColor=white)](https://paypal.me/datlag)

### This is a non-profit project!

Sponsoring to this project means sponsoring to all my projects!
So the further text is not to be attributed to this project, but to all my apps and libraries.

Supporting this project helps to keep it up-to-date. You can donate if you want or contribute to the project as well.
This shows that the library is used by people, and it's worth to maintain.
