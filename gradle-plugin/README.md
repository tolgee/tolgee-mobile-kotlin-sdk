# Tolgee Gradle Plugin

This Gradle plugin adds a Kotlin compiler plugin that automatically transforms existing code to use Tolgee
without requiring manual code changes. This is especially useful for integrating Tolgee into existing
projects with minimal effort.

## Features

- **Automatic Code Transformation**: Transforms standard string resource calls to Tolgee equivalents at compile time
- **Non-Invasive Integration**: No need to modify your existing code
- **Configurable Behavior**: Fine-tune which transformations are applied
- **Android Support**: Seamlessly works with an Android resource system
- **Compose Support**: Compatible with both Jetpack Compose and Compose Multiplatform

## Setup

Using Version Catalog is highly recommended to keep your versions aligned.

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

## Usage

### Basic Usage

Simply apply the plugin to your project, and it will automatically transform compatible code
to use Tolgee at compile time. No additional configuration is required for basic usage.

### Configuration

You can customize the plugin's behavior using the `tolgee` extension in your build script:

```kotlin
// build.gradle.kts
tolgee {
    // Configure the compiler plugin
    compilerPlugin {
        // Android-specific configuration
        android {
            // Replace Context.getString calls with Tolgee equivalents
            replaceGetString.set(true) // default: true

            // Replace Context.getQuantityString calls with Tolgee equivalents
            replacePluralString.set(true) // default: true
        }
        compose {
            // Replace androidx.compose.ui.res.stringResource and
            // org.jetbrains.compose.resources.stringResource calls with Tolgee equivalents
            stringResource.set(true) // default: true

            // Replace androidx.compose.ui.res.pluralStringResource and
            // org.jetbrains.compose.resources.pluralStringResource calls with Tolgee equivalents
            pluralStringResource.set(true) // default: true
        }
    }
}
```

## How It Works

The compiler plugin transforms code at compile time, replacing standard string resource access
methods with their Tolgee equivalents:

### Android Transformations

#### Before Transformation

```kotlin
// Standard Android getString
val text = context.getString(R.string.welcome_message)

// Standard Android getString with formatting
val formattedText = context.getString(R.string.welcome_user, username)

// Standard Android plural string
val pluralText = context.getQuantityString(R.plurals.item_count, count, count)
```

#### After Transformation

```kotlin
// Transformed to use Tolgee
val text = Tolgee.instance.t(context, R.string.welcome_message)

// Transformed with formatting
val formattedText = Tolgee.instance.t(context, R.string.welcome_user, username)

// Transformed plural
val pluralText = Tolgee.instance.t(context, R.plurals.item_count, count, count)
```

### Compose Transformations

#### Before Transformation

```kotlin
// Standard Compose stringResource
@Composable
fun WelcomeText() {
    Text(text = stringResource(R.string.welcome_message))
}

// With formatting
@Composable
fun UserWelcome(username: String) {
    Text(text = stringResource(R.string.welcome_user, username))
}

// Plurals
@Composable
fun ItemCount(count: Int) {
    Text(text = pluralStringResource(R.plurals.item_count, count, count))
}
```

#### After Transformation

```kotlin
// Transformed to use Tolgee
@Composable
fun WelcomeText() {
    Text(text = io.tolgee.stringResource(R.string.welcome_message))
}

// With formatting
@Composable
fun UserWelcome(username: String) {
    Text(text = io.tolgee.stringResource(R.string.welcome_user, username))
}

// Plurals
@Composable
fun ItemCount(count: Int) {
    Text(text = io.tolgee.pluralStringResource(R.plurals.item_count, count, count))
}
```

## Troubleshooting

### Plugin Not Applied

- Ensure the plugin is correctly declared in your build script
- Check that the plugin version matches your other Tolgee dependencies
- Verify that the Kotlin compiler version is compatible

### Transformations Not Working

- Make sure the configuration options are set correctly
- Check that the code you expect to be transformed matches the patterns the plugin looks for
- Verify that you're using the standard Android or Compose string resource methods
- Note: Compiler plugin can only replace calls made from Kotlin code (Java code won't be transformed)
- Note: Compiler plugin can't transform calls which are made deep within Android OS—such calls can usually only be modified by wrapping Context using `TolgeeContextWrapper`

### Build Errors

- If you encounter build errors after applying the plugin, try disabling specific transformations to isolate the issue
- Check the Kotlin compiler version compatibility
- Ensure all Tolgee dependencies are correctly configured

## Example Projects

For complete examples of how to use the Tolgee Gradle Plugin, check out the demo projects:

- [Example Android](../demo/exampleandroid) - Traditional Android Views example with compiler plugin