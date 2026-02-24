# AGENTS.md

Guidance for AI coding agents working on the Tolgee Mobile Kotlin SDK.

Main branch: `master`

## Project Structure

```
tolgee-mobile-kotlin-sdk/
├── core/              # KMP base library (translations, caching, CDN API)
├── compose/           # Compose Multiplatform integration
├── compiler-plugin/   # Kotlin compiler plugin (currently disabled)
├── gradle-plugin/     # Gradle plugin for project integration
└── demo/              # Example apps (Android Views, Jetpack Compose, KMP Compose)
```

The compiler plugin is commented out in `settings.gradle.kts` (broken after Kotlin 2.2).

## Building

```bash
./gradlew :core:build
./gradlew :compose:build
./gradlew :gradle-plugin:build
```

## Testing

Only the gradle-plugin module has tests currently:

```bash
./gradlew :gradle-plugin:test
./gradlew :gradle-plugin:test --tests "TolgeeTest"
```

CI (`test.yml`) runs gradle-plugin tests on every push. Requires Tolgee CLI (`npm install --global @tolgee/cli`).

## API Compatibility

The project uses the Binary Compatibility Validator plugin. After any changes to public APIs, run:

```bash
./gradlew apiDump
```

This updates `.api` dump files in each module's `api/` directory. These files **must be committed** with the change. The build will fail (`apiCheck`) if the dump is out of date.

## Module Architecture

### Core Module

Main entry point: `Tolgee` singleton class in `core/src/commonMain/kotlin/io/tolgee/Tolgee.kt`

Key components:
- `Tolgee` — Singleton with locale management, translation resolution, and configuration
- `TolgeeApi` — CDN communication and local caching
- `TolgeeTranslation` — Interface with ICU and sprintf formatting implementations
- `TolgeeStorageProvider` — Platform-specific persistent caching interface
- `TolgeeManifest` — Available locales metadata from CDN (internal)

Data flow:
```
tFlow("key", params)
  → localeFlow emits locale
  → loadManifest() fetches available locales from CDN
  → resolveLocale() applies progressive BCP 47 fallback (zh-Hans-CN → zh-Hans → zh)
  → loadTranslations() fetches from CDN or cache (LRU in-memory + persistent storage)
  → Translation formatted and emitted via Flow
```

Thread safety: `Mutex` for translation loading, `AtomicFU` for manifest cache.

### Compose Module

Composable wrappers around Core with graceful fallback to default Compose resources when Tolgee is not initialized. Provides `stringResource()`, `pluralStringResource()`, `stringArrayResource()`.

### Gradle Plugin

Build-time configuration DSL bridging Gradle with the compiler plugin. Configures replacement of `getString()` and `stringResource()` calls.

### Compiler Plugin (disabled)

Kotlin IR transformations to replace standard resource calls with Tolgee calls at compile-time. Currently disabled in `settings.gradle.kts`.

## Multiplatform

The core module targets 20+ platforms:

| Category | Targets |
|----------|---------|
| Android | androidTarget, androidNativeX64/X86/Arm64/Arm32 |
| JVM | jvm (toolchain 21) |
| Apple | iOS (x64, arm64, simulatorArm64), tvOS (×3), watchOS (×4), macOS (x64, arm64) |
| Other | linuxX64, linuxArm64, mingwX64, js (IR), wasmJs |

Source set hierarchy follows `applyDefaultHierarchyTemplate()`:
- `commonMain` — All shared code
- `androidMain` — Android-specific (Views integration, storage)
- `appleMain` — Apple platforms (iOS/macOS/tvOS/watchOS)
- `jvmMain` — JVM-specific
- `jsMain` / `wasmJsMain` — JS/WASM

## Key Design Patterns

- **Builder pattern** for configuration: `Tolgee.Config.Builder`, `ContentDelivery.Builder`, `Network.Builder`
- **Sealed interfaces** for type safety: `Formatter` (ICU/Sprintf), `TolgeeMessageParams` (None/Indexed/Mapped)
- **Reactive Flows**: `localeFlow`, `changeFlow`, `tFlow()` returning `Flow<String>`
- **Expect/actual** for platform-specific implementations (`platformHttpClient`, `platformStorage`, etc.)

## Dependencies

Managed via Version Catalog in `gradle/libs.versions.toml`. Add new dependencies there, not directly in build scripts.

Key libraries: Ktor (HTTP), kotlinx-serialization (JSON), kotlinx-coroutines (async), i18n4k (locale handling), AtomicFU (thread safety), SKIE (Swift interop for Apple targets).

## Publishing

Manual trigger via `publish.yml` workflow on macOS. Publishes to Maven Central via `publishAllPublicationsToMavenCentralRepository`. Requires signing keys and Sonatype credentials.
