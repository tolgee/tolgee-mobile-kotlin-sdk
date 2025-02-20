package dev.datlag.tolgee

import com.google.auto.service.AutoService
import dev.datlag.tolgee.transformer.AndroidTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

/**
 * Registers the Tolgee Kotlin compiler plugin.
 *
 * This component registrar is responsible for enabling the plugin and registering
 * IR transformations based on the provided compiler configuration.
 *
 * @see CompilerPluginRegistrar
 */
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class TolgeeComponentRegistrar : CompilerPluginRegistrar() {

    /**
     * Indicates that this compiler plugin supports the K2 compiler.
     */
    override val supportsK2: Boolean
        get() = true

    /**
     * Registers the necessary extensions for IR transformation.
     *
     * This method retrieves the plugin configuration and registers an [IrGenerationExtension]
     * that applies the [AndroidTransformer] to modify the IR of the compiled module.
     *
     * @param configuration The compiler configuration containing plugin settings.
     */
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val config = TolgeeCommandLineProcessor.getConfig(configuration)

        IrGenerationExtension.registerExtension(object : IrGenerationExtension {
            override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
                moduleFragment.transform(
                    transformer = AndroidTransformer(config, pluginContext),
                    data = null
                )
            }
        })
    }
}