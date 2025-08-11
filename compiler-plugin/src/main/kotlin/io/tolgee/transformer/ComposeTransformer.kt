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
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.isInt
import org.jetbrains.kotlin.ir.util.callableId
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal class ComposeTransformer(
    private val config: Config,
    private val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {

    private val androidStringResourceCallableId = CallableId(
        packageName = FqName("androidx.compose.ui.res"),
        callableName = Name.identifier("stringResource")
    )

    private val androidPluralStringResourceCallableId = CallableId(
        packageName = FqName("androidx.compose.ui.res"),
        callableName = Name.identifier("pluralStringResource")
    )

    private val multiplatformStringResourceCallableId = CallableId(
        packageName = FqName("org.jetbrains.compose.resources"),
        callableName = Name.identifier("stringResource")
    )

    private val multiplatformPluralStringResourceCallableId = CallableId(
        packageName = FqName("org.jetbrains.compose.resources"),
        callableName = Name.identifier("pluralStringResource")
    )

    private val multiplatformStringResourceClassId = ClassId(
        packageFqName = FqName("org.jetbrains.compose.resources"),
        topLevelName = Name.identifier("StringResource")
    )

    private val multiplatformPluralStringResourceClassId = ClassId(
        packageFqName = FqName("org.jetbrains.compose.resources"),
        topLevelName = Name.identifier("PluralStringResource")
    )

    private val tolgeeStringResourceFunctions = pluginContext.referenceFunctions(
        CallableId(
            packageName = FqName("io.tolgee"),
            callableName = Name.identifier("stringResource")
        )
    )

    private val tolgeePluralStringResourceFunctions = pluginContext.referenceFunctions(
        CallableId(
            packageName = FqName("io.tolgee"),
            callableName = Name.identifier("pluralStringResource")
        )
    )

    override fun visitCall(expression: IrCall): IrExpression {
        if (config.compose.stringResourceReplacement) {
            visitString(expression)?.let {
                return it
            }
        }

        if (config.compose.pluralStringReplacement) {
            visitPluralString(expression)?.let {
                return it
            }
        }
        return super.visitCall(expression)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun visitString(expression: IrCall): IrExpression? {
        val function = expression.symbol.owner
        if (function.callableId !in arrayOf(multiplatformStringResourceCallableId, androidStringResourceCallableId)) {
            return null
        }

        val replacement = tolgeeStringResourceFunctions.findReplacementFor(function) ?: return null
        return function.symbol.replace(replacement, expression.valueArguments)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun visitPluralString(expression: IrCall): IrExpression? {
        val function = expression.symbol.owner
        if (function.callableId !in arrayOf(multiplatformPluralStringResourceCallableId, androidPluralStringResourceCallableId)) {
            return null
        }

        val replacement = tolgeePluralStringResourceFunctions.findReplacementFor(function) ?: return null
        return function.symbol.replace(replacement, expression.valueArguments)
    }

    private fun IrSimpleFunctionSymbol.replace(replacement: IrSimpleFunctionSymbol, args: List<IrExpression?>): IrExpression {
        return DeclarationIrBuilder(pluginContext, this).irCall(replacement).apply {
            args.forEachIndexed { index, arg ->
                arguments[index] = arg
            }
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun Collection<IrSimpleFunctionSymbol>.findReplacementFor(function: IrSimpleFunction): IrSimpleFunctionSymbol? {
        val argumentCount = function.valueParameters.size
        val funParams = function.valueParameters
        val funFirstIsPluralStringRes = funParams.firstOrNull()?.type?.isPluralStringResourceType() == true
        val funFirstIsStringRes = funParams.firstOrNull()?.type?.isStringResourceType() == true
        val funFirstIsInt = funParams.firstOrNull()?.type?.isInt() == true

        return firstOrNull { tolgeeFunction ->
            val tolgeeParams = tolgeeFunction.owner.valueParameters
            val tolgeeFirstIsPluralStringRes = tolgeeParams.firstOrNull()?.type?.isPluralStringResourceType() == true
            val tolgeeFirstIsStringRes = tolgeeParams.firstOrNull()?.type?.isStringResourceType() == true
            val tolgeeFirstIsInt = tolgeeParams.firstOrNull()?.type?.isInt() == true

            argumentCount == tolgeeParams.size &&
                tolgeeFirstIsPluralStringRes == funFirstIsPluralStringRes &&
                tolgeeFirstIsStringRes == funFirstIsStringRes &&
                tolgeeFirstIsInt == funFirstIsInt
        }
    }

    private fun IrType.isStringResourceType(): Boolean {
        return classOrNull?.owner?.classId == multiplatformStringResourceClassId
    }

    private fun IrType.isPluralStringResourceType(): Boolean {
        return classOrNull?.owner?.classId == multiplatformPluralStringResourceClassId
    }
}
