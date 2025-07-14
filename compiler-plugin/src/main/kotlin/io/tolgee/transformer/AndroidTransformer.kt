package io.tolgee.transformer

import io.tolgee.model.Config
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.isInt
import org.jetbrains.kotlin.ir.util.callableId
import org.jetbrains.kotlin.ir.util.isSubtypeOfClass
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Transforms IR calls to `Context.getString`, replacing them with `getStringInstant`
 * from `io.tolgee.common` where applicable.
 *
 * This transformation ensures that calls to `getString` are intercepted and redirected
 * to the Tolgee localization method if:
 * - The call originates from a `Context` or any subclass (e.g., `Activity`).
 * - The function being called overrides `Context.getString`.
 * - A suitable replacement function with a matching parameter structure is available.
 *
 * This transformation is applied during IR lowering as part of the Kotlin compiler plugin.
 *
 * @property config The plugin configuration, used to determine whether transformations should be applied.
 * @property pluginContext The IR plugin context, used to resolve function and class references.
 */
internal class AndroidTransformer(
    private val config: Config,
    private val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {

    /**
     * Represents the `android.content.Context` class ID used for identifying the `Context` class in the IR.
     */
    private val contextClassId = ClassId(
        packageFqName = FqName("android.content"),
        topLevelName = Name.identifier("Context"),
    )

    private val resourcesClassId = ClassId(
        packageFqName = FqName("android.content.res"),
        topLevelName = Name.identifier("Resources"),
    )

    /**
     * A reference to the `Context` class in the IR, retrieved using [contextClassId].
     */
    private val contextClass = pluginContext.referenceClass(contextClassId)

    private val resourcesClass = pluginContext.referenceClass(resourcesClassId)

    /**
     * Represents the `CallableId` for the `Context.getString` method.
     */
    private val contextGetStringCallableId = CallableId(
        classId = contextClassId,
        callableName = Name.identifier("getString"),
    )

    private val resourcesPluralStringCallableId = CallableId(
        classId = resourcesClassId,
        callableName = Name.identifier("getQuantityString"),
    )

    /**
     * Retrieves references to the `getStringInstant` functions from the `io.tolgee.common` package.
     * These functions are intended as replacements for `Context.getString`.
     */
    private val tolgeeGetStringFunctions = pluginContext.referenceFunctions(
        CallableId(
            packageName = FqName("io.tolgee.common"),
            callableName = Name.identifier("getStringInstant")
        )
    )

    private val tolgeePluralStringFunctions = pluginContext.referenceFunctions(
        CallableId(
            packageName = FqName("io.tolgee.common"),
            callableName = Name.identifier("getQuantityStringInstant")
        )
    )

    /**
     * Visits an `IrCall` expression and replaces calls to `Context.getString` with `getStringInstant`
     * from `io.tolgee.common`, if the plugin configuration enables this transformation.
     *
     * The method checks whether the function being called belongs to a subclass of `Context`.
     * If so, and if the function overrides `getString`, it attempts to replace it with an appropriate
     * `getStringInstant` function, ensuring that the first parameter is an integer (`@StringRes resId`)
     * and that the number of parameters matches.
     *
     * @param expression The `IrCall` expression being visited.
     * @return The transformed `IrExpression`, replacing `getString` with `getStringInstant` where applicable.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitCall(expression: IrCall): IrExpression {
        if (config.android.getStringReplacement) {
            visitContext(expression)?.let {
                return it
            }
        }

        if (config.android.pluralStringReplacement) {
            visitResources(expression)?.let {
                return it
            }
        }

        return super.visitCall(expression)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun visitContext(expression: IrCall): IrExpression? {
        val context = contextClass ?: return null
        val contextReceiver = expression.getReceiver(context) ?: return null

        val function = expression.symbol.owner
        if (!function.isOverrideOf(contextGetStringCallableId)) {
            return null
        }

        // FIXME: Shouldn't we fail if we can't replace a call?
        val tolgeeMethod = tolgeeGetStringFunctions.findReplacementFor(function) ?: return null

        return function.symbol.replace(tolgeeMethod, contextReceiver, expression.valueArguments)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun visitResources(expression: IrCall): IrExpression? {
        val resources = resourcesClass ?: return null
        val resourcesReceiver = expression.getReceiver(resources) ?: return null

        val function = expression.symbol.owner
        if (!function.isOverrideOf(resourcesPluralStringCallableId)) {
            return null
        }

        // FIXME: Shouldn't we fail if we can't replace a call?
        val tolgeeMethod = tolgeePluralStringFunctions.findReplacementFor(function) ?: return null

        return function.symbol.replace(tolgeeMethod, resourcesReceiver, expression.valueArguments)
    }


    private fun IrSimpleFunctionSymbol.replace(replacement: IrSimpleFunctionSymbol, receiver: IrExpression, args: List<IrExpression?>): IrExpression {
        return DeclarationIrBuilder(pluginContext, this).irCall(replacement).apply {
            extensionReceiver = receiver

            args.forEachIndexed { index, arg ->
                arguments[index] = arg
            }
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun Collection<IrSimpleFunctionSymbol>.findReplacementFor(function: IrSimpleFunction): IrSimpleFunctionSymbol? {
        return firstOrNull {
            val resIdFirst = it.owner.valueParameters.firstOrNull()?.type?.isInt() == true
            resIdFirst && it.owner.valueParameters.size == function.valueParameters.size
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrCall.getReceiver(clazz: IrClassSymbol): IrExpression? {
        val function = symbol.owner

        if (function.dispatchReceiverParameter?.type?.isSubtypeOfClass(clazz) == true) {
            return dispatchReceiver
        }
        if (function.extensionReceiverParameter?.type?.isSubtypeOfClass(clazz) == true) {
            return extensionReceiver
        }
        return null
    }

    /**
     * Checks whether this [IrSimpleFunction] overrides a function identified by the given [targetCallableId].
     *
     * This function recursively traverses the overridden function hierarchy to determine if any of the inherited
     * functions match the given [targetCallableId]. It avoids infinite recursion by keeping track of visited functions.
     *
     * @receiver The function to check for overriding relationships.
     * @param targetCallableId The [CallableId] of the target function that we want to check against.
     * @return `true` if this function or any of its overridden functions match the [targetCallableId], otherwise `false`.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrSimpleFunction.isOverrideOf(targetCallableId: CallableId): Boolean {
        fun IrSimpleFunction.overridesRecursive(visited: MutableSet<IrSimpleFunction>): Boolean {
            val targeting = runCatching { this.callableId == targetCallableId }.getOrNull() ?: false
            if (targeting) {
                return true
            }
            if (!visited.add(this)) {
                return false
            }

            return overriddenSymbols.any { overriddenFunction ->
                val owner = runCatching { overriddenFunction.owner }.getOrNull() ?: return@any false
                val same = runCatching { owner.callableId == targetCallableId }.getOrNull() ?: false

                same || owner.overridesRecursive(visited)
            }
        }

        return this.overridesRecursive(mutableSetOf())
    }

}