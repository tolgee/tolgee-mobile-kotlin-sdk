# Tolgee

An easy Tolgee integration for your **Compose Multiplatform** or **Jetpack Compose (Android)** project.  
It can handle multiplatform `StringResources` as well as android `StringRes` and **formatting**.

## Setup

**VERSION CATALOG**

Choose whatever you need, the Kodein module exposes the Compose module as well.

```toml
[libraries]
tolgee-compose = { group = "dev.datlag.tolgee", name = "tolgee-compose", version.ref = "tolgee" }
tolgee-kodein = { group = "dev.datlag.tolgee", name = "tolgee-kodein", version.ref = "tolgee" }

[plugins]
tolgee = { id = "dev.datlag.tolgee", version.ref = "tolgee" }
```

## Compose

Simply create an I18N object, call it's `stringResource` method and the library will take care of your translations automatically.

```kotlin
val i18n = I18N {
    config {
        client(ktorClient)
    }
    contentDelivery("<YOUR TOLGEE CONTENT DELIVERY URL>")
}

@Composable
fun Component() {
    Text(text = i18n.stringResource(Res.string.about))
}

@Composable
fun ArgsSupported(vararg args: Any) {
    Text(text = i18n.stringResource(Res.string.about, *args))
}
```

## Kodein

Are you using Kodein as dependency injection? Perfect!  
Just bind an instance in your container (preferably singleton to take advantage of caching) and let the lib handle everything else.

```kotlin
@Composable
fun Component() {
    Text(text = kodeinStringResource(Res.string.about))
}

@Composable
fun ArgsSupported(vararg args: Any) {
    Text(text = kodeinStringResource(Res.string.about, *args))
}
```

## Gradle Plugin

The Gradle Plugin provides a task to pull your translations, perfect to run before building a new release.

```kotlin
tolgee {
    // REQUIRED
    apiKey.set("<YOUR TOLGEE APIKEY WITH TRANSLATION READ ACCESS>") // or use the 'tolgee.apikey=' property instead
    projectId.set("<YOUR TOLGEE PROJECT ID>")
    
    // and more options
}
```

Then just call the pull task:

`./gradlew your-module:pullTranslation`