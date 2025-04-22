package io.tolgee.transformer

import io.tolgee.model.Config
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
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

    private val multiplatformStringResourceCallableId = CallableId(
        packageName = FqName("org.jetbrains.compose.resources"),
        callableName = Name.identifier("stringResource")
    )

    private val multiplatformStringResourceClassId = ClassId(
        packageFqName = FqName("org.jetbrains.compose.resources"),
        topLevelName = Name.identifier("StringResource")
    )

    private val tolgeeStringResourceFunctions = pluginContext.referenceFunctions(
        CallableId(
            packageName = FqName("io.tolgee"),
            callableName = Name.identifier("stringResource")
        )
    )

    override fun visitCall(expression: IrCall): IrExpression {
        if (!config.compose.stringResourceReplacement) {
            return super.visitCall(expression)
        }
        return replaceAndroid(expression) ?: replaceMultiplatform(expression) ?: super.visitCall(expression)
    }

    private fun replaceAndroid(expression: IrCall): IrExpression? {
        val function = expression.symbol.owner

        if (function.callableId == androidStringResourceCallableId) {
            val argumentCount = function.valueParameters.size
            val replacementMethod = tolgeeStringResourceFunctions.firstOrNull { tolgeeFunction ->
                val tolgeeParams = tolgeeFunction.owner.valueParameters
                val tolgeeFirstIsInt = tolgeeParams.firstOrNull()?.type?.isInt() == true
                argumentCount == tolgeeParams.size && tolgeeFirstIsInt
            } ?: return null

            return DeclarationIrBuilder(pluginContext, function.symbol).irCall(replacementMethod).apply {
                expression.valueArguments.forEachIndexed { index, arg ->
                    putValueArgument(index, arg)
                }
            }
        }
        return null
    }

    private fun replaceMultiplatform(expression: IrCall): IrExpression? {
        val function = expression.symbol.owner

        if (function.callableId == multiplatformStringResourceCallableId) {
            val argumentCount = function.valueParameters.size
            val replacementMethod = tolgeeStringResourceFunctions.firstOrNull { tolgeeFunction ->
                val tolgeeParams = tolgeeFunction.owner.valueParameters
                val tolgeeFirstIsStringRes = tolgeeParams.firstOrNull()?.type?.isStringResourceType() == true
                argumentCount == tolgeeParams.size && tolgeeFirstIsStringRes
            } ?: return null

            return DeclarationIrBuilder(pluginContext, function.symbol).irCall(replacementMethod).apply {
                expression.valueArguments.forEachIndexed { index, arg ->
                    putValueArgument(index, arg)
                }
            }
        }
        return null
    }

    private fun IrType.isStringResourceType(): Boolean {
        return classOrNull?.owner?.classId == multiplatformStringResourceClassId
    }

}