# Development Guidelines for Tolgee Mobile Kotlin SDK

This document provides essential information for developers working on the Tolgee Mobile Kotlin SDK project.

## Build/Configuration Instructions

### Project Structure

The project consists of several modules:

- **core**: The Kotlin Multiplatform library providing runtime support for Tolgee translations
- **compose**: Compose Multiplatform integration for Tolgee
- **compiler-plugin**: Kotlin compiler plugin for Tolgee
- **gradle-plugin**: Gradle plugin for integrating Tolgee into projects
- **demo**: Demo applications showcasing Tolgee usage

### Building the Project

1. **Prerequisites**:
   - JDK 11 or higher
   - Kotlin 1.9.0 or higher
   - Android SDK (for Android targets)
   - Xcode (for Apple targets)

2. **Building from the command line**:
   ```bash
   ./gradlew build
   ```

3. **Building specific modules**:
   ```bash
   ./gradlew :core:build
   ./gradlew :compose:build
   ./gradlew :gradle-plugin:build
   ./gradlew :compiler-plugin:build
   ```

4. **Publishing to Maven Local** (for local testing):
   ```bash
   ./gradlew publishToMavenLocal
   ```
   You may need to disable signing temporarily as it is enforced by default.

## Testing Information

Testing infrastructure is in a relatively ok state, but there are no tests at the moment.

### Running Tests

Tests can be run using the Gradle test task:

```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :gradle-plugin:test
./gradlew :core:test
./gradlew :compose:test
./gradlew :compiler-plugin:test

# Run a specific test class
./gradlew :gradle-plugin:test --tests "TolgeeTest"

# Run a specific test method
./gradlew :gradle-plugin:test --tests "TolgeeTest.tolgee cli version check"
```

### Adding New Tests

1. **Create a test file** in the appropriate module's test directory:
   - For JVM modules: `<module>/src/test/kotlin/`
   - For Android modules: `<module>/src/androidTest/kotlin/`
   - For multiplatform modules: `<module>/src/commonTest/kotlin/` (or platform-specific test directories)

2. **Test structure example**:

```kotlin
class MyTest {

    @Test
    fun `test some functionality`() {
        // Test implementation
        assertEquals(expected, actual, "Error message")
    }
}
```

3. **Running your new test**:
   ```bash
   ./gradlew :<module>:test --tests "MyTest"
   ```

### Test Example

Here's a simple test example that was created and verified to work:

```kotlin
class SimpleTest {

    @Test
    fun `simple addition test`() {
        // A simple test to demonstrate testing in this project
        assertEquals(4, 2 + 2, "Basic addition should work correctly")
    }
}
```

## Additional Development Information

### Code Style

- The project follows Kotlin coding conventions
- Use 4 spaces for indentation
- The maximum line length is 120 characters (not fully enforced at the moment)
- Use trailing commas in parameter lists and collection literals that span multiple lines

### Multiplatform Considerations

- Common code should be placed in `commonMain` source sets
- Platform-specific code should be placed in the appropriate platform-specific source sets:
  - `androidMain` for Android
  - `jvmMain` for JVM
  - `appleMain` for Apple platforms (iOS, macOS, etc.)
  - `jsMain` for JavaScript
  - etc.

### Dependency Management

- Dependencies are managed through Version Catalog in `gradle/libs.versions.toml`
- When adding new dependencies, add them to the Version Catalog rather than directly in build scripts

### Publishing

The project uses the Vanniktech Maven Publish plugin for publishing to Maven Central:

```kotlin
mavenPublishing {
    publishToMavenCentral(host = SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = "io.tolgee",
        artifactId = "module-name",
        version = "version"
    )
}
```

### API Compatibility

The project uses the Binary Compatibility Validator plugin to ensure API compatibility. After making changes to public APIs, run:

```bash
./gradlew apiDump
```

This will update the API dump files in the `api` directory of each module. These files should be committed to the repository.
