package dev.datlag.tolgee.kormatter.utils

import dev.datlag.tolgee.kormatter.FLAG_REUSE_ARGUMENT
import dev.datlag.tolgee.kormatter.NoSuchArgumentException
import dev.datlag.tolgee.kormatter.utils.internal.ArgumentIndexHolder
import dev.datlag.tooling.async.scopeCatching

class ArgumentTaker internal constructor(
    private val holder: ArgumentIndexHolder,
    private val args: Array<out Any?>,
) {
    internal lateinit var formatString: FormatString

    fun take(): Any? {
        return when {
            formatString.argumentIndex != null -> {
                scopeCatching {
                    holder.lastTaken = formatString.argumentIndex!! - 1
                    args[holder.lastTaken]
                }.onFailure {
                    if (it is IndexOutOfBoundsException) {
                        throw NoSuchArgumentException(formatString, "Can't use the argument at index ${holder.lastTaken}!", it)
                    } else {
                        throw it
                    }
                }.onSuccess {
                    return it
                }.getOrNull()
            }
            FLAG_REUSE_ARGUMENT in formatString.flags -> {
                scopeCatching {
                    args[holder.lastTaken]
                }.onFailure {
                    if (it is IndexOutOfBoundsException) {
                        throw NoSuchArgumentException(formatString, "Can't reuse previously taken argument (${holder.lastTaken})!", it)
                    } else {
                        throw it
                    }
                }.onSuccess {
                    return it
                }.getOrNull()
            }
            else -> {
                holder.lastOrdinary++
                holder.lastTaken = holder.lastOrdinary

                scopeCatching {
                    args[holder.lastTaken]
                }.onFailure {
                    if (it is IndexOutOfBoundsException) {
                        throw NoSuchArgumentException(formatString, "Can't take the next ordinary argument (${holder.lastTaken})!", it)
                    } else {
                        throw it
                    }
                }.onSuccess {
                    return it
                }.getOrNull()
            }
        }
    }

    companion object
}