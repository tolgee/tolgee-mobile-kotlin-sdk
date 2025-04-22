package io.tolgee

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import io.tolgee.model.Config

/**
 * A command-line processor for the Tolgee Kotlin compiler plugin.
 *
 * This processor enables or disables specific plugin features based on command-line options.
 * Currently, it supports toggling the replacement of `Context.getString` calls with
 * Tolgee localization method.
 *
 * @see CommandLineProcessor
 */
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class TolgeeCommandLineProcessor : CommandLineProcessor {

    /**
     * The unique plugin identifier used to register this command-line processor.
     */
    override val pluginId: String = "tolgee"

    /**
     * A collection of supported command-line options for this plugin.
     *
     * - `tolgee.android.getString`: Enables or disables `getString` replacements in Android projects.
     */
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = KEY_ANDROID_GET_STRING_ENABLED.toString(),
            valueDescription = "<true|false>",
            description = "Enable or disable getString replacements on Android.",
            required = false
        ),
        CliOption(
            optionName = KEY_COMPOSE_STRING_RESOURCE_ENABLED.toString(),
            valueDescription = "<true|false>",
            description = "Enable or disable stringResource replacements for Compose.",
            required = false
        )
    )

    /**
     * Processes the command-line options provided to the compiler plugin.
     *
     * This method reads the specified options and updates the compiler configuration accordingly.
     *
     * @param option The CLI option being processed.
     * @param value The string value provided for the option.
     * @param configuration The compiler configuration that is modified based on the provided option.
     */
    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        super.processOption(option, value, configuration)

        when (option.optionName) {
            KEY_ANDROID_GET_STRING_ENABLED.toString() -> configuration.put(KEY_ANDROID_GET_STRING_ENABLED, value.toBoolean())
            KEY_COMPOSE_STRING_RESOURCE_ENABLED.toString() -> configuration.put(KEY_COMPOSE_STRING_RESOURCE_ENABLED, value.toBoolean())
        }
    }

    companion object {
        /**
         * A compiler configuration key for enabling or disabling `getString` replacements.
         */
        val KEY_ANDROID_GET_STRING_ENABLED = CompilerConfigurationKey<Boolean>("tolgee.android.getString")

        /**
         * A compiler configuration key for enabling or disabling `stringResource` replacements.
         */
        val KEY_COMPOSE_STRING_RESOURCE_ENABLED = CompilerConfigurationKey<Boolean>("tolgee.compose.stringResource")

        /**
         * Retrieves the plugin configuration based on the provided compiler configuration.
         *
         * @param configuration The compiler configuration containing user-defined settings.
         * @return A [Config] instance containing the resolved plugin settings.
         */
        internal fun getConfig(configuration: CompilerConfiguration): Config {
            return Config(
                android = Config.Android(
                    getStringReplacement = configuration[KEY_ANDROID_GET_STRING_ENABLED, true]
                ),
                compose = Config.Compose(
                    stringResourceReplacement = configuration[KEY_COMPOSE_STRING_RESOURCE_ENABLED, true]
                )
            )
        }
    }
}